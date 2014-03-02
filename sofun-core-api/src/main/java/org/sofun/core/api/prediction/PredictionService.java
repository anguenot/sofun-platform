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

package org.sofun.core.api.prediction;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.prediction.tournament.PredictionGameQuestion;
import org.sofun.core.api.prediction.tournament.PredictionGameScore;
import org.sofun.core.api.prediction.tournament.contestant.PredictionGameOrderedContestantsList;
import org.sofun.core.api.prediction.tournament.contestant.PredictionRoundOrderedContestantsList;
import org.sofun.core.api.prediction.tournament.contestant.PredictionSeasonOrderedContestantsList;
import org.sofun.core.api.prediction.tournament.contestant.PredictionStageOrderedContestantsList;
import org.sofun.core.api.question.Question;
import org.sofun.core.api.question.QuestionKupTiebreaker;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface PredictionService extends Serializable {

    PredictionGameOrderedContestantsList getPredictionFor(Member member,
            TournamentGame game, String type, Kup kup);

    PredictionGameScore getPredictionScoreFor(Member member,
            TournamentGame game, String type, Kup kup);

    PredictionGameQuestion getPredictionQuestionFor(Member member,
            TournamentGame game, String type, Kup kup, Question question);

    PredictionQuestionKupTiebreaker getPredictionQuestionKupTieBreakerFor(
            Member member, Kup kup, QuestionKupTiebreaker question);

    PredictionRoundOrderedContestantsList getPredictionFor(Member member,
            TournamentRound round, String type, Kup kup);

    PredictionStageOrderedContestantsList getPredictionFor(Member member,
            TournamentStage stage, String type, Kup kup);

    PredictionSeasonOrderedContestantsList getPredictionFor(Member member,
            TournamentSeason season, String type, Kup kup);

    PredictionGameOrderedContestantsList createPredictionFor(Member member,
            TournamentGame game, String type,
            List<SportContestant> contestants, Kup kup);

    PredictionGameScore createPredictionScoreFor(Member member,
            TournamentGame game, String type, List<Integer> score, Kup kup);

    PredictionGameQuestion createQuestionPredictionFor(Member member,
            TournamentGame game, String type, Question question, Kup kup,
            String choice);

    PredictionRoundOrderedContestantsList createPredictionFor(Member member,
            TournamentRound round, String type,
            List<SportContestant> contestants, Kup kup);

    PredictionStageOrderedContestantsList createPredictionFor(Member member,
            TournamentStage stage, String type,
            List<SportContestant> contestants, Kup kup);

    PredictionSeasonOrderedContestantsList createPredictionFor(Member member,
            TournamentSeason season, String type,
            List<SportContestant> contestants, Kup kup);

    boolean hasPredictions(Member member, Kup kup);

    List<Prediction> getPredictionsFor(TournamentStage stage, Kup kup);

    List<Prediction> getPredictionsFor(TournamentRound round, Kup kup);

    List<Prediction> getPredictionsFor(TournamentGame game, Kup kup);

    List<Prediction> getPredictionsGameToCompute();

    List<Prediction> getPredictionsRoundToCompute();

    List<Prediction> getPredictionsStageToCompute();

    List<Prediction> getPredictionsSeasonToCompute();

    List<Prediction> getPredictionsFor(TournamentSeason season, Kup kup);

    List<Prediction> getPredictionsFor(Kup kup);

    List<Prediction> getPredictionsFor(Member member, int offset, int batchSize);

    List<Prediction> getPredictionsFor(Kup kup, Member member);

    List<Prediction> getPredictionsFor(Kup kup, Member member, String type);

    boolean isPredictionAllowedOn(TournamentGame game);

    boolean isPredictionAllowedOn(TournamentRound round);

    boolean isPredictionAllowedOn(TournamentStage stage);

    boolean isPredictionAllowedOn(TournamentSeason season);

    boolean isPredictionAllowedOn(Kup kup);

    Question getQuestionById(long questionId);

    Date getPredictionsLastModifiedFor(Member member, Kup kup)
            throws CoreException;

    QuestionKupTiebreaker getQuestionKupTiebreakerById(long questionId);

    PredictionQuestionKupTiebreaker createQuestionTiebreakerPredictionFor(
            Member member, QuestionKupTiebreaker question, Kup kup,
            String choice);

}
