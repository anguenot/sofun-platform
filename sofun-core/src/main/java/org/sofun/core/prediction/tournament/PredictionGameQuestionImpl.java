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

package org.sofun.core.prediction.tournament;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sofun.core.api.prediction.tournament.PredictionGameQuestion;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.prediction.PredictionQuestionImpl;
import org.sofun.core.sport.tournament.TournamentGameImpl;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "predictions_questions_games")
@Inheritance(strategy = InheritanceType.JOINED)
public class PredictionGameQuestionImpl extends PredictionQuestionImpl implements PredictionGameQuestion {

    private static final long serialVersionUID = 7123399867815411504L;

    @ManyToOne(targetEntity = TournamentGameImpl.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    protected TournamentGame game;

    @Override
    public TournamentGame getTournamentGame() {
        return game;
    }

    @Override
    public void setTournamentGame(TournamentGame game) {
        this.game = game;
    }

}
