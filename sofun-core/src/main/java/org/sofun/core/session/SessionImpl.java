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

package org.sofun.core.session;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.sofun.core.CoreConstants;
import org.sofun.core.api.session.Session;

/**
 * Core session implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sessions")
public class SessionImpl implements Session {

    private static final long serialVersionUID = 8876216010980817301L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "integer")
    protected long id;

    @Column(name = "key", nullable = false, unique = true)
    protected String key;

    @Column(name = "created", nullable = false)
    protected Date created;

    @Column(name = "renewed", nullable = false)
    protected Date renewed;

    @Column(name = "remote_address")
    protected String remoteAddress;

    public SessionImpl() {
        super();
    }

    public SessionImpl(String key) {
        this();
        setKey(key);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
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
        this.created = this.renewed = Calendar.getInstance().getTime();
    }

    @Override
    public Date getExpired() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getRenewed());
        cal.add(Calendar.SECOND, CoreConstants.SESSION_TIMEOUT);
        return cal.getTime();
    }

    @Override
    public boolean isExpired() {
        Calendar now = Calendar.getInstance();
        Calendar expired = Calendar.getInstance();
        expired.setTime(getExpired());
        return now.after(expired);
    }

    @Override
    public void renew() {
        this.renewed = new Date();
    }

    @Override
    public Date getRenewed() {
        if (renewed != null) {
            return (Date) renewed.clone();
        }
        return null;
    }

    @Override
    public String getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

}
