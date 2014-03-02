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

package org.sofun.platform.legigame;

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
import org.sofun.platform.legigame.api.MemberLegigameStatus;

/**
 * Member legigame status implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members_legigame_status")
public class MemberLegigameStatusImpl implements MemberLegigameStatus {

    private static final long serialVersionUID = 5263294768898874867L;

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

    @Column(name = "status")
    private String status;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "iteration", columnDefinition = "int default 0")
    private int iteration;

    public MemberLegigameStatusImpl() {
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public String getDocumentStatus() {
        return status;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public Date getUpdated() {
        return modified;
    }

    @Override
    public void setMember(Member member) {
        this.member = member;
    }

    @Override
    public void setDocumentStatus(String status) {
        this.status = status;
    }

    @Override
    public void setLastUpdated(Date lastUpdated) {
        this.modified = lastUpdated;
    }

    @PrePersist
    protected void onCreate() {
        Date now = Calendar.getInstance().getTime();
        created = now;
        modified = now;
    }

    @Override
    public int getIteration() {
        return iteration;
    }

    @Override
    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

}
