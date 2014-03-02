/*
 * Copyright (c)  Sofun Gaming SAS.
 * Copyright (c)  Julien Anguenot <julien@anguenot.org>
 * Copyright (c)  Julien De Preaumont <juliendepreaumont@gmail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Julien Anguenot <julien@anguenot.org> - initial API and implementation
*/

package org.sofun.platform.opta.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.local.SportServiceLocal;
import org.sofun.core.api.sport.SportService;
import org.sofun.platform.opta.api.OptaException;
import org.sofun.platform.opta.api.OptaProcessedFeed;
import org.sofun.platform.opta.api.OptaService;
import org.sofun.platform.opta.api.ejb.OptaServiceLocal;
import org.sofun.platform.opta.ftp.FTPClientWrapper;
import org.sofun.platform.opta.ftp.FTPFileFilterImpl;
import org.sofun.platform.opta.parser.bb.BB1Parser;
import org.sofun.platform.opta.parser.bb.BB9Parser;
import org.sofun.platform.opta.parser.cy.CSFParser;
import org.sofun.platform.opta.parser.cy.CY1Parser;
import org.sofun.platform.opta.parser.cy.CY40Parser;
import org.sofun.platform.opta.parser.f1.MR1Parser;
import org.sofun.platform.opta.parser.f1.MR2Parser;
import org.sofun.platform.opta.parser.f1.MR4Parser;
import org.sofun.platform.opta.parser.f1.MR5Parser;
import org.sofun.platform.opta.parser.f1.MR6Parser;
import org.sofun.platform.opta.parser.football.F01Parser;
import org.sofun.platform.opta.parser.football.F03Parser;
import org.sofun.platform.opta.parser.football.F07Parser;
import org.sofun.platform.opta.parser.football.F40Parser;
import org.sofun.platform.opta.parser.rugby.RU01Parser;
import org.sofun.platform.opta.parser.rugby.RU02Parser;
import org.sofun.platform.opta.parser.rugby.RU10Parser;
import org.sofun.platform.opta.parser.rugby.RU6Parser;
import org.sofun.platform.opta.parser.tennis.TAB1Parser;
import org.sofun.platform.opta.parser.tennis.TAB2Parser;
import org.sofun.platform.opta.parser.tennis.TAB7Parser;

