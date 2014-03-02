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

package org.sofun.core.team;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Timeout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.local.TeamServiceLocal;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.TeamService;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Singleton
public class FacebookLigue1Clubs {

    private boolean available = true;

    private static final Log log = LogFactory.getLog(FacebookLigue1Clubs.class);

    private static final long APP_TEAM_ID = 3638935;

    private static final long APP_TEAM_MHSC_ID = 3638852;
    private static final long APP_TEAM_PSG_ID = 3638876;
    private static final long APP_TEAM_LOSC_ID = 3638879;
    private static final long APP_TEAM_OL_ID = 3638882;
    private static final long APP_TEAM_GDB_ID = 3638885;
    private static final long APP_TEAM_RENNES_ID = 3638888;
    private static final long APP_TEAM_ASSE_ID = 3638893;
    private static final long APP_TEAM_TOULOUSE_ID = 3638896;
    private static final long APP_TEAM_EVIAN_ID = 3638899;
    private static final long APP_TEAM_OM_ID = 3638902;

    private static final long APP_TEAM_NANCY_ID = 3638905;
    private static final long APP_TEAM_VAL_ID = 3638908;
    private static final long APP_TEAM_NICE_ID = 3638911;
    private static final long APP_TEAM_SOCHAUX_ID = 3638914;
    private static final long APP_TEAM_BREST_ID = 3638917;
    private static final long APP_TEAM_AJACCIO_ID = 3638920;
    private static final long APP_TEAM_TROYES_ID = 3638923;
    private static final long APP_TEAM_LORIENT_ID = 3638926;
    private static final long APP_TEAM_BASTIA_ID = 3638929;
    private static final long APP_TEAM_REIMS_ID = 3638932;

    @EJB(beanName = "TeamServiceImpl", beanInterface = TeamServiceLocal.class)
    private TeamService teams;

    @Timeout
    // XXX DISABLED
    // @Schedule(minute = "*/15", hour = "*", persistent = false)
    @Lock(LockType.READ)
    public void check() throws Exception {

        if (!available) {
            return;
        } else {
            available = false;
        }

        try {

            Team appTeam = teams.getTeam(APP_TEAM_ID);
            Team[] clubs = new Team[] { teams.getTeam(APP_TEAM_MHSC_ID),
                    teams.getTeam(APP_TEAM_PSG_ID),
                    teams.getTeam(APP_TEAM_LOSC_ID),
                    teams.getTeam(APP_TEAM_OL_ID),
                    teams.getTeam(APP_TEAM_GDB_ID),
                    teams.getTeam(APP_TEAM_RENNES_ID),
                    teams.getTeam(APP_TEAM_ASSE_ID),
                    teams.getTeam(APP_TEAM_TOULOUSE_ID),
                    teams.getTeam(APP_TEAM_EVIAN_ID),
                    teams.getTeam(APP_TEAM_OM_ID),
                    teams.getTeam(APP_TEAM_NANCY_ID),
                    teams.getTeam(APP_TEAM_VAL_ID),
                    teams.getTeam(APP_TEAM_NICE_ID),
                    teams.getTeam(APP_TEAM_SOCHAUX_ID),
                    teams.getTeam(APP_TEAM_BREST_ID),
                    teams.getTeam(APP_TEAM_AJACCIO_ID),
                    teams.getTeam(APP_TEAM_TROYES_ID),
                    teams.getTeam(APP_TEAM_LORIENT_ID),
                    teams.getTeam(APP_TEAM_BASTIA_ID),
                    teams.getTeam(APP_TEAM_REIMS_ID) };

            List<Team> lclubs = Arrays.asList(clubs);

            Iterator<MemberRankingTableEntry> entriesIt = appTeam
                    .getRankingTable().getEntries().iterator();
            while (entriesIt.hasNext()) {

                MemberRankingTableEntry entry = entriesIt.next();
                boolean up = false;
                for (Team club : lclubs) {
                    if (club.isMember(entry.getMember())) {
                        MemberRankingTableEntry clubEntry = club
                                .getRankingTable().getEntryForMember(
                                        entry.getMember());
                        clubEntry.setValue(entry.getValue());
                        up = true;
                        break;
                    }
                }
                if (!up) {
                    log.error("No entry found for member="
                            + entry.getMember().getEmail());
                    // throw new CoreException("No club found for member="
                    // + entry.getMember().getEmail());
                }

            }

            // Compute total points for each clubs
            for (Team club : lclubs) {
                long totalPoints = 0;
                Iterator<MemberRankingTableEntry> clubEntriesIt = club
                        .getRankingTable().getEntries().iterator();
                while (clubEntriesIt.hasNext()) {
                    MemberRankingTableEntry entry = clubEntriesIt.next();
                    totalPoints += entry.getValue();
                }
                club.getRankingTable().setEntriesTotalPoints(totalPoints);
            }

        } catch (Throwable t) {
            t.printStackTrace();
            log.error(t.getMessage());
        } finally {
            available = true;
        }

    }

}
