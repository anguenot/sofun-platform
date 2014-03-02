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

package org.sofun.platform.web.rest.resource.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import org.sofun.core.CoreConstants;
import org.sofun.core.api.community.Community;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.table.TeamRankingTable;
import org.sofun.platform.web.rest.api.exception.ReSTException;
import org.sofun.platform.web.rest.api.exception.ReSTRuntimeException;
import org.sofun.platform.web.rest.api.feed.ReSTFeed;
import org.sofun.platform.web.rest.api.member.ReSTMember;
import org.sofun.platform.web.rest.api.member.ReSTMemberRakingStats;
import org.sofun.platform.web.rest.api.team.ReSTMemberRankingTable;
import org.sofun.platform.web.rest.api.team.ReSTMemberRankingTableEntry;
import org.sofun.platform.web.rest.api.team.ReSTTeam;
import org.sofun.platform.web.rest.resource.ejb.api.CommunityResource;

/**
 * Community ReST APIs.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
public class CommunityResourceBean extends AbstractResource implements
        CommunityResource {

    private static final long serialVersionUID = 4846138321903841155L;

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    public CommunityResourceBean() {
        super();
    }

    public CommunityResourceBean(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    public Collection<ReSTMember> getMembers(long communityId)
            throws ReSTException {
        final Community c = getCoreCommunityById(communityId);
        final Collection<ReSTMember> members = new ArrayList<ReSTMember>();
        Set<Member> coreMembers = c.getMembers();
        synchronized (coreMembers) {
            for (Member member : c.getMembers()) {
                members.add(new ReSTMember(member));
            }
        }
        return members;
    }

    @Override
    public Response getMemberCommunityRankingTable(long communityId,
            String memberEmail, int offset, int batchSize) throws ReSTException {
        Community c = getCoreCommunityById(communityId);
        return Response
                .status(202)
                .entity(new ReSTMemberRankingTable((TeamRankingTable) c
                        .getDefaultTeam().getRankingTable(), offset, batchSize,
                        new ReSTMember(getCoreMemberByEmail(memberEmail))))
                .build();
    }

    @Override
    public Response getCommunityRankingTable(long communityId, int offset,
            int batchSize) throws ReSTException {

        Community c = getCoreCommunityById(communityId);
        Team team = c.getDefaultTeam();
        TeamRankingTable table = (TeamRankingTable) team.getRankingTable();

        ReSTMemberRankingTable rTable = new ReSTMemberRankingTable(table,
                offset, batchSize);

        return Response.status(202).entity(rTable).build();

    }

    @Override
    public ReSTMemberRankingTable getFriendsCommunityRankingTable(
            long communityId, String memberEmail, int offset, int bachSize)
            throws ReSTException {
        Community c = getCoreCommunityById(communityId);
        return new ReSTMemberRankingTable((TeamRankingTable) c.getDefaultTeam()
                .getRankingTable(), offset, bachSize, new ReSTMember(
                getCoreMemberByEmail(memberEmail), true), true, null);
    }

    protected boolean hasMemberPredictionsFor(Member member, long kupId)
            throws ReSTException {
        Kup kup = getKupService().getKupById(kupId);
        return getPredictionService().hasPredictions(member, kup);
    }

    @Override
    public Response delMember(long communityId, String email)
            throws ReSTException {
        Community c = getCoreCommunityById(communityId);
        Member m = getCoreMemberByEmail(email);
        c.removeMember(m);
        c.removeAdmin(m);
        c.getDefaultTeam().removeMember(m);
        c.getDefaultTeam().removeAdmin(m);
        return Response.status(202)
                .entity("Member with email=" + email + " has been removed")
                .build();
    }

    @Override
    public ReSTMember getMember(long communityId, String email)
            throws ReSTException {
        // Community c = getCoreCommunityById(communityId);
        Member member = getCoreMemberByEmail(email);
        ReSTMember m = null;
        // if (member.getMemberTeams().contains(c.getDefaultTeam())) {
        m = new ReSTMember(member);
        m.setHasPredictions(hasMemberPredictionsFor(member, communityId));
        // }
        return m;
    }

    @Override
    public ReSTMember getMemberById(long communityId, long uuid)
            throws ReSTException {
        Member member = getCoreMemberById(uuid);
        return getMember(communityId, member.getEmail());
    }

    @Override
    public Response getRankingInfoForMember(long communityId, String memberEmail)
            throws ReSTException {

        ReSTMemberRankingTable table = getFriendsCommunityRankingTable(
                communityId, memberEmail, 0, 0);

        ReSTMemberRankingTableEntry memberEntry = table
                .getEntryFor(memberEmail);
        int totalPoints = 0;
        final double percentageSuccess = 0;
        long nbPredictions = 0;
        if (memberEntry != null) {
            totalPoints = memberEntry.getValue();
            nbPredictions = memberEntry.getStats().getNumberOfPredictions();
        }

        ReSTMemberRakingStats stats = new ReSTMemberRakingStats(
                table.getMemberPosition(), table.getTotalMembers(),
                table.getFriendsMemberPosition(), table.getTotalFriends(),
                totalPoints, nbPredictions, percentageSuccess);

        return Response.status(202).entity(stats).build();
    }

    @Override
    public Response getCommunityFeed(long communityId, int offset, int size)
            throws ReSTException {
        Community c = getCoreCommunityById(communityId);
        return Response.status(202)
                .entity(new ReSTFeed(c.getActivityFeed(), offset, size))
                .build();
    }

    @Override
    public Response getMemberFriends(long communityId, String email)
            throws ReSTException {

        // Community c = getCoreCommunityById(communityId);
        Member member = getCoreMemberByEmail(email);

        List<ReSTMember> friends = new ArrayList<ReSTMember>();
        for (Member friend : member.getFriends()) {
            // if (friend.getMemberTeams().contains(c.getDefaultTeam())) {
            friends.add(new ReSTMember(friend));
            // }
        }

        return Response.status(202).entity(friends).build();
    }

    private List<ReSTTeam> _getTeamsFor(long communityId, String memberEmail)
            throws ReSTException {

        final List<ReSTTeam> teams = new ArrayList<ReSTTeam>();
        final Community c = getCoreCommunityById(communityId);
        try {
            final Member m = getCoreMemberByEmail(memberEmail);
            for (Team t : c.getTeams()) {
                if (t.getMembers().contains(m) || t.getAdmins().contains(m)) {
                    teams.add(new ReSTTeam(t));
                }
            }
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }

        Collections.sort(teams);
        Collections.reverse(teams);
        return teams;
    }

    protected List<ReSTTeam> doBatchTeams(List<ReSTTeam> teams, int offset,
            int batchSize) {
        List<ReSTTeam> batchedTeams = new ArrayList<ReSTTeam>();
        if (teams.size() < offset) {
            batchedTeams = teams;
        } else if (teams.size() < (offset + batchSize)) {
            batchedTeams = teams.subList(offset, teams.size());
        } else {
            batchedTeams = teams.subList(offset, offset + batchSize);
        }
        return batchedTeams;
    }

    @Override
    public Response getBatchedTeamsFor(long communityId, String memberEmail,
            int offset, int batchSize) throws ReSTException {
        return Response
                .status(202)
                .entity(doBatchTeams(_getTeamsFor(communityId, memberEmail),
                        offset, batchSize)).build();
    }

}
