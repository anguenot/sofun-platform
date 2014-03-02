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

package org.sofun.platform.web.rest.api.prediction.results;

import java.io.Serializable;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.platform.web.rest.api.sport.ReSTSportContestant;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTPredictionContestantResult implements Serializable {

    private static final long serialVersionUID = 8745525321135775339L;

    protected ReSTSportContestant prediction;

    protected ReSTSportContestant actual;

    protected int points = 0;

    protected double percentageSuccess = 0.0;

    public ReSTPredictionContestantResult() {
        super();
    }

    public ReSTPredictionContestantResult(SportContestant prediction,
            SportContestant actual, int points, double percentageSuccess, long seasonId) {
        this();
        if (prediction != null) {
            this.prediction = new ReSTSportContestant(prediction, seasonId);
        }
        if (actual != null) {
            this.actual = new ReSTSportContestant(actual, seasonId);
        }
        this.points = points;
        this.percentageSuccess = percentageSuccess;
    }

    public ReSTSportContestant getPrediction() {
        return prediction;
    }

    public void setPrediction(ReSTSportContestant prediction) {
        this.prediction = prediction;
    }

    public ReSTSportContestant getActual() {
        return actual;
    }

    public void setActual(ReSTSportContestant actual) {
        this.actual = actual;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public double getPercentageSuccess() {
        return percentageSuccess;
    }

    public void setPercentageSuccess(double percentageSuccess) {
        this.percentageSuccess = percentageSuccess;
    }

}
