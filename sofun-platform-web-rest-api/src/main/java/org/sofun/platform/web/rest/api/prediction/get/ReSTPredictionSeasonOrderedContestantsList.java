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

import org.sofun.core.api.prediction.tournament.contestant.PredictionSeasonOrderedContestantsList;
import org.sofun.platform.web.rest.api.tournament.ReSTTournamentSeason;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTPredictionSeasonOrderedContestantsList extends
        ReSTPredictionOrderedContestantsList {

    private static final long serialVersionUID = -7402078703265385023L;

    protected ReSTTournamentSeason season;

    public ReSTPredictionSeasonOrderedContestantsList() {
        super();
    }

    public ReSTPredictionSeasonOrderedContestantsList(
            PredictionSeasonOrderedContestantsList corePrediction, long seasonId) {
        super(corePrediction, seasonId);
        if (corePrediction != null) {
            setSeason(new ReSTTournamentSeason(
                    corePrediction.getTournamentSeason()));
        }
    }

    public ReSTTournamentSeason getSeason() {
        return season;
    }

    public void setSeason(ReSTTournamentSeason season) {
        this.season = season;
    }

}
