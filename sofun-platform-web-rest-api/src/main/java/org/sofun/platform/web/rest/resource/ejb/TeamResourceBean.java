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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.banking.CurrencyType;
import org.sofun.core.api.community.Community;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.feed.Feed;
import org.sofun.core.api.feed.FeedEntryType;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberAccountType;
import org.sofun.core.api.member.MemberTransaction;
import org.sofun.core.api.member.MemberTransactionStatus;
import org.sofun.core.api.member.MemberTransactionType;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.TeamPrivacy;
import org.sofun.core.api.team.TeamTag;
import org.sofun.core.api.team.TeamType;
import org.sofun.core.api.team.table.TeamRankingTable;
import org.sofun.core.member.MemberTransactionImpl;
import org.sofun.core.team.TeamTagImpl;
import org.sofun.core.team.TeamTypeImpl;
import org.sofun.platform.web.rest.api.exception.ReSTException;
import org.sofun.platform.web.rest.api.exception.ReSTRuntimeException;
import org.sofun.platform.web.rest.api.exception.ReSTValidationException;
import org.sofun.platform.web.rest.api.feed.ReSTFeed;
import org.sofun.platform.web.rest.api.kup.ReSTKup;
import org.sofun.platform.web.rest.api.member.ReSTMember;
import org.sofun.platform.web.rest.api.team.ReSTMemberRankingTable;
import org.sofun.platform.web.rest.api.team.ReSTTeam;
import org.sofun.platform.web.rest.api.team.ReSTTeamTag;
import org.sofun.platform.web.rest.api.team.ReSTTeamType;
import org.sofun.platform.web.rest.resource.ejb.api.TeamResource;
import org.sofun.platform.web.rest.util.JSONUtil;

/**
 * Team (aka "rooms") Web Resources implementation
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 */
@Stateless
public class TeamResourceBean extends AbstractResource implements TeamResource {

    private static final long serialVersionUID = 6076271033878753239L;

    private static final Log log = LogFactory.getLog(TeamResourceBean.class);

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    public TeamResourceBean() {
        super();
    }

    @Override
    public Response existsTeam(String teamName) throws ReSTException {
        try {
            final String decodedName = URLDecoder.decode(teamName, "UTF-8");
            Team t = getTeamService().getTeam(decodedName);
            if (t != null) {
                return Response.status(202).entity(decodedName).build();
            }
            return Response.status(400).entity(decodedName).build();
        } catch (UnsupportedEncodingException e) {
            throw new ReSTException(e.getMessage());
        }
    }

