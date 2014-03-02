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

import org.sofun.core.api.prediction.tournament.contestant.PredictionGameOrderedContestantsList;
import org.sofun.platform.web.rest.api.tournament.ReSTTournamentGame;
import org.sofun.platform.web.rest.api.tournament.ReSTTournamentGameImpl;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTPredictionGameOrderedContestantsList extends
        ReSTPredictionOrderedContestantsList {

    private static final long serialVersionUID = -7402078703265385023L;

    protected ReSTTournamentGame game;

    public ReSTPredictionGameOrderedContestantsList() {
        super();
    }

    public ReSTPredictionGameOrderedContestantsList(
            PredictionGameOrderedContestantsList corePrediction, long seasonId) {
        super(corePrediction, seasonId);
        if (corePrediction != null) {
            setGame(new ReSTTournamentGameImpl(
                    corePrediction.getTournamentGame()));
        }
    }

    public ReSTTournamentGame getGame() {
        return game;
    }

    public void setGame(ReSTTournamentGame game) {
        this.game = game;
    }

}
