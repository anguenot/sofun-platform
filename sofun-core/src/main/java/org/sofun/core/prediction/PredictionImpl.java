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

package org.sofun.core.prediction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.prediction.Prediction;
import org.sofun.core.api.prediction.PredictionResult;
import org.sofun.core.kup.KupImpl;
import org.sofun.core.member.MemberImpl;

/**
 * Base core prediction implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "predictions")
@Inheritance(strategy = InheritanceType.JOINED)
public class PredictionImpl implements Prediction {

    private static final long serialVersionUID = 5544346954390793540L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date created;

    @Column(name = "modified")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modified;

    @ManyToOne(targetEntity = MemberImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    protected Member member;

    @ManyToOne(targetEntity = KupImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "kup_id")
    protected Kup kup;

    @Column(name = "points", columnDefinition = "int default 0")
    protected int points = 0;

    @Column(name = "points_computed", nullable = false, columnDefinition = "boolean default false")
    protected boolean pointsComputed = false;

    @Column(name = "type")
    protected String type;

    @OneToMany(targetEntity = PredictionResultImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected List<PredictionResult> results;

    public PredictionImpl() {

    }

    @Override
    public long getId() {
        return id;
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
    public Date getCreated() {
        if (created == null) {
            return null;
        }
        return (Date) created.clone();
    }

    @Override
    public void setCreated(Date created) {
        if (created != null) {
            this.created = (Date) created.clone();
        }
    }

    @Override
    public Date getLastModified() {
        if (modified == null) {
            return null;
        }
        return (Date) modified.clone();
    }

    @Override
    public void setLastModified(Date date) {
        if (date != null) {
            this.modified = (Date) date.clone();
        }
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public boolean isPointsComputed() {
        return pointsComputed;
    }

    @Override
    public void setPointsComputed(boolean pointsComputed) {
        this.pointsComputed = pointsComputed;
    }

    @PrePersist
    protected void onCreate() {
        Date now = Calendar.getInstance().getTime();
        setCreated(now);
        setLastModified(now);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public List<PredictionResult> getResults() {
        if (results == null) {
            results = new ArrayList<PredictionResult>();
        }
        return results;
    }

    @Override
    public void setResults(List<PredictionResult> results) {
        this.results = results;
    }

    @Override
    public void addResult(PredictionResult result) {
        getResults().add(result);
    }

    @Override
    public Kup getKup() {
        return kup;
    }

    @Override
    public void setKup(Kup kup) {
        this.kup = kup;
    }

}
