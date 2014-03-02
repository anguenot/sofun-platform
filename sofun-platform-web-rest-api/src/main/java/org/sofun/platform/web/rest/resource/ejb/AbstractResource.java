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

import java.util.List;

import javax.ejb.EJB;

import org.sofun.core.api.Core;
import org.sofun.core.api.community.Community;
import org.sofun.core.api.community.CommunityService;
import org.sofun.core.api.feed.FeedService;
import org.sofun.core.api.kup.KupService;
import org.sofun.core.api.local.CommunityServiceLocal;
import org.sofun.core.api.local.CoreLocal;
import org.sofun.core.api.local.FeedServiceLocal;
import org.sofun.core.api.local.KupServiceLocal;
import org.sofun.core.api.local.NotificationServiceLocal;
import org.sofun.core.api.local.PredictionServiceLocal;
import org.sofun.core.api.local.SessionServiceLocal;
import org.sofun.core.api.local.SofunMessagingServiceLocal;
import org.sofun.core.api.local.SportServiceLocal;
import org.sofun.core.api.local.TeamServiceLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.api.member.ejb.MemberServiceLocal;
import org.sofun.core.api.messaging.SofunMessagingService;
import org.sofun.core.api.notification.NotificationService;
import org.sofun.core.api.prediction.PredictionService;
import org.sofun.core.api.session.SessionService;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.TeamService;
import org.sofun.platform.arjel.banned.api.ARJELBannedService;
import org.sofun.platform.arjel.banned.api.ejb.ARJELBannedServiceLocal;
import org.sofun.platform.facebook.api.FacebookService;
import org.sofun.platform.facebook.api.local.FacebookServiceLocal;
import org.sofun.platform.web.rest.api.exception.ReSTException;
import org.sofun.platform.web.rest.api.exception.ReSTRuntimeException;

