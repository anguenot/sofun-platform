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

package org.sofun.platform.facebook;

import java.util.HashSet;
import java.util.List;
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
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberGender;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.api.member.ejb.MemberServiceLocal;
import org.sofun.platform.facebook.api.FacebookMemberFriend;
import org.sofun.platform.facebook.api.FacebookMemberInfo;
import org.sofun.platform.facebook.api.FacebookMemberLike;
import org.sofun.platform.facebook.api.FacebookService;
import org.sofun.platform.facebook.api.local.FacebookServiceLocal;
import org.sofun.platform.facebook.api.remote.FacebooKServiceRemote;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookResponseStatusException;
import com.restfb.types.User;

/**
 * Facebook Service.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(FacebookServiceLocal.class)
@Remote(FacebooKServiceRemote.class)
public class FacebookServiceImpl implements FacebookService {

    private static final long serialVersionUID = 321817429577181101L;

    private static final Log log = LogFactory.getLog(FacebookServiceImpl.class);

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    @EJB(beanName = "MemberServiceImpl", beanInterface = MemberServiceLocal.class)
    private MemberService memberService;

    public FacebookServiceImpl() {
    }

    public FacebookServiceImpl(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    public Member updateMemberInfoFromFacebook(Member member) throws Exception {
        Member m = getMemberService().getMember(member.getEmail());
        return updateMember(m);
    }

    protected MemberService getMemberService() throws Exception {
        return memberService;
    }

    @Override
    public FacebookMemberInfo getFacebookMemberInfo(Member member) {

        String queryStr = "from " + FacebookMemberInfoImpl.class.getSimpleName() + " l where l.member.facebookId =:fbid";
        Query query = createQuery(queryStr);
        query.setParameter("fbid", member.getFacebookId());

        FacebookMemberInfo info;
        try {
            info = (FacebookMemberInfo) query.getSingleResult();
        } catch (NoResultException nre) {
            // Lazily create entry
            info = new FacebookMemberInfoImpl(member);
            em.persist(info);
        }

        return info;

    }

    protected Member updateMember(Member member) throws Exception {

        User user = getUserFromGraph(member);

        FacebookMemberInfo info = getFacebookMemberInfo(member);
        member.setFacebookId(user.getId());

        //
        // Only update the following fields if not yet initialized.
        //

        if (member.getFirstName() == null || "".equals(member.getFirstName())) {
            member.setFirstName(user.getFirstName());
        }
        info.setFirstName(user.getFirstName());

        if (member.getLastName() == null || "".equals(member.getLastName())) {
            member.setLastName(user.getLastName());
        }
        info.setLastName(user.getLastName());

        if (member.getMiddleNames() == null || "".equals(member.getMiddleNames())) {
            member.setMiddleName(user.getMiddleName());
        }

        if (member.getEmail() == null || "".equals(member.getEmail())) {
            member.setEmail(user.getEmail());
        }

        if (member.getBirthDate() == null) {
            member.setBirthDate(user.getBirthdayAsDate());
        }

        member.setLocale(user.getLocale());
        member.setTimeZone(user.getTimezone());
        member.setAbout(user.getAbout());
        member.setAvatar(FacebookUtils.getPictureFor(Long.valueOf(user.getId()), null));

        if (user.getGender() != null) {
            if (user.getGender().equals("male")) {
                member.setGender(MemberGender.MALE);
            } else if (user.getGender().equals("female")) {
                member.setGender(MemberGender.FEMALE);
            } else {
                member.setGender(MemberGender.UNKNOWN);
            }
        } else {
            member.setGender(MemberGender.UNKNOWN);
        }

        Connection<User> friends = getFriendsFromGraph(member);
        if (friends != null) {
            for (User friend : friends.getData()) {
                addMemberFriend(member, friend.getId());
                Member f = getMemberService().getMemberByFacebookId(friend.getId());
                if (f != null && !member.isFriend(f)) {
                    member.addFriend(f);
                    // f.addFriend(member);
                }
            }
        }

        // Update friends relationship. (Remove the ones that have been unfriended)
        Set<Member> newFriends = new HashSet<Member>();
        Set<Member> coreFriends = member.getFriends();
        synchronized (coreFriends) {
            for (Member friend : coreFriends) {
                if (getMemberFriends(member).getFriends().contains(friend.getFacebookId())) {
                    newFriends.add(friend);
                }
            }
            member.setFriends(newFriends);
        }
        
        return member;
        
    }

    private User getUserFromGraph(Member member) throws Exception {
        FacebookClient fbc = connectToGraph(member);
        return fbc.fetchObject("me", User.class);
    }

    private Connection<User> getFriendsFromGraph(Member member) throws Exception {
        FacebookClient fbc = connectToGraph(member);
        return fbc.fetchConnection("me/friends", User.class);
    }

    private FacebookClient connectToGraph(Member member) throws Exception {
        return new DefaultFacebookClient(member.getFacebookToken());
    }

    /**
     * Connects to the Facebook Graph to check if a member does like the Facebook page.
     * 
     * <p/>
     * 
     * Typically, we use this to check if wether or not a member is fan of a corresponding community Facebook page.
     * 
     * <p/>
     * 
     * On-X Security audit reference: #V13 - #R13 Sofun issue reference: #PF-111
     * 
     * @param member: a Sofun {@link Member}
     * @param pageId: a Facebook page identifier
     * @return true of false.
     * @throws Exception: typically Facebook internal exception.
     */
    private boolean _doesLikePage(Member member, String pageId) throws Exception {

        //
        // #PF-144: on-x audit #1 V13
        //
        // There is no injection risk here.
        //
        // 1. the query is against the Facebook Graph (not the Sofun DB)
        // 2. the Facebook Client (com.restfb) does not provide any API to set query parameters
        // (like we do with JPA queries against our own DB)
        //

        try {
            // Although there is no real danger here let's verify this is an FB page identifier. (long)
            Long.valueOf(pageId);
        } catch (NumberFormatException e) {
            log.error("Invalid Facebook Page ID provided => " + pageId);
            return false;
        }

        try {
            FacebookClient fbc = connectToGraph(member);
            final String queryStr = "SELECT target_id FROM connection WHERE source_id =" + member.getFacebookId()
                    + " AND target_id = " + pageId;
            final List<Boolean> likes = fbc.executeQuery(queryStr, Boolean.class);
            return likes.size() > 0;
        } catch (FacebookResponseStatusException exception) {
            return false;
        }

    }

    @Override
    public void addMemberLike(Member member, String like) throws Exception {

        FacebookMemberLike likes = getMemberLike(member);
        boolean created = false;

        if (likes == null) {
            likes = new FacebookMemberLikeImpl();
            likes.setMember(member);
            created = true;
        } else {
            if (!likes.getLikes().contains(like) && _doesLikePage(member, like)) {
                likes.addLike(like);
            } else if (likes.getLikes().contains(like) && !_doesLikePage(member, like)) {
                // Update if member once liked then later unliked
                likes.removeLike(like);
            }
        }

        if (created == true) {
            em.persist(likes);
        } else {
            em.merge(likes);
        }

    }

    protected Query createQuery(String queryStr) {
        Query query = em.createQuery(queryStr);
        return query;
    }

    @Override
    public FacebookMemberLike getMemberLike(Member member) throws Exception {

        String queryStr = "from " + FacebookMemberLikeImpl.class.getSimpleName() + " l where l.member.email =:email";
        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());

        try {
            return (FacebookMemberLike) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public boolean doesMemberLike(Member member, String pageId) throws Exception {
        em.refresh(member);
        FacebookMemberLike likes = getMemberLike(member);
        if (likes != null) {
            return likes.getLikes().contains(pageId);
        }
        return false;
    }

    @Override
    public void addMemberFriend(Member member, String friend) throws Exception {

        FacebookMemberFriend friends = getMemberFriends(member);
        boolean created = false;

        if (friends == null) {
            friends = new FacebookMemberFriendImpl();
            friends.setMember(member);
            created = true;
        }
        friends.addFriend(friend);

        if (created == true) {
            em.persist(friends);
        } else {
            em.merge(friends);
        }

    }

    @Override
    public FacebookMemberFriend getMemberFriends(Member member) throws Exception {
        String queryStr = "from " + FacebookMemberFriendImpl.class.getSimpleName() + " l where l.member.email =:email";
        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());

        try {
            return (FacebookMemberFriend) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