    @Override
    public Response addTeamMember(long teamId, Map<String, String> params)
            throws ReSTException {
        try {
            Team t = getTeamService().getTeam(teamId);
            if (t == null) {
                throw new ReSTRuntimeException(400, "Team not found!");
            }
            final String email = params.get("email");
            Member m = getMemberService().getMember(email);
            if (m == null) {
                throw new ReSTRuntimeException(400, "Member not found!");
            }
            final String privacy = t.getPrivacy();
            if ((privacy.equals(TeamPrivacy.PRIVATE) || privacy
                    .equals(TeamPrivacy.PRIVATE_GAMBLING_FR))
                    && !"".equals(t.getPassword())) {
                final String password = params.get("password");
                if (password == null || !t.verifyPassword(password)) {
                    return Response.status(401)
                            .entity("Password does not match.").build();
                }
            }

            if ((TeamPrivacy.PUBLIC_GAMBLING_FR.equals(privacy) || TeamPrivacy.PRIVATE_GAMBLING_FR
                    .equals(privacy))
                    && !m.getAccountType()
                            .equals(MemberAccountType.GAMBLING_FR)) {
                return Response
                        .status(401)
                        .entity("Member must have a GAMBLING_FR account type to join the team.")
                        .build();
            }
            // Only add member if not there yet.
            if (!t.isMember(m)) {

                if (t.getId() == 2724482) {
                    if (!m.getEmail().endsWith("20minutes.fr")) {
                        return Response
                                .status(401)
                                .entity("Member must have @20minutes.fr account to join the team.")
                                .build();
                    }
                }

                t.addMember(m);

                // FIXME make this generic and factore out.
                // Room join based bonus.
                if (t.getName().equals("Rue Des Joueurs")) {
                    final String label = "Room : " + t.getName();
                    boolean exists = false;
                    for (MemberTransaction txn : getMemberService()
                            .getBonusCreditHistory(m)) {
                        if (label.equals(txn.getLabel())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        final int bonusAmount = 5;
                        MemberTransaction txn = new MemberTransactionImpl("0",
                                "0", new Date(), bonusAmount,
                                CurrencyType.EURO,
                                MemberTransactionType.BONUS_CREDIT);
                        txn.setStatusCode("00000");
                        txn.setStatus(MemberTransactionStatus.APPROVED);
                        txn.setLabel(label);
                        txn.setCredit(true);
                        txn.setBonus(true);
                        m.addTransaction(txn);
                        txn.setMember(m);
                    }
                } else if (t.getName().equals("SPORT BUZZ BUSINESS")) {
                    final String label = "Room : " + t.getName();
                    boolean exists = false;
                    for (MemberTransaction txn : getMemberService()
                            .getBonusCreditHistory(m)) {
                        if (label.equals(txn.getLabel())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        final int bonusAmount = 2;
                        MemberTransaction txn = new MemberTransactionImpl("0",
                                "0", new Date(), bonusAmount,
                                CurrencyType.EURO,
                                MemberTransactionType.BONUS_CREDIT);
                        txn.setStatusCode("00000");
                        txn.setStatus(MemberTransactionStatus.APPROVED);
                        txn.setLabel(label);
                        txn.setCredit(true);
                        txn.setBonus(true);
                        m.addTransaction(txn);
                        txn.setMember(m);
                    }
                } else if (t.getName().equals("TesterTout.com")) {
                    final String label = "Room : " + t.getName();
                    boolean exists = false;
                    for (MemberTransaction txn : getMemberService()
                            .getBonusCreditHistory(m)) {
                        if (label.equals(txn.getLabel())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        final int bonusAmount = 2;
                        MemberTransaction txn = new MemberTransactionImpl("0",
                                "0", new Date(), bonusAmount,
                                CurrencyType.EURO,
                                MemberTransactionType.BONUS_CREDIT);
                        txn.setStatusCode("00000");
                        txn.setStatus(MemberTransactionStatus.APPROVED);
                        txn.setLabel(label);
                        txn.setCredit(true);
                        txn.setBonus(true);
                        m.addTransaction(txn);
                        txn.setMember(m);
                    }

                } else if (t.getName().equals("Kuzeo")) {
                    final String label = "Room : " + t.getName();
                    boolean exists = false;
                    for (MemberTransaction txn : getMemberService()
                            .getBonusCreditHistory(m)) {
                        if (label.equals(txn.getLabel())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        final int bonusAmount = 2;
                        MemberTransaction txn = new MemberTransactionImpl("0",
                                "0", new Date(), bonusAmount,
                                CurrencyType.EURO,
                                MemberTransactionType.BONUS_CREDIT);
                        txn.setStatusCode("00000");
                        txn.setStatus(MemberTransactionStatus.APPROVED);
                        txn.setLabel(label);
                        txn.setCredit(true);
                        txn.setBonus(true);
                        m.addTransaction(txn);
                        txn.setMember(m);
                    }

                } else if (t.getName().equals("Poker-mania")) {
                    final String label = "Room : " + t.getName();
                    boolean exists = false;
                    for (MemberTransaction txn : getMemberService()
                            .getBonusCreditHistory(m)) {
                        if (label.equals(txn.getLabel())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        final int bonusAmount = 2;
                        MemberTransaction txn = new MemberTransactionImpl("0",
                                "0", new Date(), bonusAmount,
                                CurrencyType.EURO,
                                MemberTransactionType.BONUS_CREDIT);
                        txn.setStatusCode("00000");
                        txn.setStatus(MemberTransactionStatus.APPROVED);
                        txn.setLabel(label);
                        txn.setCredit(true);
                        txn.setBonus(true);
                        m.addTransaction(txn);
                        txn.setMember(m);
                    }

                }

                final long communityId = Long
                        .valueOf(params.get("communityId"));
                Community c = getCoreCommunityById(communityId);
                List<String> args = new ArrayList<String>();
                args.add(t.getName());

                if (c.isCommunityFeedOnly()) {
                    pushToFeed(c.getDefaultTeam(), m,
                            FeedEntryType.NEW_TEAM_MEMBER, args);
                } else {
                    pushToFeed(t, m, FeedEntryType.NEW_TEAM_MEMBER, args);
                }
            }

        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }

        return Response.status(202).entity(null).build();
    }

    @Override
    public Response getTeam(long teamId) throws ReSTException {
        Team coreTeam = getTeamService().getTeam(teamId);
        final ReSTTeam team = new ReSTTeam(coreTeam);
        return Response.status(202).entity(team).build();
    }

    @Override
    public Response getTeamByName(String name) throws ReSTException {
        try {
            final String decodedName = URLDecoder.decode(name, "UTF-8");
            Team coreTeam = getTeamService().getTeam(decodedName);
            final ReSTTeam team = new ReSTTeam(coreTeam);
            return Response.status(202).entity(team).build();
        } catch (UnsupportedEncodingException e) {
            throw new ReSTException(e.getMessage());
        }
    }

    @Override
    public Response addTeam(Map<String, String> params) throws ReSTException {

        // XXX a validator should be performing this check.
        if (params == null) {
            throw new ReSTValidationException("No Form has been submitted!");
        }

        final long communityId = Long.valueOf(params.get("communityId"));
        Community c = getCoreCommunityById(communityId);

        String teamName;
        try {
            teamName = URLDecoder.decode(params.get("name"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ReSTValidationException(500, e.getMessage());
        }

        // XXX a validator should be performing this check.
        if (teamName == null) {
            throw new ReSTValidationException(400, "Name not found!");
        }

        ReSTTeam t;
        try {
            t = new ReSTTeam(getCoreTeamByName(communityId, teamName));
        } catch (ReSTException re) {
            t = null;
        }

        // Team already exists.
        if (t != null) {
            return Response.status(304).entity(t).build();
        }

        try {

            boolean hasRankingTable = true;
            if (c.isCommunityRankingOnly()) {
                hasRankingTable = false;
            }

            Team team = getTeamService().createTeam(teamName, hasRankingTable);
            team.setDescription(params.get("description"));
            team.setAvatar(params.get("avatar"));

            team.setPrivacy(params.get("accessPolicy"));

            final String password = params.get("password");
            if ((team.getPrivacy().equals(TeamPrivacy.PRIVATE) || team
                    .getPrivacy().equals(TeamPrivacy.PRIVATE_GAMBLING_FR))
                    && password != null) {
                try {
                    team.setPassword(password);
                } catch (CoreException e) {
                    return Response.status(400).entity(e.getMessage()).build();
                }
            }

            final String tags = params.get("tags");
            String[] ltags = tags.split(" ");
            for (int i = 0; i < ltags.length; i++) {
                final String name = URLDecoder.decode(ltags[i].toLowerCase(),
                        "UTF-8");
                TeamTag itag = getTeamService().getTeamTag(name);
                if (itag == null) {
                    itag = new TeamTagImpl(name);
                    itag.setScore(1);
                    em.persist(itag);
                } else {
                    itag.setScore(itag.getScore() + 1);
                }
                team.addTag(itag);
            }

            final String types = params.get("types");
            String[] ltypes = types.split(" ");
            for (int i = 0; i < ltypes.length; i++) {
                // Handle dirt from betkup here ("[]")
                if (ltypes[i] == null || ltypes[i].equals("[]")) {
                    continue;
                }
                TeamType type = getTeamService().getTeamType(ltypes[i]);
                if (type != null) {
                    team.addType(type);
                } else {
                    log.error("Cannot find team type with name=" + ltypes[i]);
                }
            }

            Member m = getCoreMemberByEmail(params.get("adminEmail"));

            team.addAdmin(m);
            team.addMember(m);

            c.addTeam(team);
            Team newTeam = getCoreTeamByName(communityId, teamName);

            // XXX move this at lower level
            List<String> args = new ArrayList<String>();
            // args.add(newTeam.getName());
            pushToFeed(c.getDefaultTeam(), m, FeedEntryType.NEW_TEAM, args);

            return Response.status(202).entity(new ReSTTeam(newTeam)).build();
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }

    }

    @Override
    public Response editTeam(Map<String, String> params) throws ReSTException {

        final long communityId = Long.valueOf(params.get("communityId"));
        final long teamId = Long.valueOf(params.get("teamId"));
        final String description = params.get("description");
        final String policy = params.get("accessPolicy");
        final String password = params.get("password");
        final String avatar = params.get("avatar");

        final Team team = getCoreTeamById(communityId, teamId);

        team.setDescription(description);
        if (avatar != null) {
            team.setAvatar(avatar);
        }

        team.setPrivacy(policy);
        if ((policy.equals(TeamPrivacy.PRIVATE) || policy
                .equals(TeamPrivacy.PRIVATE_GAMBLING_FR)) && password != null) {
            try {
                team.setPassword(password);
            } catch (CoreException e) {
                return Response.status(400).entity(e.getMessage()).build();
            }
        }

        // Get current team tag names
        List<String> existing = new ArrayList<String>();
        for (TeamTag coreTag : team.getTags()) {
            existing.add(coreTag.getName());
        }

        final String tags = params.get("tags");
        String[] ltags = tags.split(" ");
        for (int i = 0; i < ltags.length; i++) {

            final String name = ltags[i].toLowerCase();
            TeamTag itag = getTeamService().getTeamTag(name);

            // If the tag does not exist it means the team does not have it.
            if (itag == null) {
                itag = new TeamTagImpl(name);
                itag.setScore(1);
                em.persist(itag);
            } else {
                // Tag already set on this team.
                if (!team.getTags().contains(itag)) {
                    itag.setScore(itag.getScore() + 1);
                }
            }
            team.addTag(itag);

        }

        // Remove tags.
        List<String> newTags = Arrays.asList(ltags);
        for (String each : existing) {
            if (!newTags.contains(each)) {
                team.delTag(new TeamTagImpl(each));
            }
        }

        // Get current team type names
        existing = new ArrayList<String>();
        for (TeamType coreType : team.getTypes()) {
            existing.add(coreType.getName());
        }

        final String types = params.get("types");
        String[] ltypes = types.split(" ");
        for (int i = 0; i < ltypes.length; i++) {
            TeamType type = getTeamService().getTeamType(ltypes[i]);
            if (type != null) {
                team.addType(type);
            }
        }

        // Remove types.
        List<String> newTypes = Arrays.asList(ltypes);
        for (String each : existing) {
            if (!newTypes.contains(each)) {
                team.delType(new TeamTypeImpl(each));
            }
        }

        try {
            getTeamService().updateTeam(team);
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

        return Response.status(202).entity(null).build();
    }

    @Override
    public Response delTeam(Map<String, String> params) throws ReSTException {
        final long teamId = Long.valueOf(params.get("teamId"));

        Team coreTeam = getTeamService().getTeam(teamId);
        if (coreTeam == null) {
            return Response.status(400).entity("Team not found.").build();
        }
        if (getTeamService().isTeamRemovable(coreTeam)) {
            coreTeam = getTeamService().removeTeam(coreTeam);
            return Response.status(202).entity("Team deleted successfully.")
                    .build();
        } else {
            return Response.status(500).entity("Team cannot be deleted.")
                    .build();
        }
    }

    @Override
    public Response getTeamRankingTable(long teamId, int offset, int batchSize)
            throws ReSTException {

        Team team = getTeamService().getTeam(teamId);

        // XXX we only supports only one to one relationship right now.
        Community c = null;
        if (team.getCommunities().size() > 0) {
            c = team.getCommunities().iterator().next();
        }

        TeamRankingTable table;
        if (c != null && c.isCommunityRankingOnly()) {
            // Ranking table only at community level. We will filter community
            // table ranking entries based on team members.
            table = (TeamRankingTable) c.getDefaultTeam().getRankingTable();
        } else {
            table = team.getTeamRanking();
        }

        List<ReSTMember> teamMembers = new ArrayList<ReSTMember>();
        Iterator<Member> itMembers = team.getMembers().iterator();
        while (itMembers.hasNext()) {
            teamMembers.add(new ReSTMember(itMembers.next()));
        }

        ReSTMemberRankingTable rtable = new ReSTMemberRankingTable(table,
                offset, batchSize, null, false, teamMembers);
        return Response.status(202).entity(rtable).build();
    }

    @Override
    public Response delTeamMember(long teamId, Map<String, String> params)
            throws ReSTException {
        final String email = params.get("email");
        Team team = getTeamService().getTeam(teamId);
        if (team == null) {
            throw new ReSTRuntimeException(400, "Team not found!");
        }
        Member member;
        try {
            member = getMemberService().getMember(email);
            team.removeMember(member);
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
        return Response.status(202).entity(null).build();
    }

    @Override
    public Response getTeamTags(int limit) {
        final List<ReSTTeamTag> tags = new ArrayList<ReSTTeamTag>();
        for (TeamTag coreTag : getTeamService().getTeamTags(limit)) {
            tags.add(new ReSTTeamTag(coreTag));
        }
        return Response.status(202).entity(tags).build();
    }

    @Override
    public Response getTeamTypes() {
        final List<ReSTTeamType> types = new ArrayList<ReSTTeamType>();
        for (TeamType coreType : getTeamService().getTeamTypes()) {
            types.add(new ReSTTeamType(coreType));
        }
        return Response.status(202).entity(types).build();
    }

    @Override
    public Response getTeamPrivacyTypes() {
        final Set<String> types = getTeamService().getTeamPrivacyTypes();
        return Response.status(202).entity(types).build();
    }

    @Override
    public Response searchTeams(Map<String, String> params)
            throws ReSTException {

        final List<ReSTTeam> teams = new ArrayList<ReSTTeam>();

        Set<Team> coreTeams = null;
        try {
            coreTeams = getTeamService().searchTeams(params);
        } catch (CoreException e) {
            throw new ReSTRuntimeException(e.getMessage());
        }

        for (Team coreTeam : coreTeams) {
            teams.add(new ReSTTeam(coreTeam, false));
        }

        return Response.status(202).entity(teams).build();

    }

    @Override
    public Response getTeamCredentialsFor(long teamId, String email) {

        final Team team = getTeamService().getTeam(teamId);
        final Member member = getMemberService().getMember(email);
        final Set<String> credentials = getTeamService().getCredentials(team,
                member);

        return Response.status(202).entity(credentials).build();

    }

    @Override
    public Response getTeamFeed(long teamId, int offset, int size)
            throws ReSTException {

        final Team team = getTeamService().getTeam(teamId);
        final Feed feed = team.getActivityFeed();

        return Response.status(202).entity(new ReSTFeed(feed, offset, size))
                .build();

    }

    @Override
    public Response addTeamKup(long teamId, Map<String, String> params)
            throws ReSTException {

        final Team team = getTeamService().getTeam(teamId);
        if (team == null) {
            return Response.status(400).entity("Team not found").build();
        }

        final long metaKupId = Long.valueOf(params.get("metaKupId"));
        Kup kup = getKupService().getKupById(metaKupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        final float stake = Float.valueOf(params.get("stake"));
        final byte repartitionType = Byte
                .valueOf(params.get("repartitionType"));

        if (team.getKupByName(kup.getName()) != null) {
            return Response.status(500)
                    .entity("Your room is already registered to this Kup.")
                    .build();
        }

        Kup newKup;
        try {
            newKup = getKupService().createKupFromMeta(kup, team, stake,
                    repartitionType);
        } catch (CoreException e) {
            return Response.status(500).entity("Internal error").build();
        }
        return Response.status(202).entity(new ReSTKup(newKup)).build();

    }

    @Override
    public Response delTeamKup(long teamId, Map<String, String> params)
            throws ReSTException {

        final Team team = getTeamService().getTeam(teamId);
        if (team == null) {
            return Response.status(400).entity("Team not found").build();
        }

        final long kupId = Long.valueOf(params.get("kupId"));
        Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        if (kup.getParticipants().size() > 0) {
            return Response.status(500)
                    .entity("Impossible to delete a Kup w/ participants")
                    .build();
        }

        try {
            team.delKup(kup);
        } catch (CoreException e) {
            return Response.status(500).entity("Internal error").build();
        }
        return Response.status(202).entity(true).build();

    }

    @Override
    public Response getTeamKups(long teamId, int offset, int size)
            throws ReSTException {

        final Team team = getTeamService().getTeam(teamId);
        if (team == null) {
            return Response.status(400).entity("Team not found").build();
        }

        List<ReSTKup> kups = new ArrayList<ReSTKup>();
        List<Kup> coreKups;
        try {
            coreKups = getTeamService().getKupsFor(team, offset, size);
        } catch (CoreException e) {
            return Response.status(500).entity(kups).build();
        }
        for (Kup coreKup : coreKups) {
            kups.add(new ReSTKup(coreKup));
        }
        return Response.status(202).entity(kups).build();

    }

    @Override
    public Response invite(long teamId, Map<String, String> params)
            throws ReSTException {

        final Team team = getTeamService().getTeam(teamId);
        if (team == null) {
            return Response.status(400).entity("Team not found").build();
        }

        final String inviter = params.get("inviter_email");
        Member member = getMemberService().getMember(inviter);
        if (member == null) {
            return Response.status(400).entity("Inviter member not found")
                    .build();
        }

        final Map<String, String> recipients = JSONUtil.getMapFromJSON(params
                .get("email_rcpts"));

        Map<String, String> emailing = new HashMap<String, String>();
        emailing.put("subject", params.get("email_subject"));
        emailing.put("body", params.get("email_body"));
        emailing.put("emails", StringUtils.join(recipients.keySet(), ','));

        try {
            getTeamService().invite(team, member, emailing);
        } catch (CoreException e) {
            return Response.status(500)
                    .entity("An error occured while sending invites").build();
        }

        return Response.status(202).entity(true).build();
    }

    @Override
    public Response countSearchTeams(Map<String, String> params)
            throws ReSTException {
        int count = 0;
        try {
            count = getTeamService().countSearchTeams(params);
        } catch (CoreException e) {
            return Response.status(202).entity("Internal error").build();
        }
        return Response.status(202).entity(count).build();
    }

    @Override
    public Response getTeamRankingTableFor(long teamId, String email,
            int offset, int batchSize) throws ReSTException {

        Team team = getTeamService().getTeam(teamId);

        // XXX we only supports only one to one relationship right now.
        Community c = null;
        if (team.getCommunities().size() > 0) {
            c = team.getCommunities().iterator().next();
        }

        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        TeamRankingTable table;
        if (c != null && c.isCommunityRankingOnly()) {
            // Ranking table only at community level. We will filter community
            // table ranking entries based on team members.
            table = (TeamRankingTable) c.getDefaultTeam().getRankingTable();
        } else {
            table = team.getTeamRanking();
        }

        ReSTMemberRankingTable rtable = new ReSTMemberRankingTable(table,
                offset, batchSize, new ReSTMember(member));
        return Response.status(202).entity(rtable).build();
    }

    @Override
    public Response getTeamRankingTableFacebookFor(long teamId, String email,
            int offset, int batchSize) throws ReSTException {

        Team team = getTeamService().getTeam(teamId);

        // XXX we only supports only one to one relationship right now.
        Community c = null;
        if (team.getCommunities().size() > 0) {
            c = team.getCommunities().iterator().next();
        }

        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        TeamRankingTable table;
        if (c != null && c.isCommunityRankingOnly()) {
            // Ranking table only at community level. We will filter community
            // table ranking entries based on team members.
            table = (TeamRankingTable) c.getDefaultTeam().getRankingTable();
        } else {
            table = team.getTeamRanking();
        }

        ReSTMemberRankingTable rtable = new ReSTMemberRankingTable(table,
                offset, batchSize, new ReSTMember(member), true, null);
        return Response.status(202).entity(rtable).build();

    }

    @Override
    public Response getTeamCredentialsAndPrivacyFor(long teamId, String email) {
        List<String> security = new ArrayList<String>();
        final Team team = getTeamService().getTeam(teamId);
        security.add(team.getPrivacy());
        final Member member = getMemberService().getMember(email);
        if (member != null) {
            security.addAll(getTeamService().getCredentials(team, member));

        }
        return Response.status(202).entity(security).build();
    }

}
