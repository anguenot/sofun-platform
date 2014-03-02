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

package org.sofun.core.prediction.contestant;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.sofun.core.api.prediction.PredictionResult;
import org.sofun.core.api.prediction.contestant.PredictionOrderedContestantResult;
import org.sofun.core.api.prediction.contestant.PredictionOrderedContestantsList;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.prediction.PredictionImpl;
import org.sofun.core.sport.SportContestantImpl;

/**
 * Ordered list of contestants.
 * 
 * <p />
 * 
 * This is the application responsability to handle duplicates or not. This implemention only ensures position of contestants.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "predictions_ordered_contestants")
@Inheritance(strategy = InheritanceType.JOINED)
public class PredictionOrderedContestantsListImpl extends PredictionImpl implements PredictionOrderedContestantsList {

    private static final long serialVersionUID = -4911893687099164384L;

    @ManyToMany(targetEntity = SportContestantImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "predictions_ordered_contestants_positions", joinColumns = { @JoinColumn(name = "prediction_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "contestant_id", referencedColumnName = "id") })
    @OrderColumn(name = "position")
    protected List<SportContestant> contestants;
    
    @Column(
            name = "drawn",
            nullable = false,
            columnDefinition = "boolean default false")
    protected boolean drawn = false;

    public PredictionOrderedContestantsListImpl() {
        super();
    }

    @Override
    public List<SportContestant> getContestants() {
        if (contestants == null) {
            contestants = new ArrayList<SportContestant>();
        }
        return contestants;
    }

    @Override
    public void setContestants(List<SportContestant> contestants) {
        this.contestants = contestants;
    }

    @Override
    public void addContestant(SportContestant contestant) {
        getContestants().add(contestant);
    }

    @Override
    public void reset() {
        if (contestants != null) {
            contestants.clear();
        }
    }

    @Override
    public PredictionOrderedContestantResult getResultsForPosition(int position) {
        PredictionOrderedContestantResult found = null;
        for (PredictionResult each : getResults()) {
            if ("position".equals(each.getLabel()) && each instanceof PredictionOrderedContestantResult) {
                PredictionOrderedContestantResult one = (PredictionOrderedContestantResult) each;
                if (position == one.getPosition()) {
                    return one;
                }
            }
        }
        return found;
    }

    @Override
    public boolean isDrawn() {
        return drawn;
    }

    @Override
    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }
    
}
