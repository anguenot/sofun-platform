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

package org.sofun.platform.notification;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.sofun.core.api.member.Member;
import org.sofun.core.member.MemberImpl;
import org.sofun.platform.notification.api.MemberNotification;

/**
 * Member notification scheme implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members_notifications")
public class MemberNotificationImpl implements MemberNotification {

    private static final long serialVersionUID = -890510790696903736L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @OneToOne(targetEntity = MemberImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    protected Member member;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(name = "members_notifications_scheme", joinColumns = @JoinColumn(name = "id"))
    protected Map<String, Boolean> scheme = new HashMap<String, Boolean>();

    public MemberNotificationImpl() {
        super();
    }
    
    public MemberNotificationImpl(Member member) {
        this();
        this.member = member;
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
    public Map<String, Boolean> getScheme() {
        return scheme;
    }

    @Override
    public void setScheme(Map<String, Boolean> scheme) {
        this.scheme = scheme;
    }

}