/**
 * Opta Service Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(OptaServiceLocal.class)
public class OptaServiceImpl implements OptaService {

    private static final long serialVersionUID = 353940601529591907L;

    private static final Log log = LogFactory.getLog(OptaServiceImpl.class);

    private static final String OPTA_PROPERTIES_PATH = "/opta.properties";

    private static transient Properties properties = new Properties();

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    private transient EntityManager em;

    @EJB(beanName = "SportServiceImpl", beanInterface = SportServiceLocal.class)
    private SportService sports;

    private final SecureRandom randomGenerator = new SecureRandom();

    public OptaServiceImpl() {
        super();
    }

    public OptaServiceImpl(EntityManager em, SportService sports) {
        this();
        this.em = em;
        this.sports = sports;
    }

    @Override
    public void syncF() throws OptaException {
        int processed = 0;
        for (Map.Entry<Integer, String[]> entry : getFootballSeasonsByCompetitions()
                .entrySet()) {
            final Integer compId = entry.getKey();
            final String[] seasons = entry.getValue();
            for (int i = 0; i < seasons.length; i++) {
                if (processed > 10) {
                    // batch to keep transactions small. These feeds are rather
                    // large.
                    // mostly an issue while bootstrapping new competitions with
                    // lots of pending feeds to process.
                    log.info("End of Football feeds processing batch. Will resume in 5 minutes.");
                    return;
                }
                // sync up all feeds (except live feeds) for a given competition
                // in a given season. (F40, F1, F3)
                processed += syncFComp(String.valueOf(compId), seasons[i]);
            }
        }
    }

    @Override
    public void syncRU() throws OptaException {
        int processed = 0;
        for (Map.Entry<Integer, String[]> entry : getRugbySeasonsByCompetitions()
                .entrySet()) {
            final Integer compId = entry.getKey();
            final String[] seasons = entry.getValue();
            for (int i = 0; i < seasons.length; i++) {
                if (processed >= 12) {
                    // batch to keep transactions small. These feeds are rather
                    // large.
                    // mostly an issue while bootstrapping new competitions with
                    // lots of pending feeds to process.
                    log.info("End of Rugby feeds processing batch. Will resume in 5 minutes.");
                    return;
                }
                // sync up all feeds (except live feeds) for a given competition
                // in a given season. (F40, F1, F3)
                processed += syncRUComp(String.valueOf(compId), seasons[i]);
            }
        }
    }

    /**
     * Returns an initialized FTP Client wrapper to access Opta feeds.
     * 
     * @return {@link FTPClientWrapper} instance.
     */
    private FTPClientWrapper getFTPClientWrapper() {
        return new FTPClientWrapper(
                getOptaProperty(OptaService.PROP_FTP_HOST_KEY),
                Integer.valueOf(getOptaProperty(OptaService.PROP_FTP_PORT_KEY)),
                getOptaProperty(PROP_FTP_USER_KEY),
                getOptaProperty(PROP_FTP_PWD_KEY));
    }

    @Override
    public void syncFLiveFeeds() throws OptaException {
        final String liveFeedPattern = "srml-.*-.*-f.*-matchresults.xml";
        int processed = 0;
        final FTPClientWrapper opta = getFTPClientWrapper();
        try {
            opta.connect();
            final FTPFileFilter filter = new FTPFileFilterImpl(liveFeedPattern,
                    this);
            for (FTPFile ftpFile : opta.getClient().listFiles(null, filter)) {
                if (processed > 50) {
                    // batch to keep transactions small.
                    // mostly an issue while bootstrapping new competitions with
                    // lots of pending feeds to process.
                    log.info("End of Football Live Feeds processing batch. Will resume in 5 minutes.");
                    break;
                }
                File feed = getFileFromFTP(opta, ftpFile.getName());
                F07Parser f7p = f7Sync(feed);
                if (f7p.getGame() != null) {
                    log.info("Syncing file with name=" + ftpFile.getName());
                    setFileAsProcessed(ftpFile.getName(), ftpFile
                            .getTimestamp().getTime());
                    processed += 1;
                } else {
                    log.warn("Could not find game for live feed with name="
                            + ftpFile.getName());
                }
            }
        } catch (IOException e) {
            throw new OptaException(e.getMessage());
        } finally {
            opta.disconnect();
        }
    }

    @Override
    public RU10Parser ru10Sync(File file) {
        return new RU10Parser(file, sports, em);
    }

    @Override
    public RU02Parser ru2Sync(File file) {
        return new RU02Parser(file, sports, em);
    }

    @Override
    public RU01Parser ru1Sync(File file) {
        return new RU01Parser(file, sports, em);
    }

    @Override
    public RU6Parser ru6Sync(File file) throws OptaException {
        return new RU6Parser(file, sports, em);
    }

    @Override
    public F01Parser f1Sync(File file) throws OptaException {
        return new F01Parser(file, sports, em);

    }

    @Override
    public F03Parser f3Sync(File file) throws OptaException {
        return new F03Parser(file, sports, em);

    }

    @Override
    public F07Parser f7Sync(File file) throws OptaException {
        return new F07Parser(file, sports, em);

    }

    @Override
    public F40Parser f40Sync(File file) throws OptaException {
        return new F40Parser(file, sports, em);

    }

    @Override
    public OptaProcessedFeed getProcessedFeedFor(String filename) {
        final String queryStr = "from "
                + OptaProcessedFeedImpl.class.getSimpleName()
                + " f where f.fileName =:filename";
        Query query = em.createQuery(queryStr);
        query.setParameter("filename", filename);
        try {
            return (OptaProcessedFeed) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Creates and returns a local files given a remote FTP file.
     * 
     * @param opta: Opta FTP instance
     * @param fileName: remote filename to get
     * @return a File instance.
     * @throws OptaException
     */
    private File getFileFromFTP(FTPClientWrapper opta, String fileName)
            throws OptaException {
        File temp = null;
        boolean c = true;
        try {
            final String tempFileName = fileName + "_"
                    + String.valueOf(randomGenerator.nextInt(1000000));
            temp = File.createTempFile(tempFileName, ".tmp");
            OutputStream out = new FileOutputStream(temp);
            OutputStream r = opta.getFile(out, fileName);
            if (r == null) {
                c = false;
            }
            out.close();
        } catch (IOException e) {
            throw new OptaException(e.getMessage());
        } finally {
            if (temp != null) {
                temp.deleteOnExit();
            }
        }
        if (!c) {
            return null;
        }
        return temp;
    }

    /**
     * Mark a feed as processed
     * 
     * @param filename: a filename.
     */
    private void setFileAsProcessed(String filename, Date timestamp) {
        OptaProcessedFeed feed = getProcessedFeedFor(filename);
        if (feed == null) {
            feed = new OptaProcessedFeedImpl(filename, timestamp);
            em.persist(feed);
        } else {
            feed.setLastUpdated(new Date());
            feed.setTimestamp(timestamp);
            em.merge(feed);
        }
    }

    /**
     * Synchronize all F feeds for a given competition's season (F1, F3 and F40)
     * 
     * <p>
     * 
     * This method is responsible for the actual update of feeds if they do
     * require it.
     * 
     * @param competitionId: the competition ID
     * @param season: the season label.
     * @return the amount processed files.
     * @throws OptaException
     */
    private int syncFComp(String competitionId, String season)
            throws OptaException {

        final String f1 = "srml-" + competitionId + "-" + season
                + "-results.xml";
        final String f3 = "srml-" + competitionId + "-" + season
                + "-standings.xml";
        final String f40 = "srml-" + competitionId + "-" + season
                + "-squads.xml";

        int processed = 0;

        final FTPClientWrapper opta = getFTPClientWrapper();
        try {

            opta.connect();

            // Processing F40 (order matters for first run)
            OptaProcessedFeed pfeed = getProcessedFeedFor(f40);
            FTPFile ftpFile = null;
            boolean process = false;
            FTPFile[] ftpfiles;
            try {
                ftpfiles = opta.getClient().listFiles(f40);
            } catch (IOException e) {
                throw new OptaException(e.getMessage());
            }
            if (ftpfiles.length == 1) {
                ftpFile = ftpfiles[0];
            }
            if (pfeed == null) {
                process = true;
            } else {
                if (ftpFile != null) {
                    final Date formerFeedTimestamp = pfeed.getTimestamp();
                    final Date newFeedTimestamp = ftpFile.getTimestamp()
                            .getTime();
                    if (newFeedTimestamp == null
                            || newFeedTimestamp.compareTo(formerFeedTimestamp) > 0) {
                        process = true;
                    } else {
                        log.debug("Feed has not changed. No need to reprocess feed="
                                + f40);
                    }
                } else {
                    log.warn("Feed with name=" + f40 + " cannot be found");
                }
            }

            if (process && ftpFile != null) {
                File feed = getFileFromFTP(opta, f40);
                if (feed != null) {
                    log.info("Syncing file with name=" + f40);
                    f40Sync(feed);
                    setFileAsProcessed(f40, ftpFile.getTimestamp().getTime());
                    processed += 1;
                } else {
                    log.warn("Feed not available:" + f40);
                }
            }

            // Processing F1
            pfeed = getProcessedFeedFor(f1);
            ftpFile = null;
            process = false;
            try {
                ftpfiles = opta.getClient().listFiles(f1);
            } catch (IOException e) {
                throw new OptaException(e.getMessage());
            }
            if (ftpfiles.length == 1) {
                ftpFile = ftpfiles[0];
            }
            if (pfeed == null) {
                process = true;
            } else {
                if (ftpFile != null) {
                    final Date formerFeedTimestamp = pfeed.getTimestamp();
                    final Date newFeedTimestamp = ftpFile.getTimestamp()
                            .getTime();
                    if (newFeedTimestamp == null
                            || newFeedTimestamp.compareTo(formerFeedTimestamp) > 0) {
                        process = true;
                    } else {
                        log.debug("Feed has not changed. No need to reprocess feed="
                                + f1);
                    }
                } else {
                    log.warn("Feed with name=" + f1 + " cannot be found");
                }
            }

            if (process && ftpFile != null) {
                File feed = getFileFromFTP(opta, f1);
                if (feed != null) {
                    log.info("Syncing file with name=" + f1);
                    f1Sync(feed);
                    setFileAsProcessed(f1, ftpFile.getTimestamp().getTime());
                    processed += 1;
                } else {
                    log.warn("Feed not available:" + f1);
                }
            }

            // Processing F3
            pfeed = getProcessedFeedFor(f3);
            process = false;
            ftpFile = null;
            try {
                ftpfiles = opta.getClient().listFiles(f3);
            } catch (IOException e) {
                throw new OptaException(e.getMessage());
            }
            if (ftpfiles.length == 1) {
                ftpFile = ftpfiles[0];
            }
            if (pfeed == null) {
                process = true;
            } else {
                if (ftpFile != null) {
                    final Date formerFeedTimestamp = pfeed.getTimestamp();
                    final Date newFeedTimestamp = ftpFile.getTimestamp()
                            .getTime();
                    if (newFeedTimestamp == null
                            || newFeedTimestamp.compareTo(formerFeedTimestamp) > 0) {
                        process = true;
                    } else {
                        log.debug("Feed has not changed. No need to reprocess feed="
                                + f3);
                    }
                } else {
                    log.warn("Feed with name=" + f3 + " cannot be found");
                }
            }

            if (process && ftpFile != null) {
                File feed = getFileFromFTP(opta, f3);
                if (feed != null) {
                    log.info("Syncing file with name=" + f3);
                    f3Sync(feed);
                    setFileAsProcessed(f3, ftpFile.getTimestamp().getTime());
                    processed += 1;
                } else {
                    log.warn("Feed not available:" + f3);
                }
            }

        } finally {
            opta.disconnect();
        }

        return processed;

    }

    /**
     * Synchronize all RU feeds for a given competition's season (RU1, RU2 and
     * RU2)
     * 
     * <p>
     * 
     * This method is responsible for the actual update of feeds if they do
     * require it.
     * 
     * @param competitionId: the competition ID
     * @param season: the season label.
     * @return the amount processed files.
     * @throws OptaException
     */
    private int syncRUComp(String competitionId, String season)
            throws OptaException {

        final String ru1Pattern = String.format(
                "ru1_compfixtures.(%s).(%s).*.xml", competitionId, season);
        final String ru2Pattern = String.format("ru2_tables.(%s).(%s).xml",
                competitionId, season);
        final String ru10Pattern = String.format("ru10_comp-(%s).xml",
                competitionId);

        int processed = 0;
        final FTPClientWrapper opta = getFTPClientWrapper();
        try {

            opta.connect();

            // RU1 first (order matters)
            FTPFileFilter filter = new FTPFileFilterImpl(ru1Pattern, this);
            for (FTPFile ftpFile : opta.getClient().listFiles(null, filter)) {
                File feed = getFileFromFTP(opta, ftpFile.getName());
                ru1Sync(feed);
                log.info("Syncing file with name=" + ftpFile.getName());
                setFileAsProcessed(ftpFile.getName(), ftpFile.getTimestamp()
                        .getTime());
                processed += 1;
                if (processed >= 50) {
                    // batch to keep transactions small. These feeds are rather
                    // large.
                    // mostly an issue while bootstrapping new competitions with
                    // lots of pending feeds to process.
                    break;
                }
            }

            // RU10
            // Only one RU10 per competition
            filter = new FTPFileFilterImpl(ru10Pattern, this);
            for (FTPFile ftpFile : opta.getClient().listFiles(null, filter)) {
                File feed = getFileFromFTP(opta, ftpFile.getName());
                ru10Sync(feed);
                log.info("Syncing file with name=" + ftpFile.getName());
                setFileAsProcessed(ftpFile.getName(), ftpFile.getTimestamp()
                        .getTime());
                processed += 1;
            }

            // RU2
            // Only one RU2 per competition
            filter = new FTPFileFilterImpl(ru2Pattern, this);
            for (FTPFile ftpFile : opta.getClient().listFiles(null, filter)) {
                File feed = getFileFromFTP(opta, ftpFile.getName());
                ru2Sync(feed);
                log.info("Syncing file with name=" + ftpFile.getName());
                setFileAsProcessed(ftpFile.getName(), ftpFile.getTimestamp()
                        .getTime());
                processed += 1;
            }

        } catch (IOException e) {
            throw new OptaException(e.getMessage());
        } finally {
            opta.disconnect();
        }

        return processed;

    }

    @Override
    public void syncRULiveFeeds() throws OptaException {
        final String liveFeedPattern = "ru6_wapresults.*.*.xml";
        int processed = 0;
        final FTPClientWrapper opta = getFTPClientWrapper();
        try {
            opta.connect();
            final FTPFileFilter filter = new FTPFileFilterImpl(liveFeedPattern,
                    this);
            for (FTPFile ftpFile : opta.getClient().listFiles(null, filter)) {
                if (processed > 50) {
                    // batch to keep transactions small.
                    // mostly an issue while bootstrapping new competitions with
                    // lots of pending feeds to process.
                    log.info("End of Rugby Live Feeds processing batch. Will resume in 5 minutes.");
                    break;
                }
                File feed = getFileFromFTP(opta, ftpFile.getName());
                RU6Parser ru6p = ru6Sync(feed);
                if (ru6p.getGame() != null) {
                    log.info("Syncing file with name=" + ftpFile.getName());
                    setFileAsProcessed(ftpFile.getName(), ftpFile
                            .getTimestamp().getTime());
                    processed += 1;
                } else {
                    log.warn("Could not find game for live feed with name="
                            + ftpFile.getName());
                }
            }
        } catch (IOException e) {
            throw new OptaException(e.getMessage());
        } finally {
            opta.disconnect();
        }
    }

    @Override
    public String getOptaProperty(String key) {

        if (properties.size() == 0) {
            InputStream raw = getClass().getResourceAsStream(
                    OPTA_PROPERTIES_PATH);
            InputStreamReader utf8Reader = null;
            try {
                utf8Reader = new InputStreamReader(raw, "UTF-8");
                if (utf8Reader != null) {
                    properties.load(raw);
                }
            } catch (UnsupportedEncodingException e) {
                log.error("A problem occured while reading the Opta properties:"
                        + e.getMessage());
                e.printStackTrace();
            } catch (IOException ioe) {
                log.error("A problem occured while reading the Opta properties:"
                        + ioe.getMessage());
                ioe.printStackTrace();
            } finally {
                try {
                    if (utf8Reader != null) {
                        utf8Reader.close();
                    }
                } catch (IOException e) {
                    // Do nothing; just spit
                    e.printStackTrace();
                }
            }
        }

        if (properties.containsKey(key)) {
            return (String) properties.get(key);
        }
        return null;

    }

    /**
     * Returns information about which seasons is available for a given football
     * competition.
     * 
     * @return a mapping from comp_id -> seasons
     */
    @Override
    public final Map<Integer, String[]> getFootballSeasonsByCompetitions() {

        Map<Integer, String[]> seasons = new HashMap<Integer, String[]>();

        seasons.put(21, new String[] { "2012" }); // F 21: Liga (IT)
        seasons.put(22, new String[] { "2012" }); // F 22: Bundesliga (DE)
        seasons.put(23, new String[] { "2012" }); // F 23: Seria A (ES)
        seasons.put(24, new String[] { "2012" }); // F 24: L1 (FR)
        seasons.put(104, new String[] { "2012" }); // F 104: L2 (FR)
        seasons.put(8, new String[] { "2012" }); // F 8: PR (UK)

        seasons.put(5, new String[] { "2012" }); // F 5: EUFA CL
        seasons.put(6, new String[] { "2012" }); // F 6: EUFA Europe League
        seasons.put(88, new String[] { "2012" }); // F 88: EDF friendly
        seasons.put(362, new String[] { "2012" }); // F 362: French Coupe de la
                                                   // ligue FR
        seasons.put(336, new String[] { "112014" }); // F 336: European World
                                                     // Cup
                                                     // Qualifiers

        // seasons.put(3, new String[] {}); // F 3: EURO
        // seasons.put(190, new String[] {}); // F 190: CAN
        // seasons.put(235, new String[] {}); // F 235: Euro (qualifications)

        return seasons;

    }

    /**
     * Returns information about which seasons is available for a given rugby
     * competition.
     * 
     * @return a mapping from comp_id -> seasons
     */
    @Override
    public final Map<Integer, String[]> getRugbySeasonsByCompetitions() {

        Map<Integer, String[]> seasons = new HashMap<Integer, String[]>();

        seasons.put(202, new String[] { "2012", "2013" }); // H-Kup
        seasons.put(209, new String[] { "2012", "2013" }); // Six Nations
        seasons.put(214, new String[] { "2012", "2013" }); // Tri Nations
        seasons.put(203, new String[] { "2012", "2013" }); // Top 14
        // seasons.put(210, new String[] { "" }); // WC

        return seasons;

    }

    @Override
    public List<String> getFormula1Seasons() {

        List<String> seasons = new ArrayList<String>();
        seasons.add("2012");
        return seasons;

    }

    /**
     * 
     * @param season
     * @return
     * @throws OptaException
     */
    private int syncFormula1Season(String season) throws OptaException {

        final String mr4Pattern = String.format("F1_STANDINGS_DRIVER_(%s).xml",
                season);
        final String mr5Pattern = String.format("F1_STANDINGS_TEAMS_(%s).xml",
                season);
        final String mr6Pattern = String.format("F1_CALENDAR_(%s).xml", season);
        final String mr2QualiPattern = "F1_QUALI_.*_NT.xml";
        final String mr2FPPattern = "F1_FP.*_NT.xml";
        final String mr1Pattern = "F1_RACE_.*_NT.xml";

        int processed = 0;
        final FTPClientWrapper opta = getFTPClientWrapper();
        try {

            opta.connect();

            // MR6 first first (order matters)
            FTPFileFilter filter = new FTPFileFilterImpl(mr6Pattern, this);
            for (FTPFile ftpFile : opta.getClient().listFiles(null, filter)) {
                File feed = getFileFromFTP(opta, ftpFile.getName());
                mr6Sync(feed);
                log.info("Syncing file with name=" + ftpFile.getName());
                setFileAsProcessed(ftpFile.getName(), ftpFile.getTimestamp()
                        .getTime());
                processed += 1;
                if (processed >= 50) {
                    // batch to keep transactions small. These feeds are rather
                    // large.
                    // mostly an issue while bootstrapping new competitions with
                    // lots of pending feeds to process.
                    break;
                }
            }

            // MR5
            filter = new FTPFileFilterImpl(mr5Pattern, this);
            for (FTPFile ftpFile : opta.getClient().listFiles(null, filter)) {
                File feed = getFileFromFTP(opta, ftpFile.getName());
                mr5Sync(feed);
                log.info("Syncing file with name=" + ftpFile.getName());
                setFileAsProcessed(ftpFile.getName(), ftpFile.getTimestamp()
                        .getTime());
            }

            // MR4
            filter = new FTPFileFilterImpl(mr4Pattern, this);
            for (FTPFile ftpFile : opta.getClient().listFiles(null, filter)) {
                File feed = getFileFromFTP(opta, ftpFile.getName());
                mr4Sync(feed);
                log.info("Syncing file with name=" + ftpFile.getName());
                setFileAsProcessed(ftpFile.getName(), ftpFile.getTimestamp()
                        .getTime());
            }

            // MR2
            filter = new FTPFileFilterImpl(mr2QualiPattern, this);
            for (FTPFile ftpFile : opta.getClient().listFiles(null, filter)) {
                File feed = getFileFromFTP(opta, ftpFile.getName());
                mr2Sync(feed);
                log.info("Syncing file with name=" + ftpFile.getName());
                setFileAsProcessed(ftpFile.getName(), ftpFile.getTimestamp()
                        .getTime());
            }
            filter = new FTPFileFilterImpl(mr2FPPattern, this);
            for (FTPFile ftpFile : opta.getClient().listFiles(null, filter)) {
                File feed = getFileFromFTP(opta, ftpFile.getName());
                mr2Sync(feed);
                log.info("Syncing file with name=" + ftpFile.getName());
                setFileAsProcessed(ftpFile.getName(), ftpFile.getTimestamp()
                        .getTime());
            }

            // MR1
            filter = new FTPFileFilterImpl(mr1Pattern, this);
            for (FTPFile ftpFile : opta.getClient().listFiles(null, filter)) {
                File feed = getFileFromFTP(opta, ftpFile.getName());
                mr1Sync(feed);
                log.info("Syncing file with name=" + ftpFile.getName());
                setFileAsProcessed(ftpFile.getName(), ftpFile.getTimestamp()
                        .getTime());
            }

        } catch (IOException e) {
            throw new OptaException(e.getMessage());
        } finally {
            opta.disconnect();
        }

        return processed;
    }

    @Override
    public void syncFormula1() throws OptaException {
        for (String season : getFormula1Seasons()) {
            syncFormula1Season(season);
        }
    }

    @Override
    public MR1Parser mr1Sync(File file) throws OptaException {
        return new MR1Parser(file, sports, em);
    }

    @Override
    public MR2Parser mr2Sync(File file) throws OptaException {
        return new MR2Parser(file, sports, em);
    }

    @Override
    public MR4Parser mr4Sync(File file) throws OptaException {

        // Season label is not included in the feed itself. We need to extract
        // it from the filename. The File naming convention used for this feed
        // is the following: F1_CALENDAR_<YEAR>.xml
        final String fileName = file.getName();
        String year;
        String type;
        if (fileName.contains("LIVE")) {
            type = MR4Parser.TABLE_TYPE_LIVE;
            year = fileName.substring("F1_STANDINGS_DRIVER_LIVE_".length(),
                    "F1_STANDINGS_DRIVER_LIVE_".length() + 4);
        } else {
            year = fileName.substring("F1_STANDINGS_DRIVER_".length(),
                    "F1_STANDINGS_DRIVER_".length() + 4);
            type = MR4Parser.TABLE_TYPE_POST_RACE;
        }
        return new MR4Parser(file, sports, em, year, type);

    }

    @Override
    public MR5Parser mr5Sync(File file) throws OptaException {
        // Season label is not included in the feed itself. We need to extract
        // it from the filename. The File naming convention used for this feed
        // is the following: F1_CALENDAR_<YEAR>.xml
        final String fileName = file.getName();
        String year;
        String type;
        if (fileName.contains("LIVE")) {
            year = fileName.substring("F1_STANDINGS_TEAMS_LIVE_".length(),
                    "F1_STANDINGS_TEAMS_LIVE_".length() + 4);
            type = MR5Parser.TABLE_TYPE_LIVE;
        } else {
            year = fileName.substring("F1_STANDINGS_TEAMS_".length(),
                    "F1_STANDINGS_TEAMS_".length() + 4);
            type = MR5Parser.TABLE_TYPE_POST_RACE;
        }
        return new MR5Parser(file, sports, em, year, type);
    }

    @Override
    public MR6Parser mr6Sync(File file) throws OptaException {
        // Season label is not included in the feed itself. We need to extract
        // it from the filename. The File naming convention used for this feed
        // is the following: F1_CALENDAR_<YEAR>.xml
        final String fileName = file.getName();
        final String year = fileName.substring("F1_CALENDAR_".length(),
                "F1_CALENDAR_".length() + 4);
        return new MR6Parser(file, sports, em, year);
    }

    @Override
    public Map<Integer, String[]> getTennisSeasonsByCompetitions() {

        Map<Integer, String[]> seasons = new HashMap<Integer, String[]>();

        seasons.put(7599, new String[] { "2012" }); // RG Womens singles
        seasons.put(7595, new String[] { "2012" }); // RG Mens Singles
        seasons.put(7596, new String[] { "2012" }); // RG Mens Doubles
        seasons.put(7597, new String[] { "2012" }); // RG Womens Doubles
        seasons.put(7598, new String[] { "2012" }); // RG Mixed Doubles

        seasons.put(7609, new String[] { "2012" }); // W Womens singles
        seasons.put(7606, new String[] { "2012" }); // W Mens Singles
        seasons.put(7607, new String[] { "2012" }); // W Mens Doubles
        seasons.put(7610, new String[] { "2012" }); // W Womens Doubles
        seasons.put(7608, new String[] { "2012" }); // W Mixed Doubles

        seasons.put(7630, new String[] { "2012" }); // US Womens singles
        seasons.put(7627, new String[] { "2012" }); // US Mens Singles
        seasons.put(7628, new String[] { "2012" }); // US Mens Doubles
        seasons.put(7629, new String[] { "2012" }); // US Womens Doubles
        seasons.put(7631, new String[] { "2012" }); // US Mixed Doubles

        return seasons;

    }

    @Override
    public TAB1Parser tab1Sync(File file) throws OptaException {
        return new TAB1Parser(file, sports, em);
    }

    @Override
    public TAB2Parser tab2Sync(File file) throws OptaException {
        return new TAB2Parser(file, sports, em);
    }

    @Override
    public TAB7Parser tab7Sync(File file) throws OptaException {
        return new TAB7Parser(file, sports, em);
    }

    @Override
    public void syncTennis() throws OptaException {

        int processed = 0;
        for (Map.Entry<Integer, String[]> entry : getTennisSeasonsByCompetitions()
                .entrySet()) {
            final Integer compId = entry.getKey();
            final String[] seasons = entry.getValue();
            for (int i = 0; i < seasons.length; i++) {
                if (processed > 10) {
                    // batch to keep transactions small. These feeds are rather
                    // large. mostly an issue while bootstrapping new
                    // competitions with lots of pending feeds to process.
                    log.info("End of Tennis feeds processing batch. Will resume in 5 minutes.");
                    return;
                }
                // sync up all feeds (except live feeds) for a given competition
                // in a given season.
                processed += syncTennisComp(String.valueOf(compId), seasons[i]);
            }
        }

    }

    /**
     * Synchronize all Tennis feeds for a given competition's season (TAB1,
     * TAB2)
     * 
     * <p>
     * 
     * This method is responsible for the actual update of feeds if they do
     * require it.
     * 
     * @param competitionId: the competition ID
     * @param season: the season label.
     * @return the amount processed files.
     * @throws OptaException
     */
    private int syncTennisComp(String competitionId, String season)
            throws OptaException {

        final String tab1 = "TAB1-" + season + ".xml";
        final String tab2 = "TAB2-" + competitionId + "-" + season + ".xml";

        int processed = 0;

        final FTPClientWrapper opta = getFTPClientWrapper();
        try {

            opta.connect();

            // Processing TAB1 (order matters for first run)
            OptaProcessedFeed pfeed = getProcessedFeedFor(tab1);
            FTPFile ftpFile = null;
            boolean process = false;
            FTPFile[] ftpfiles;
            try {
                ftpfiles = opta.getClient().listFiles(tab1);
            } catch (IOException e) {
                throw new OptaException(e.getMessage());
            }
            if (ftpfiles.length == 1) {
                ftpFile = ftpfiles[0];
            }
            if (pfeed == null) {
                process = true;
            } else {
                if (ftpFile != null) {
                    final Date formerFeedTimestamp = pfeed.getTimestamp();
                    final Date newFeedTimestamp = ftpFile.getTimestamp()
                            .getTime();
                    if (newFeedTimestamp == null
                            || newFeedTimestamp.compareTo(formerFeedTimestamp) > 0) {
                        process = true;
                    } else {
                        log.debug("Feed has not changed. No need to reprocess feed="
                                + tab1);
                    }
                } else {
                    log.warn("Feed with name=" + tab1 + " cannot be found");
                }
            }

            if (process && ftpFile != null) {
                File feed = getFileFromFTP(opta, tab1);
                if (feed != null) {
                    log.info("Syncing file with name=" + tab1);
                    tab1Sync(feed);
                    setFileAsProcessed(tab1, ftpFile.getTimestamp().getTime());
                    processed += 1;
                } else {
                    log.warn("Feed not available:" + tab1);
                }
            }

            // Processing TAB2
            pfeed = getProcessedFeedFor(tab2);
            ftpFile = null;
            process = false;
            try {
                ftpfiles = opta.getClient().listFiles(tab2);
            } catch (IOException e) {
                throw new OptaException(e.getMessage());
            }
            if (ftpfiles.length == 1) {
                ftpFile = ftpfiles[0];
            }
            if (pfeed == null) {
                process = true;
            } else {
                if (ftpFile != null) {
                    final Date formerFeedTimestamp = pfeed.getTimestamp();
                    final Date newFeedTimestamp = ftpFile.getTimestamp()
                            .getTime();
                    if (newFeedTimestamp == null
                            || newFeedTimestamp.compareTo(formerFeedTimestamp) > 0) {
                        process = true;
                    } else {
                        log.debug("Feed has not changed. No need to reprocess feed="
                                + tab2);
                    }
                } else {
                    log.warn("Feed with name=" + tab2 + " cannot be found");
                }
            }

            if (process && ftpFile != null) {
                File feed = getFileFromFTP(opta, tab2);
                if (feed != null) {
                    log.info("Syncing file with name=" + tab2);
                    tab2Sync(feed);
                    setFileAsProcessed(tab2, ftpFile.getTimestamp().getTime());
                    processed += 1;
                } else {
                    log.warn("Feed not available:" + tab2);
                }
            }

        } finally {
            opta.disconnect();
        }

        return processed;

    }

    @Override
    public void syncTennisLiveFeeds() throws OptaException {

        final String liveFeedPattern = "TAB7-.*.xml";
        int processed = 0;
        final FTPClientWrapper opta = getFTPClientWrapper();
        try {
            opta.connect();
            final FTPFileFilter filter = new FTPFileFilterImpl(liveFeedPattern,
                    this);
            for (FTPFile ftpFile : opta.getClient().listFiles(null, filter)) {
                if (processed > 100) {
                    // batch to keep transactions small.
                    // mostly an issue while bootstrapping new competitions with
                    // lots of pending feeds to process.
                    log.info("End of Tennis Live Feeds processing batch. Will resume in 5 minutes.");
                    break;
                }
                File feed = getFileFromFTP(opta, ftpFile.getName());
                TAB7Parser tab7p = tab7Sync(feed);
                if (tab7p.getGame() != null) {
                    log.info("Syncing file with name=" + ftpFile.getName());
                    setFileAsProcessed(ftpFile.getName(), ftpFile
                            .getTimestamp().getTime());
                    processed += 1;
                } else {
                    log.warn("Could not find game for live feed with name="
                            + ftpFile.getName());
                }
            }
        } catch (IOException e) {
            throw new OptaException(e.getMessage());
        } finally {
            opta.disconnect();
        }

    }

    @Override
    public Map<Integer, String[]> getCyclingSeasonsByCompetitions() {
        Map<Integer, String[]> seasons = new HashMap<Integer, String[]>();
        seasons.put(90002, new String[] { "2012" }); // RG Womens singles
        return seasons;
    }

    @Override
    public CY1Parser cy1Sync(File file) throws OptaException {
        return new CY1Parser(file, sports, em);
    }

    @Override
    public CY40Parser cy40sync(File file) throws OptaException {
        return new CY40Parser(file, sports, em);
    }

    @Override
    public CSFParser cyCSFCync(File file) throws OptaException {
        return new CSFParser(file, sports, em);
    }

    /**
     * Synchronize all Cycling feeds for a given competition's season (CY1,
     * CY40)
     * 
     * <p>
     * 
     * This method is responsible for the actual update of feeds if they do
     * require it.
     * 
     * @param competitionId: the competition ID
     * @param season: the season label.
     * @return the amount processed files.
     * @throws OptaException
     */
    private int syncCyclingComp(String competitionId, String season)
            throws OptaException {

        final String cy1 = "CNC_" + competitionId + ".Xml";
        final String cy40 = "CMP_" + competitionId + ".Xml";
        final String csf = "CSF_" + competitionId + "_.*.Xml";

        int processed = 0;

        final FTPClientWrapper opta = getFTPClientWrapper();
        try {

            opta.connect();

            // Processing CY1 (order matters for first run)
            OptaProcessedFeed pfeed = getProcessedFeedFor(cy1);
            FTPFile ftpFile = null;
            boolean process = false;
            FTPFile[] ftpfiles;
            try {
                ftpfiles = opta.getClient().listFiles(cy1);
            } catch (IOException e) {
                throw new OptaException(e.getMessage());
            }
            if (ftpfiles.length == 1) {
                ftpFile = ftpfiles[0];
            }
            if (pfeed == null) {
                process = true;
            } else {
                if (ftpFile != null) {
                    final Date formerFeedTimestamp = pfeed.getTimestamp();
                    final Date newFeedTimestamp = ftpFile.getTimestamp()
                            .getTime();
                    if (newFeedTimestamp == null
                            || newFeedTimestamp.compareTo(formerFeedTimestamp) > 0) {
                        process = true;
                    } else {
                        log.debug("Feed has not changed. No need to reprocess feed="
                                + cy1);
                    }
                } else {
                    log.warn("Feed with name=" + cy1 + " cannot be found");
                }
            }

            if (process && ftpFile != null) {
                File feed = getFileFromFTP(opta, cy1);
                if (feed != null) {
                    log.info("Syncing file with name=" + cy1);
                    cy1Sync(feed);
                    setFileAsProcessed(cy1, ftpFile.getTimestamp().getTime());
                    processed += 1;
                } else {
                    log.warn("Feed not available:" + cy1);
                }
            }

            // Processing CY40
            pfeed = getProcessedFeedFor(cy40);
            ftpFile = null;
            process = false;
            try {
                ftpfiles = opta.getClient().listFiles(cy40);
            } catch (IOException e) {
                throw new OptaException(e.getMessage());
            }
            if (ftpfiles.length == 1) {
                ftpFile = ftpfiles[0];
            }
            if (pfeed == null) {
                process = true;
            } else {
                if (ftpFile != null) {
                    final Date formerFeedTimestamp = pfeed.getTimestamp();
                    final Date newFeedTimestamp = ftpFile.getTimestamp()
                            .getTime();
                    if (newFeedTimestamp == null
                            || newFeedTimestamp.compareTo(formerFeedTimestamp) > 0) {
                        process = true;
                    } else {
                        log.debug("Feed has not changed. No need to reprocess feed="
                                + cy40);
                    }
                } else {
                    log.warn("Feed with name=" + cy40 + " cannot be found");
                }
            }

            if (process && ftpFile != null) {
                File feed = getFileFromFTP(opta, cy40);
                if (feed != null) {
                    log.info("Syncing file with name=" + cy40);
                    cy40sync(feed);
                    setFileAsProcessed(cy40, ftpFile.getTimestamp().getTime());
                    processed += 1;
                } else {
                    log.warn("Feed not available:" + cy40);
                }
            }

            // Processing CSF

            FTPFileFilter filter = new FTPFileFilterImpl(csf, this);
            for (FTPFile ftpf : opta.getClient().listFiles(null, filter)) {
                File feed = getFileFromFTP(opta, ftpf.getName());
                cyCSFCync(feed);
                log.info("Syncing file with name=" + ftpf.getName());
                setFileAsProcessed(ftpf.getName(), ftpf.getTimestamp()
                        .getTime());
                processed += 1;
            }

        } catch (IOException e) {
            throw new OptaException(e.getMessage());
        } finally {
            opta.disconnect();
        }

        return processed;

    }

    @Override
    public void syncCycling() throws OptaException {

        int processed = 0;
        for (Map.Entry<Integer, String[]> entry : getCyclingSeasonsByCompetitions()
                .entrySet()) {
            final Integer compId = entry.getKey();
            final String[] seasons = entry.getValue();
            for (int i = 0; i < seasons.length; i++) {
                if (processed > 10) {
                    // batch to keep transactions small. These feeds are rather
                    // large. mostly an issue while bootstrapping new
                    // competitions with lots of pending feeds to process.
                    log.info("End of Cycling feeds processing batch. Will resume in 5 minutes.");
                    return;
                }
                // sync up all feeds (except live feeds) for a given competition
                // in a given season.
                processed += syncCyclingComp(String.valueOf(compId), seasons[i]);
            }
        }

    }

    /**
     * Synchronize all Basket feeds for a given competition's season
     * <p>
     * 
     * This method is responsible for the actual update of feeds if they do
     * require it.
     * 
     * @param competitionId: the competition ID
     * @param season: the season label.
     * @return the amount processed files.
     * @throws OptaException
     */
    private int syncBasketComp(String competitionId, String season)
            throws OptaException {

        final String bb1 = "BB1-" + competitionId + "-" + season + "-7.xml";

        // BB9-{competition_id}-season_id}-{sport_id}-{game_id}.xml
        final String bb9 = "BB9-" + competitionId + "-" + season + "-7-.*.xml";

        int processed = 0;

        final FTPClientWrapper opta = getFTPClientWrapper();
        try {

            opta.connect();

            // Processing BB1 (order matters for first run)
            OptaProcessedFeed pfeed = getProcessedFeedFor(bb1);
            FTPFile ftpFile = null;
            boolean process = false;
            FTPFile[] ftpfiles;
            try {
                ftpfiles = opta.getClient().listFiles(bb1);
            } catch (IOException e) {
                throw new OptaException(e.getMessage());
            }
            if (ftpfiles.length == 1) {
                ftpFile = ftpfiles[0];
            }
            if (pfeed == null) {
                process = true;
            } else {
                if (ftpFile != null) {
                    final Date formerFeedTimestamp = pfeed.getTimestamp();
                    final Date newFeedTimestamp = ftpFile.getTimestamp()
                            .getTime();
                    if (newFeedTimestamp == null
                            || newFeedTimestamp.compareTo(formerFeedTimestamp) > 0) {
                        process = true;
                    } else {
                        log.debug("Feed has not changed. No need to reprocess feed="
                                + bb1);
                    }
                } else {
                    log.warn("Feed with name=" + bb1 + " cannot be found");
                }
            }

            if (process && ftpFile != null) {
                File feed = getFileFromFTP(opta, bb1);
                if (feed != null) {
                    log.info("Syncing file with name=" + bb1);
                    bb1Sync(feed);
                    setFileAsProcessed(bb1, ftpFile.getTimestamp().getTime());
                    processed += 1;
                } else {
                    log.warn("Feed not available:" + bb1);
                }
            }

            /*
            // Processing BB9 (order matters for first run)
            pfeed = getProcessedFeedFor(bb9);
            ftpFile = null;
            process = false;
            try {
                ftpfiles = opta.getClient().listFiles(bb9);
            } catch (IOException e) {
                throw new OptaException(e.getMessage());
            }
            if (ftpfiles.length == 1) {
                ftpFile = ftpfiles[0];
            }
            if (pfeed == null) {
                process = true;
            } else {
                if (ftpFile != null) {
                    final Date formerFeedTimestamp = pfeed.getTimestamp();
                    final Date newFeedTimestamp = ftpFile.getTimestamp()
                            .getTime();
                    if (newFeedTimestamp == null
                            || newFeedTimestamp.compareTo(formerFeedTimestamp) > 0) {
                        process = true;
                    } else {
                        log.debug("Feed has not changed. No need to reprocess feed="
                                + bb9);
                    }
                } else {
                    log.warn("Feed with name=" + bb9 + " cannot be found");
                }
            }

            if (process && ftpFile != null) {
                File feed = getFileFromFTP(opta, bb9);
                if (feed != null) {
                    log.info("Syncing file with name=" + bb9);
                    bb9Sync(feed);
                    setFileAsProcessed(bb1, ftpFile.getTimestamp().getTime());
                    processed += 1;
                } else {
                    log.warn("Feed not available:" + bb9);
                }
            }
            */

        } catch (Exception e) {
            e.printStackTrace();
            throw new OptaException(e.getMessage());
        } finally {
            opta.disconnect();
        }

        return processed;

    }

    @Override
    public BB1Parser bb1Sync(File file) throws OptaException {
        return new BB1Parser(file, sports, em);
    }

    @Override
    public void syncBasket() throws OptaException {
        int processed = 0;
        for (Map.Entry<Integer, String[]> entry : getBasketSeasonsByCompetitions()
                .entrySet()) {
            final Integer compId = entry.getKey();
            final String[] seasons = entry.getValue();
            for (int i = 0; i < seasons.length; i++) {
                if (processed > 100) {
                    // batch to keep transactions small. These feeds are rather
                    // large. mostly an issue while bootstrapping new
                    // competitions with lots of pending feeds to process.
                    log.info("End of Basket feeds processing batch. Will resume in 5 minutes.");
                    return;
                }
                // sync up all feeds (except live feeds) for a given competition
                // in a given season.
                processed += syncBasketComp(String.valueOf(compId), seasons[i]);
            }
        }
    }

    @Override
    public Map<Integer, String[]> getBasketSeasonsByCompetitions() {
        Map<Integer, String[]> seasons = new HashMap<Integer, String[]>();
        seasons.put(6, new String[] { "2012" }); // NB REG
        return seasons;
    }

    @Override
    public BB9Parser bb9Sync(File file) throws OptaException {
        return new BB9Parser(file, sports, em);
    }

}
