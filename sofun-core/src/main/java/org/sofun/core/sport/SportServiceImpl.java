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

package org.sofun.core.sport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.sofun.core.CoreConstants;
import org.sofun.core.api.country.Country;
import org.sofun.core.api.local.SportServiceLocal;
import org.sofun.core.api.remote.SportServiceRemote;
import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.SportCategory;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentSeasonStatus;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTable;
import org.sofun.core.country.CountryImpl;
import org.sofun.core.sport.tournament.TournamentGameImpl;
import org.sofun.core.sport.tournament.TournamentImpl;
import org.sofun.core.sport.tournament.TournamentRoundImpl;
import org.sofun.core.sport.tournament.TournamentSeasonImpl;
import org.sofun.core.sport.tournament.TournamentStageImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableImpl;

/**
 * Sport Service Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(SportServiceLocal.class)
@Remote(SportServiceRemote.class)
public class SportServiceImpl implements SportService {

    private static final long serialVersionUID = 8561495539939827181L;

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    public SportServiceImpl() {
        super();
    }

    public SportServiceImpl(EntityManager em) {
        this();
        this.em = em;
    }

    protected Query createQuery(String queryStr) {
        return em.createQuery(queryStr);
    }

    @Override
    public SportCategory getSportCategory(long id) {
        return (SportCategory) getEntityByUUID(id, SportCategoryImpl.class);
    }

    @Override
    public Sport getSport(long id) {
        return (Sport) getEntityByUUID(id, SportImpl.class);
    }

    @Override
    public Sport getSportByName(String name) {

        String queryStr = "from " + SportImpl.class.getSimpleName()
                + " s where s.name=:name";

        Query query = createQuery(queryStr);
        query.setParameter("name", name);

        try {
            return (Sport) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public long countSportContestantTeams() {
        String queryStr = "SELECT COUNT(c.id) FROM "
                + SportContestantImpl.class.getSimpleName()
                + " c where c.type =:type";
        Query query = createQuery(queryStr);
        query.setParameter("type", SportContestantType.TEAM);
        return (Long) query.getSingleResult();
    }

    @Override
    public long countSports() {
        String queryStr = "SELECT COUNT(c.id) FROM "
                + SportImpl.class.getSimpleName() + " c";
        Query query = createQuery(queryStr);
        return (Long) query.getSingleResult();
    }

    @Override
    public long countSportCategories() {
        String queryStr = "SELECT COUNT(c.id) FROM "
                + SportCategoryImpl.class.getSimpleName() + " c";
        Query query = createQuery(queryStr);
        return (Long) query.getSingleResult();
    }

    @Override
    public long countSportTournaments() {
        String queryStr = "SELECT COUNT(c.id) FROM "
                + TournamentImpl.class.getSimpleName() + " c";
        Query query = createQuery(queryStr);
        return (Long) query.getSingleResult();
    }

    @Override
    public long countSportContestants() {
        String queryStr = "SELECT COUNT(c.id) FROM "
                + SportContestantImpl.class.getSimpleName()
                + " c where c.type =:type";
        Query query = createQuery(queryStr);
        query.setParameter("type", SportContestantType.INDIVIDUAL);
        return (Long) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Tournament> getAllTournaments() {
        final String queryStr = "from " + TournamentImpl.class.getSimpleName()
                + " c";
        Query query = createQuery(queryStr);
        return query.getResultList().iterator();
    }

    @Override
    public SportContestant getSportContestantTeam(String uuid) {
        String queryStr = "from " + SportContestantImpl.class.getSimpleName()
                + " c where c.uuid =:id and c.type =:type";
        Query query = createQuery(queryStr);
        query.setParameter("id", uuid);
        query.setParameter("type", SportContestantType.TEAM);

        try {
            return (SportContestant) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public SportContestant getSportContestantPlayer(String uuid) {
        String queryStr = "from " + SportContestantImpl.class.getSimpleName()
                + " c where c.uuid =:id and c.type =:type";
        Query query = createQuery(queryStr);
        query.setParameter("id", uuid);
        query.setParameter("type", SportContestantType.INDIVIDUAL);

        try {
            return (SportContestant) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public Tournament getSportTournament(long id) {
        return (Tournament) getEntityByUUID(id, TournamentImpl.class);
    }

    @Override
    public Tournament getSportTournamentByCode(String code) {
        final String queryStr = "from " + TournamentImpl.class.getName()
                + " t where t.code=:code";
        final Query query = createQuery(queryStr);
        query.setParameter("code", code);
        try {
            return (Tournament) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public TournamentSeason getTournamentSeason(long id) {
        return (TournamentSeason) getEntityByUUID(id,
                TournamentSeasonImpl.class);
    }

    @Override
    public TournamentGame getTournamentGame(String uuid) {
        return (TournamentGame) getEntityByUUID(uuid, TournamentGameImpl.class);
    }

    @Override
    public TournamentStage getTournamentStage(long id) {
        return (TournamentStage) getEntityByUUID(id, TournamentStageImpl.class);
    }

    @Override
    public TournamentLeagueTable getTournamentLeagueTable(long id) {
        return (TournamentLeagueTable) getEntityByUUID(id,
                TournamentLeagueTableImpl.class);
    }

    @Override
    public TournamentRound getTournamentRound(long id) {
        String klass = TournamentRoundImpl.class.getSimpleName();
        String queryStr = "from " + klass + " o where o.id =:id";
        Query query = createQuery(queryStr);
        query.setParameter("id", id);
        try {
            return (TournamentRound) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    /**
     * Returns an entity instance given it's UUID and class.
     * 
     * <p/>
     * 
     * This method is here for factorization sake since the model for sports is
     * built up the same way.
     * 
     * <p/>
     * 
     * ATTENTION: do not change scope of this method. It has to remain private
     * #PF-144
     * 
     * @param uuid: uuid (not internal SQL identifier) of the entity
     * @param klass: the entity class implementation
     * @return an instance of class or null if not found.
     */
    private Object getEntityByUUID(long uuid,
            @SuppressWarnings("rawtypes") Class klass) {

        //
        // #PF-144: on-x audit #1 V13
        //
        // There is no injection risk here.
        //
        // The method is private and the class parameter does not come from
        // anywhere else than this class. (initialization
        // happens within the class.)
        //

        final String queryStr = "from " + klass.getSimpleName()
                + " o where o.uuid =:uuid";
        final Query query = createQuery(queryStr);
        query.setParameter("uuid", uuid);
        try {
            return query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    /**
     * Returns an entity instance given it's UUID and class.
     * 
     * <p/>
     * 
     * This method is here for factorization sake since the model for sports is
     * built up the same way.
     * 
     * <p/>
     * 
     * ATTENTION: do not change scope of this method. It has to remain private
     * #PF-144
     * 
     * @param uuid: uuid (not internal SQL identifier) of the entity
     * @param klass: the entity class implementation
     * @return an instance of class or null if not found.
     */
    private Object getEntityByUUID(String uuid,
            @SuppressWarnings("rawtypes") Class klass) {

        //
        // #PF-144: on-x audit #1 V13
        //
        // There is no injection risk here.
        //
        // The method is private and the class parameter does not come from
        // anywhere else than this class. (initialization
        // happens within the class.)
        //

        final String queryStr = "from " + klass.getSimpleName()
                + " o where o.uuid =:uuid";
        final Query query = createQuery(queryStr);
        query.setParameter("uuid", uuid);
        try {
            return query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public Country getCountry(String iso) {
        String queryStr = "from " + CountryImpl.class.getSimpleName()
                + " c where c.iso =:iso";
        Query query = createQuery(queryStr);
        query.setParameter("iso", iso);
        try {
            return (Country) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public Country getCountryByName(String name) {

        String queryStr = "from " + CountryImpl.class.getSimpleName()
                + " c where c.name =:name";
        Query query = createQuery(queryStr);
        query.setParameter("name", name);
        try {
            return (Country) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TournamentGame> getTournamentGamesByStatus(String status) {
        String queryStr = "";
        if (status != null) {
            queryStr = "from " + TournamentGameImpl.class.getSimpleName()
                    + " t where t.status=:status";
        } else {
            queryStr = "from " + TournamentGameImpl.class.getSimpleName()
                    + " t where t.status IS NULL";
        }
        Query query = createQuery(queryStr);
        if (status != null) {
            query.setParameter("status", status);
        }
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TournamentRound> getTournamentRoundsByStatus(String status) {
        String queryStr = "";
        if (status != null) {
            queryStr = "from " + TournamentRoundImpl.class.getSimpleName()
                    + " t where t.status=:status";
        } else {
            queryStr = "from " + TournamentRoundImpl.class.getSimpleName()
                    + " t where t.status IS NULL";
        }
        Query query = createQuery(queryStr);
        if (status != null) {
            query.setParameter("status", status);
        }
        return query.getResultList();
    }

    @Override
    public TournamentRound getTournamentRoundByUUID(long id) {
        return (TournamentRound) getEntityByUUID(id, TournamentRoundImpl.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TournamentSeason> getSeasonsFor(Tournament tournament) {

        List<TournamentSeason> season = new ArrayList<TournamentSeason>();

        if (tournament == null) {
            return season;
        }

        final String queryStr = "from "
                + TournamentSeasonImpl.class.getSimpleName()
                + " t where t.tournament.uuid=:uuid";
        final Query query = createQuery(queryStr);
        query.setParameter("uuid", tournament.getUUID());

        return query.getResultList();

    }

    @Override
    public TournamentSeason getSeasonByYearLabel(Tournament tournament,
            String yearLabel) {

        if (tournament == null || yearLabel == null) {
            return null;
        }

        final String queryStr = "from "
                + TournamentSeasonImpl.class.getSimpleName()
                + " t where t.tournament.uuid=:uuid and t.yearLabel=:yearLabel";
        final Query query = createQuery(queryStr);
        query.setParameter("uuid", tournament.getUUID());
        query.setParameter("yearLabel", yearLabel);

        try {
            return (TournamentSeason) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public TournamentSeason getActiveSeasonFor(Tournament tournament) {
        TournamentSeason active = null;
        for (TournamentSeason season : getSeasonsFor(tournament)) {
            if (!TournamentSeasonStatus.TERMINATED.equals(season.getStatus())) {
                if (active == null) {
                    active = season;
                } else {
                    if (active.getYearLabel().compareTo(season.getYearLabel()) < 0) {
                        active = season;
                    }
                }
            }
        }
        return active;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TournamentStage> getTournamentStagesByStatus(String status) {
        String queryStr = "";
        if (status != null) {
            queryStr = "from " + TournamentStageImpl.class.getSimpleName()
                    + " t where t.status=:status";
        } else {
            queryStr = "from " + TournamentStageImpl.class.getSimpleName()
                    + " t where t.status IS NULL";
        }
        Query query = createQuery(queryStr);
        if (status != null) {
            query.setParameter("status", status);
        }
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TournamentSeason> getTournamentSeasonsByStatus(String status) {
        String queryStr = "";
        if (status != null) {
            queryStr = "from " + TournamentSeasonImpl.class.getSimpleName()
                    + " t where t.status=:status";
        } else {
            queryStr = "from " + TournamentSeasonImpl.class.getSimpleName()
                    + " t where t.status IS NULL";
        }
        Query query = createQuery(queryStr);
        if (status != null) {
            query.setParameter("status", status);
        }
        return query.getResultList();
    }

    @Override
    public TournamentSeason getSeasonByName(Tournament tournament, String name) {

        if (tournament == null || name == null) {
            return null;
        }

        final String queryStr = "from "
                + TournamentSeasonImpl.class.getSimpleName()
                + " t where t.tournament.uuid=:uuid and t.name=:name";
        final Query query = createQuery(queryStr);
        query.setParameter("uuid", tournament.getUUID());
        query.setParameter("name", name);

        try {
            return (TournamentSeason) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public TournamentSeason getSeasonByUUID(Tournament tournament, long uuid) {

        if (tournament == null) {
            return null;
        }

        final String queryStr = "from "
                + TournamentSeasonImpl.class.getSimpleName()
                + " t where t.tournament.uuid=:tuuid and t.uuid=:uuid";
        final Query query = createQuery(queryStr);
        query.setParameter("tuuid", tournament.getUUID());
        query.setParameter("uuid", uuid);

        try {
            return (TournamentSeason) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

}
