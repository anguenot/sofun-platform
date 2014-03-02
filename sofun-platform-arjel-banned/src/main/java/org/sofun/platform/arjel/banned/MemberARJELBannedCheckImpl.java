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

package org.sofun.platform.arjel.banned;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sofun.core.api.member.Member;
import org.sofun.core.member.MemberImpl;
import org.sofun.platform.arjel.banned.api.MemberARJELBannedCheck;

/**
 * Member ARJEL Banned Check Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members_arjel_banned_checks")
public class MemberARJELBannedCheckImpl implements MemberARJELBannedCheck {

    private static final long serialVersionUID = -3700156588541278080L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @OneToOne(targetEntity = MemberImpl.class, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "member_id",
            unique = true,
            nullable = false,
            updatable = false)
    private Member member;

    @Column(name = "first_check")
    @Temporal(TemporalType.TIMESTAMP)
    private Date firstChecked;

    @Column(name = "last_check")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChecked;
    
    @Column(name = "banned")
    private boolean banned = false;

    public MemberARJELBannedCheckImpl() {
    }

    public MemberARJELBannedCheckImpl(Member member) {
        this.member = member;
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public Date getFirstCheckTime() {
        return firstChecked;
    }

    @Override
    public Date getLastCheckTime() {
        return lastChecked;
    }

    @Override
    public void setLastCheckTime(Date date) {
        if (date != null) {
            lastChecked = (Date) date.clone();
        }
    }

    @PrePersist
    protected void onCreate() {
        Date now = Calendar.getInstance().getTime();
        firstChecked = now;
        lastChecked = now;
    }

    @Override
    public boolean isBanned() {
        return banned;
    }

    @Override
    public void setBanned(boolean banned) {
        this.banned = banned;
    }

}
