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

package org.sofun.core.member;

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

import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberConnectionLog;

/**
 * Member connection log implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members_connections")
public class MemberConnectionLogImpl implements MemberConnectionLog {

    private static final long serialVersionUID = -9088844855510893915L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "login_time", nullable = false)
    protected Date loginTime;

    @Column(name = "logout_time", nullable = true)
    protected Date logoutTime;

    @Column(name = "remote_addr", nullable = false)
    protected String remoteAddress;

    @Column(name = "session_key", nullable = false)
    protected String sessionKey;

    @ManyToOne(targetEntity = MemberImpl.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    protected Member member;

    public MemberConnectionLogImpl() {
        super();
    }

    public MemberConnectionLogImpl(Member member, Date loginTime, String sessionKey, String remoteAddress) {
        this();
        this.member = member;
        if (loginTime != null) {
            this.loginTime = (Date) loginTime.clone();
        }
        this.remoteAddress = remoteAddress;
        this.sessionKey = sessionKey;
    }

    @Override
    public Date getLoginDate() {
        if (loginTime != null) {
            return (Date) loginTime.clone();
        }
        return null;
    }

    @Override
    public Date getLogoutDate() {
        if (logoutTime != null) {
            return (Date) logoutTime.clone();
        }
        return null;
    }

    @Override
    public String getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public void setLoginDate(Date login) {
        if (login != null) {
            loginTime = (Date) login.clone();
        } else {
            loginTime = null;
        }

    }

    @Override
    public void setLogoutDate(Date logout) {
        if (logout != null) {
            logoutTime = (Date) logout.clone();
        } else {
            logoutTime = null;
        }

    }

    @Override
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    public String getSessionKey() {
        return sessionKey;
    }

    @Override
    public void setSessionId(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public void setMember(Member member) {
        this.member = member;
    }

}
