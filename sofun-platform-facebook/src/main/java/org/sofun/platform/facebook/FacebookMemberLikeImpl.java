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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.sofun.core.api.member.Member;
import org.sofun.core.member.MemberImpl;
import org.sofun.platform.facebook.api.FacebookMemberLike;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "facebook_member_likes")
public class FacebookMemberLikeImpl implements FacebookMemberLike {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @OneToOne(targetEntity = MemberImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    protected Member member;

    @ElementCollection
    @CollectionTable(
            name = "facebook_member_likes_ids",
            joinColumns = @JoinColumn(name = "id"))
    @Column(name = "like_id")
    protected Set<String> likes;

    public FacebookMemberLikeImpl() {

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
    public Set<String> getLikes() {
        if (likes == null) {
            likes = new HashSet<String>();
        }
        return likes;
    }

    @Override
    public void setLikes(Set<String> likes) {
        this.likes = likes;
    }

    @Override
    public void addLike(String like) {
        if (!getLikes().contains(like)) {
            likes.add(like);
        }
    }

    @Override
    public void removeLike(String like) {
        if (getLikes().contains(like)) {
            likes.remove(like);
        }
    }

}
