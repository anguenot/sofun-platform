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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.community.Community;
import org.sofun.core.api.community.CommunityService;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.local.CommunityServiceLocal;
import org.sofun.core.api.local.NotificationServiceLocal;
import org.sofun.core.api.local.TeamServiceLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.api.member.ejb.MemberServiceLocal;
import org.sofun.core.api.notification.NotificationService;
import org.sofun.core.api.remote.TeamServiceRemote;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.TeamPrivacy;
import org.sofun.core.api.team.TeamRoles;
import org.sofun.core.api.team.TeamService;
import org.sofun.core.api.team.TeamTag;
import org.sofun.core.api.team.TeamType;
import org.sofun.core.api.team.table.TeamRankingTable;
import org.sofun.core.kup.KupImpl;

/**
 * Team (aka "room") Service Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(TeamServiceLocal.class)
@Remote(TeamServiceRemote.class)
public class TeamServiceImpl implements TeamService {

    private static final long serialVersionUID = -4826736368960366092L;

    private static final Log log = LogFactory.getLog(TeamServiceImpl.class);

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    public transient EntityManager em;

    @EJB(
            beanName = "MemberServiceImpl",
            beanInterface = MemberServiceLocal.class)
    private MemberService members;

    @EJB(
            beanName = "CommunityServiceImpl",
            beanInterface = CommunityServiceLocal.class)
    private CommunityService communities;

    @EJB(
            beanName = "NotificationServiceImpl",
            beanInterface = NotificationServiceLocal.class)
    private NotificationService notifications;

    public TeamServiceImpl() {
        super();
    }

    public TeamServiceImpl(EntityManager em) {
        this();
        this.em = em;
    }

    private Query createQuery(String queryStr) {
        Query query = em.createQuery(queryStr);
        return query;
    }

    @Override
    public Team getTeam(long id) {

        String queryStr = "from " + TeamImpl.class.getSimpleName()
                + " t where t.id =:id";
        Query query = createQuery(queryStr);
        query.setParameter("id", id);

        try {
            return (Team) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public Team getTeam(String name) {

        String queryStr = "from " + TeamImpl.class.getSimpleName()
                + " t where t.name =:name";
        Query query = createQuery(queryStr);
        query.setParameter("name", name);

        try {
            return (Team) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public long countTeams() {
        String queryStr = "SELECT COUNT(t.id) FROM "
                + TeamImpl.class.getSimpleName() + " t";
        Query query = createQuery(queryStr);
        return (Long) query.getSingleResult();
    }

    @Override
    public Team addTeam(Team team) {
        final String name = team.getName();
        Team t = getTeam(name);
        if (t == null) {
            t = new TeamImpl(team.getName(), team.hasRankingTable());
            em.persist(t);
        } else {
            em.merge(t);
        }
        return t;

    }

    @Override
    public Team updateTeam(Team team) {
        em.merge(team);
        return team;
    }

    @Override
    public Team createTeam(String name) {
        return createTeam(name, true);
    }

    @Override
    public Team createTeam(String name, boolean hasRankingtable) {
        Team newTeam = new TeamImpl(name, hasRankingtable);
        return newTeam;
    }

    @Override
    public Team addTeam(String name) {
        return addTeam(createTeam(name, true));
    }

    @Override
    public boolean exists(Team team) {
        return getTeam(team.getId()) != null ? true : false;
    }

    @Override
    public Team removeTeam(Team team) {
        if (team == null) {
            return null;
        }
        for (Community c : team.getCommunities()) {
            c.delTeam(team);
            team.delCommunity(c);
        }
        final long id = team.getId();
        Team t = getTeam(id);
        if (t != null) {
            // FIXME we need to rather flag the team as non visible to not
            // delete any content.
            log.error("Deleting team is not yet implemented.");
        }
        return t;
    }

    @Override
    public Team removeTeamById(long id) {
        return removeTeam(getTeam(id));
    }

    @Override
    public TeamRankingTable getRankingTableFor(Team team) {
        if (team != null) {
            return (TeamRankingTable) team.getRankingTable();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<TeamTag> getTeamTags(int limit) {

        Set<TeamTag> tags = new HashSet<TeamTag>();

        String queryStr = "from " + TeamTagImpl.class.getSimpleName();
        Query query = createQuery(queryStr);

        if (limit > 0) {
            query.setMaxResults(limit);
        }

        List<TeamTag> results = query.getResultList();

        // Sort tags in reverse order based on score. See compareTo() for
        // implementation details.
        Collections.sort(results, Collections.reverseOrder());

        if (results != null) {
            tags = new LinkedHashSet<TeamTag>(results);
        }

        return tags;

    }

    @Override
    public TeamTag getTeamTag(String name) {

        String queryStr = "from " + TeamTagImpl.class.getSimpleName()
                + " t where t.name=:name";
        Query query = createQuery(queryStr);
        query.setParameter("name", name);

        try {
            return (TeamTag) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public Set<TeamType> getTeamTypes() {

        Set<TeamType> types = new HashSet<TeamType>();

        String queryStr = "from " + TeamTypeImpl.class.getSimpleName();
        Query query = createQuery(queryStr);

        @SuppressWarnings("unchecked")
        List<TeamType> results = query.getResultList();
        if (results != null) {
            types = new HashSet<TeamType>(results);
        }

        return types;

    }

    @Override
    public TeamType getTeamType(String name) {

        String queryStr = "from " + TeamTypeImpl.class.getSimpleName()
                + " t where t.name=:name";
        Query query = createQuery(queryStr);
        query.setParameter("name", name);

        try {
            return (TeamType) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public Set<String> getTeamPrivacyTypes() {
        Set<String> types = new HashSet<String>();
        types.add(TeamPrivacy.PUBLIC);
        types.add(TeamPrivacy.PUBLIC_GAMBLING_FR);
        types.add(TeamPrivacy.PRIVATE);
        types.add(TeamPrivacy.PRIVATE_GAMBLING_FR);
        return types;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Team> searchTeams(Map<String, String> params)
            throws CoreException {

        log.debug("searchTeams()");

        Set<Team> teams = new HashSet<Team>();
        List<TeamImpl> results = null;

        // Batching information
        int offset = 0;
        final String offsetStr = params.get("offset");
        if (offsetStr != null) {
            log.debug("searchTeams() - user param offset => " + offsetStr);
            offset = Integer.valueOf(offsetStr);
        }
        int batchSize = 10; // default batch size
        final String batchSizeStr = params.get("batchSize");
        if (batchSizeStr != null) {
            log.debug("searchTeams() - user param batchSize => " + batchSizeStr);
            batchSize = Integer.valueOf(batchSizeStr);
        }

        String queryStr = "";
        Query query = null;

        final String email = params.get("email");
        if (email != null) {
            Member member = members.getMember(email);
            if (member != null) {
                log.debug("searchTeams() - user param email => " + email);
                // We assume an administrator is always a member of the team
                queryStr = "SELECT t FROM "
                        + TeamImpl.class.getSimpleName()
                        + " t JOIN t.members m WHERE m.id=:member_id AND t.name NOT IN (:name)";
                query = em.createQuery(queryStr, TeamImpl.class);
                query.setParameter("member_id", member.getId());

                // Exclude community teams.
                query.setParameter("name", communities.getCommunityNames());
            }
        } else {

            boolean sflag = false;

            queryStr = "SELECT DISTINCT * FROM teams WHERE ";

            // Keywords
            final String lkeywords = params.get("keywords");
            if (lkeywords != null && !lkeywords.isEmpty()) {
                final List<String> keywords = Arrays.asList(lkeywords
                        .split(" "));
                int i = 0;
                for (String keyword : keywords) {
                    if (i == 0) {
                        queryStr += " ( ";
                    } else if (i > 0) {
                        queryStr += " OR ";
                    }
                    queryStr += " to_tsvector('french', name || ' ' || description) @@ to_tsquery('french', '"
                            + keyword + "') ";
                    i++;
                }
                if (keywords.size() > 0) {
                    sflag = true;
                    queryStr += " ) ";
                }
            }

            final String name = params.get("name");
            if (name != null && !name.isEmpty()) {

                if (sflag) {
                    queryStr += " AND ";
                }

                final List<String> names = Arrays.asList(name.split(","));
                // Remove all community names to avoid default community teams
                // to show up in search results.
                for (String cname : communities.getCommunityNames()) {
                    if (names.contains(cname)) {
                        names.remove(cname);
                    }
                }

                if (names.size() > 0) {
                    queryStr += " ( ";
                    int i = 0;
                    for (String kname : names) {
                        if (i > 0) {
                            queryStr += " OR ";
                        }
                        queryStr += " name like '" + kname + "'";
                        i++;
                    }
                    queryStr += " ) ";
                    sflag = true;
                }

            } else {

                if (sflag) {
                    queryStr += " AND ";
                }

                final List<String> names = communities.getCommunityNames();
                if (names.size() > 0) {
                    queryStr += " ( ";
                    int i = 0;
                    for (String kname : names) {
                        if (i > 0) {
                            queryStr += " AND ";
                        }
                        queryStr += " name not like '" + kname + "'";
                        i++;
                    }
                    sflag = true;
                    queryStr += " ) ";
                }

            }

            // Privacy

            final String privacy = params.get("privacy");
            List<String> privacyParam = new ArrayList<String>();
            if (privacy != null && !privacy.isEmpty()) {
                if (TeamPrivacy.PUBLIC.equals(privacy)) {
                    privacyParam.add(TeamPrivacy.PUBLIC);
                    privacyParam.add(TeamPrivacy.PUBLIC_GAMBLING_FR);
                } else if (TeamPrivacy.PRIVATE.equals(privacy)) {
                    privacyParam.add(TeamPrivacy.PRIVATE);
                    privacyParam.add(TeamPrivacy.PRIVATE_GAMBLING_FR);
                } else {
                    privacyParam.add(privacy);
                }
            } else {
                privacyParam.add(TeamPrivacy.PRIVATE);
                privacyParam.add(TeamPrivacy.PRIVATE_GAMBLING_FR);
                privacyParam.add(TeamPrivacy.PUBLIC);
                privacyParam.add(TeamPrivacy.PUBLIC_GAMBLING_FR);
            }

            if (privacyParam.size() > 0) {

                if (sflag) {
                    queryStr += " AND ";
                }

                queryStr += " ( ";
                int i = 0;
                for (String kprivacy : privacyParam) {
                    if (i > 0) {
                        queryStr += " OR ";
                    }
                    queryStr += " privacy like '" + kprivacy + "'";
                    i++;
                }
                queryStr += " ) ";
            }

            // SORTING
            queryStr += " ORDER BY nb_members DESC,created DESC";
            query = em.createNativeQuery(queryStr, TeamImpl.class);
        }

        if (query != null) {
            // Batching information
            query.setFirstResult(offset);
            query.setMaxResults(batchSize);
            results = query.getResultList();
            if (results != null) {
                log.debug("searchTeams() - results.size() => " + results.size());
                teams = new HashSet<Team>(results);
            }
        }
        return teams;
    }

    @Override
    public Set<String> getCredentials(Team team, Member member) {

        Set<String> credentials = new HashSet<String>();

        if (member.getAdminTeams().contains(team)) {
            credentials.add(TeamRoles.ADMINISTRATOR);
        }

        if (member.getMemberTeams().contains(team)) {
            credentials.add(TeamRoles.MEMBER);
        }

        if (credentials.size() == 0) {
            credentials.add(TeamRoles.ANONYMOUS);
        }

        return credentials;

    }

    @Override
    public void invite(Team team, Member inviter, Map<String, String> params)
            throws CoreException {
        params.put("templateId", "team-invite_fr");
        params.put("format", "text/plain");
        notifications.sendEmail(inviter, params);
    }

    @Override
    public int countSearchTeams(Map<String, String> params)
            throws CoreException {
        // XXX temporary hack
        params.put("offset", "0");
        params.put("batchSize", String.valueOf(Integer.MAX_VALUE));
        return searchTeams(params).size();
    }

    @Override
    public List<Kup> getKupsFor(Team team, int offset, int size)
            throws CoreException {

        final Query query = em
                .createNativeQuery(
                        "SELECT * FROM kups WHERE id IN (SELECT DISTINCT kup_id FROM teams_kups WHERE team_id=:team_id) and status != -1 ORDER BY status ASC, start_date ASC",
                        KupImpl.class);
        query.setParameter("team_id", team.getId());
        query.setFirstResult(offset);
        query.setMaxResults(size);

        @SuppressWarnings("unchecked")
        List<Kup> results = query.getResultList();
        if (results != null) {
            return results;
        }
        return new ArrayList<Kup>();

    }

    @Override
    public boolean isTeamRemovable(Team team) {
        boolean canRemove = true;
        if (team != null) {
            for (Kup kup : team.getKups()) {
                if (kup.getParticipants().size() > 1) {
                    // admin is always a member.
                    canRemove = false;
                    break;
                }
            }
        } else {
            canRemove = false;
        }
        return canRemove;
    }

    @Override
    public List<Team> getTeamsWithActiveKups() {
        final Query query = em
                .createNativeQuery(
                        "SELECT DISTINCT * FROM teams,teams_kups where teams.id=teams_kups.team_id and teams_kups.kup_id IN ( select id from kups where ((kups.type like 'FREE' AND (kups.status <= 3 and kups.status != -1)) OR ((kups.type like 'GAMBLING_FR' AND (kups.status <= 4 and kups.status != -1) )))) ORDER by teams.id;",
                        TeamImpl.class);

        @SuppressWarnings("unchecked")
        List<Team> results = query.getResultList();
        if (results != null) {
            results.add(getTeam(3820258)); // TMP
            results.add(getTeam(3921267)); // TMP
            return results;
        }
        return new ArrayList<Team>();
    }

}
