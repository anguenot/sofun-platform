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

import java.util.List;

import org.sofun.core.api.prediction.tournament.contestant.PredictionSeasonOrderedContestantsList;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.platform.web.rest.api.tournament.ReSTTournamentSeason;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTPredictionSeasonOrderedContestantsResults extends
        ReSTPredictionOrderedContestantsResults {

    private static final long serialVersionUID = 8659049668119802141L;

    protected ReSTTournamentSeason season;

    public ReSTPredictionSeasonOrderedContestantsResults() {
        super();
    }

    public ReSTPredictionSeasonOrderedContestantsResults(
            PredictionSeasonOrderedContestantsList corePrediction,
            List<SportContestant> actual) {
        super(corePrediction, actual);
        if (corePrediction != null) {
            this.season = new ReSTTournamentSeason(
                    corePrediction.getTournamentSeason());
        }
    }

    public ReSTTournamentSeason getSeason() {
        return season;
    }

    public void setSeason(ReSTTournamentSeason season) {
        this.season = season;
    }

}
