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

import org.sofun.core.CoreConstants;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.PasswordResetToken;

/**
 * Password reset token implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members_passwords_reset_tokens")
public class PasswordResetTokenImpl implements PasswordResetToken {

    private static final long serialVersionUID = -6211750116998861038L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @OneToOne(targetEntity = MemberImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    protected Member member;

    @Column(name = "token", nullable = false)
    protected String token;

    @Column(name = "created", nullable = false)
    protected Date created;

    public PasswordResetTokenImpl() {
        super();
    }

    public PasswordResetTokenImpl(Member member, String token) {
        this.member = member;
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public Date getCreated() {
        if (created != null) {
            return (Date) created.clone();
        }
        return null;
    }

    @PrePersist
    protected void onCreate() {
        this.created = Calendar.getInstance().getTime();
    }

    @Override
    public Date getExpired() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(created);
        cal.add(Calendar.SECOND, CoreConstants.PASSWORD_RESET_EXPIRATION);
        return cal.getTime();
    }

    @Override
    public boolean isExpired() {
        Calendar now = Calendar.getInstance();
        Calendar expired = Calendar.getInstance();
        expired.setTime(getExpired());
        return now.after(expired);
    }

}
