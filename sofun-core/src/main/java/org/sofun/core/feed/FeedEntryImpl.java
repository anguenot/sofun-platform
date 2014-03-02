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

package org.sofun.core.feed;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sofun.core.api.feed.Feed;
import org.sofun.core.api.feed.FeedEntry;
import org.sofun.core.api.member.Member;
import org.sofun.core.member.MemberImpl;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "feeds_entries")
public class FeedEntryImpl implements FeedEntry {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @ManyToOne(targetEntity = MemberImpl.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    protected Member member;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    protected Date date;

    @Column(name = "content")
    protected String content;

    @ManyToOne(targetEntity = FeedImpl.class)
    @JoinColumn(name = "feed_id", nullable = false)
    protected Feed feed;

    public FeedEntryImpl() {
        super();
    }

    public FeedEntryImpl(Member member, Date date, String content) {
        this();
        setMember(member);
        setDate(date);
        setContent(content);
    }

    @Override
    public int compareTo(FeedEntry o) {
        return getDate().compareTo(o.getDate());
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
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
    public Date getDate() {
        if (date == null) {
            return null;
        }
        return (Date) date.clone();
    }

    @Override
    public void setDate(Date date) {
        if (date != null) {
            this.date = (Date) date.clone();
        }
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof FeedEntry) {
            FeedEntry fe = (FeedEntry) obj;
            return getId() == fe.getId();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getId()).hashCode();
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

}
