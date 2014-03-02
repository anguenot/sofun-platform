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

package org.sofun.platform.web.rest.api.prediction.get;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sofun.core.api.prediction.contestant.PredictionOrderedContestantsList;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.platform.web.rest.api.member.ReSTMember;
import org.sofun.platform.web.rest.api.sport.ReSTSportContestant;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTPredictionOrderedContestantsList implements Serializable {

    private static final long serialVersionUID = 8170905423398530502L;

    protected Date created;

    protected Date modified;

    protected ReSTMember member;

    protected List<ReSTSportContestant> contestants = new ArrayList<ReSTSportContestant>();

    public ReSTPredictionOrderedContestantsList() {

    }

    public ReSTPredictionOrderedContestantsList(
            PredictionOrderedContestantsList corePrediction, long seasonId) {
        this();
        if (corePrediction != null) {
            setCreated(corePrediction.getCreated());
            setModified(corePrediction.getLastModified());
            setMember(new ReSTMember(corePrediction.getMember()));
            for (SportContestant corePlayer : corePrediction.getContestants()) {
                getContestants().add(new ReSTSportContestant(corePlayer, seasonId));
            }
        }
    }

    public Date getCreated() {
        if (created == null) {
            return null;
        }
        return (Date) created.clone();
    }

    public void setCreated(Date created) {
        if (created != null) {
            this.created = (Date) created.clone();
        }
    }

    public Date getModified() {
        if (modified == null) {
            return null;
        }
        return (Date) modified.clone();
    }

    public void setModified(Date modified) {
        if (modified != null) {
            this.modified = (Date) modified.clone();
        }
    }

    public ReSTMember getMember() {
        return member;
    }

    public void setMember(ReSTMember member) {
        this.member = member;
    }

    public List<ReSTSportContestant> getContestants() {
        return contestants;
    }

    public void setContestants(List<ReSTSportContestant> contestants) {
        this.contestants = contestants;
    }

}
