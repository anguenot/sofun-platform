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
import java.util.ArrayList;
import java.util.List;

import org.sofun.core.api.prediction.contestant.PredictionOrderedContestantResult;
import org.sofun.core.api.prediction.contestant.PredictionOrderedContestantsList;
import org.sofun.core.api.sport.SportContestant;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTPredictionOrderedContestantsResults implements Serializable {

    private static final long serialVersionUID = 3441744036075343619L;

    protected List<ReSTPredictionContestantResult> results = new ArrayList<ReSTPredictionContestantResult>();

    protected int points = 0;

    public ReSTPredictionOrderedContestantsResults() {
        super();
    }

    public ReSTPredictionOrderedContestantsResults(
            PredictionOrderedContestantsList corePrediction,
            List<SportContestant> actual) {
        this();

        int i = 0;
        for (SportContestant contestant : actual) {
            SportContestant prediction = null;
            if (corePrediction != null) {
                try {
                    prediction = corePrediction.getContestants().get(i);
                } catch (IndexOutOfBoundsException e) {
                    prediction = null;
                }
            }

            int p = 0;
            if (corePrediction != null) {
                PredictionOrderedContestantResult posPoints = ((PredictionOrderedContestantsList) corePrediction).getResultsForPosition(i);
                if (posPoints != null) {
                    p = posPoints.getPoints();
                }
            }

            results.add(new ReSTPredictionContestantResult(prediction,
                    contestant, p, 0.0, 0));
            i++;
        }

        if (corePrediction != null) {
            points = corePrediction.getPoints();
        }
    }

    public List<ReSTPredictionContestantResult> getResults() {
        return results;
    }

    public void setResults(List<ReSTPredictionContestantResult> results) {
        this.results = results;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

}
