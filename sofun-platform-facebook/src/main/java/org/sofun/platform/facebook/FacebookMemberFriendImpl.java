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
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sofun.core.api.member.Member;
import org.sofun.core.member.MemberImpl;
import org.sofun.platform.facebook.api.FacebookMemberFriend;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "facebook_member_friends")
public class FacebookMemberFriendImpl implements FacebookMemberFriend {

    private static final long serialVersionUID = -5360461311753424773L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @ManyToOne(targetEntity = MemberImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    protected Member member;

    @ElementCollection
    @CollectionTable(
            name = "facebook_member_friends_ids",
            joinColumns = @JoinColumn(name = "id"))
    @Column(name = "friend_id")
    protected Set<String> friends;

    public FacebookMemberFriendImpl() {

    }
    
    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public void setMember(Member member) {
        this.member = member;
    }

    @Override
    public Set<String> getFriends() {
        if (friends == null) {
            friends = new HashSet<String>();
        }
        return friends;
    }

    @Override
    public void setFriends(Set<String> friends) {
        this.friends = friends;
    }

    @Override
    public void addFriend(String friend) {
        if (!getFriends().contains(friend)) {
            friends.add(friend);
        }
    }

    @Override
    public void removeFriend(String friend) {
        if (getFriends().contains(friend)) {
            friends.remove(friend);
        }
    }

}