/**
 * Abstract resource.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public abstract class AbstractResource {

    @EJB(beanName = "CoreImpl", beanInterface = CoreLocal.class)
    private Core core;

    @EJB(beanName = "SportServiceImpl", beanInterface = SportServiceLocal.class)
    private SportService sportService;

    @EJB(beanName = "TeamServiceImpl", beanInterface = TeamServiceLocal.class)
    private TeamService teamService;

    @EJB(
            beanName = "CommunityServiceImpl",
            beanInterface = CommunityServiceLocal.class)
    private CommunityService communityService;

    @EJB(
            beanName = "MemberServiceImpl",
            beanInterface = MemberServiceLocal.class)
    private MemberService memberService;

    @EJB(beanName = "KupServiceImpl", beanInterface = KupServiceLocal.class)
    private KupService kupService;

    @EJB(
            beanName = "FacebookServiceImpl",
            beanInterface = FacebookServiceLocal.class)
    private FacebookService facebookService;

    @EJB(beanName = "FeedServiceImpl", beanInterface = FeedServiceLocal.class)
    private FeedService feedService;

    @EJB(
            beanName = "SofunMessagingServiceImpl",
            beanInterface = SofunMessagingServiceLocal.class)
    private SofunMessagingService messagingService;

    @EJB(
            beanName = "PredictionServiceImpl",
            beanInterface = PredictionServiceLocal.class)
    private PredictionService predictionService;

    @EJB(
            beanName = "SessionServiceImpl",
            beanInterface = SessionServiceLocal.class)
    private SessionService sessionService;

    @EJB(
            beanName = "NotificationServiceImpl",
            beanInterface = NotificationServiceLocal.class)
    private NotificationService notificationService;

    @EJB(
            beanName = "ARJELBannedServiceImpl",
            beanInterface = ARJELBannedServiceLocal.class)
    private ARJELBannedService arjelService;

    protected Core getCore() {
        return core;
    }

    protected SportService getSportService() {
        return sportService;
    }

    protected TeamService getTeamService() {
        return teamService;
    }

    protected CommunityService getCommunityService() {
        return communityService;
    }

    protected MemberService getMemberService() {
        return memberService;
    }

    protected KupService getKupService() {
        return kupService;
    }

    protected FacebookService getFacebookService() {
        return facebookService;
    }

    protected FeedService getFeedService() {
        return feedService;
    }

    protected SofunMessagingService getMessagingService() {
        return messagingService;
    }

    protected PredictionService getPredictionService() {
        return predictionService;
    }

    protected SessionService getSessionService() {
        return sessionService;
    }

    protected NotificationService getNotificationService() {
        return notificationService;
    }

    protected ARJELBannedService getArjelService() {
        return arjelService;
    }

    protected Tournament getCoreTournament(long id) throws ReSTException {
        Tournament t;
        try {
            t = getSportService().getSportTournament(id);
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
        if (t == null) {
            throw new ReSTRuntimeException(400,
                    "Tournament identifier not found");
        }
        return t;
    }

    protected TournamentSeason getCoreTournamentSeasonById(long id)
            throws ReSTException {
        TournamentSeason s;
        try {
            s = getSportService().getTournamentSeason(id);
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
        if (s == null) {
            throw new ReSTRuntimeException(400,
                    "Tournament season identifier not found");
        }
        return s;
    }

    protected TournamentGame getCoreTournamentGameById(String uuid)
            throws ReSTException {
        TournamentGame g;
        try {
            g = getSportService().getTournamentGame(uuid);
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
        if (g == null) {
            throw new ReSTRuntimeException(400,
                    "Tournament game identifier not found");
        }
        return g;
    }

    protected TournamentStage getCoreTournamentStageById(long id)
            throws ReSTException {
        TournamentStage s;
        try {
            s = getSportService().getTournamentStage(id);
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
        if (s == null) {
            throw new ReSTRuntimeException(400,
                    "Tournament stage identifier not found");
        }
        return s;
    }

    protected TournamentRound getCoreTournamentRoundById(long id)
            throws ReSTException {
        TournamentRound r;
        try {
            r = getSportService().getTournamentRound(id);
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
        if (r == null) {
            throw new ReSTRuntimeException(400,
                    "Tournament round identifier not found");
        }
        return r;
    }

    protected Team getCoreTeamById(long communityId, long teamId)
            throws ReSTException {
        Team t;
        try {
            t = getTeamService().getTeam(teamId); // FIXME communities
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
        if (t == null) {
            throw new ReSTRuntimeException(400, "Team identifier not found");
        }
        return t;
    }

    protected Team getCoreTeamByName(long communityId, String teamName)
            throws ReSTException {
        Team t;
        try {
            t = getTeamService().getTeam(teamName); // FIXME communities
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
        if (t == null) {
            throw new ReSTRuntimeException(400, "Team name not found");
        }
        return t;
    }

    protected Member getCoreMemberById(long memberId) throws ReSTException {
        Member m;
        try {
            m = getMemberService().getMember(memberId);
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
        if (m == null) {
            throw new ReSTRuntimeException(400, "Member not found");
        }
        return m;
    }

    protected Member getCoreMemberByFacebookId(String facebookId)
            throws ReSTException {
        Member m;
        try {
            m = getMemberService().getMemberByFacebookId(facebookId);
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
        if (m == null) {
            throw new ReSTRuntimeException(400, "Member not found");
        }
        return m;
    }

    protected Member getCoreMemberByEmail(String memberEmail)
            throws ReSTException {
        Member m;
        try {
            m = getMemberService().getMember(memberEmail);
        } catch (Throwable t) {
            throw new ReSTRuntimeException(500, t.getMessage());
        }
        if (m == null) {
            throw new ReSTRuntimeException(400, "Member not found");
        }
        return m;
    }

    protected Community getCoreCommunityById(Long id) throws ReSTException {
        Community c;
        try {
            c = getCommunityService().getCommunityById(id);
        } catch (Throwable t) {
            throw new ReSTRuntimeException(500, t.getMessage());
        }
        if (c == null) {
            throw new ReSTRuntimeException(400, "Community not found");
        }
        return c;
    }

    protected void pushToFeed(Team team, Member member, final String type,
            final List<String> args) {

        // FIXME

        /*
         * try { final String msg = feedService.getStringFor(type); final String
         * content = String.format(msg, args.toArray());
         * getFeedService().addFeedEntry(team, member, type, content); } catch
         * (Throwable t) { log.error(t.getMessage()); }
         */

    }

}
