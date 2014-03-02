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

package org.sofun.core.api.kup;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.bet.KupWinningsRepartitionRuleType;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.prediction.Prediction;
import org.sofun.core.api.question.Question;
import org.sofun.core.api.question.QuestionKupTiebreaker;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.team.Team;

/**
 * Kup service.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface KupService extends Serializable {

    /**
     * Returns a {@link Kup} given its identifier.
     * 
     * @param kupId: the kup identifier.
     * @return a {@link Kup} instance.
     */
    Kup getKupById(long kupId);

    /**
     * Returns {@link Map} from {@link Member} ranking position to percentage of
     * the jackpot winnings.
     * 
     * <p/>
     * 
     * The {@link Kup} instance defines its repartition type.
     * 
     * @see {@link KupWinningsRepartitionRuleType}
     * 
     * @param kup: a {@link Kup} instance.
     * @return a {@link Map} from position to percentage.
     */
    Map<Integer, Float> getWinningsRepartitionRulesFor(Kup kup);

    /**
     * Search for Kups.
     * 
     * @param params: a {@link Map} from param key to param value.
     * @return a {@link KupSearchResults} instance
     * @throws CoreException
     */
    KupSearchResults search(Map<String, String> params) throws CoreException;

    /**
     * Member places a bet in a Kup.
     * 
     * @param member: a {@link Member} instance.
     * @param kup: a {@link Kup} instance.
     * @throws CoreException
     */
    void placeKupBet(Member member, Kup kup) throws CoreException;

    /**
     * Has a given member already placed bet on given kup?
     * 
     * @param member: a {@link Member} instance.
     * @param kup: a {@link Kup} instance.
     * @throws CoreException
     */
    boolean hasBet(Member member, Kup kup) throws CoreException;

    /**
     * Records a prediction.
     * 
     * @param type: the prediction type. Can be whatever the application decides
     *        it to be. It is useful to allow several predictions on the same
     *        sports events.
     * @param member: a {@link Member} instance
     * @param kup: a {@link Kup} instance
     * @param game: a {@link TournamentGame} instance
     * @param contestants: list of contestants. Empty in case of no winner
     *        prediction (odd game)
     * @throws CoreException
     */
    void addPrediction(String type, Member member, Kup kup,
            TournamentGame game, List<SportContestant> contestants)
            throws CoreException;

    /**
     * Records a prediction.
     * 
     * @param type: the prediction type. Can be whatever the application decides
     *        it to be. It is useful to allow several predictions on the same
     *        sports events.
     * @param member: a {@link Member} instance
     * @param kup: a {@link Kup} instance
     * @param game: a {@link TournamentGame} instance
     * @param score: list of integers. (team1, team2) order matters.
     * @throws CoreException
     */
    void addPredictionScore(String type, Member member, Kup kup,
            TournamentGame game, List<Integer> score) throws CoreException;

    /**
     * Records a prediction.
     * 
     * @param type: the prediction type. Can be whatever the application decides
     *        it to be. It is useful to allow several predictions on the same
     *        sports events.
     * @param member: a {@link Member} instance
     * @param kup: a {@link Kup} instance
     * @param game: a {@link TournamentGame} instance
     * @param question: a {@link Question} instance
     * @param choice: the actual prediction's choice
     * @throws CoreException
     */
    void addGameQuestionPrediction(String type, Member member, Kup kup,
            TournamentGame game, Question question, String choice)
            throws CoreException;

    /**
     * Records a Tiebreaker question.
     * 
     * @param member: a {@link Member} instance
     * @param kup: a {@link Kup} instance
     * @param question: a {@link QuestionKupTiebreaker} instance
     * @param choice: the actual prediction's choice
     * @throws CoreException
     */
    void addQuestionKupTiebreaker(Member member, Kup kup,
            QuestionKupTiebreaker question, String choice) throws CoreException;

    /**
     * Records a prediction.
     * 
     * @param type: the prediction type. Can be whatever the application decides
     *        it to be. It is useful to allow several predictions on the same
     *        sports events.
     * @param member: a {@link Member} instance
     * @param kup: a {@link Kup} instance
     * @param round: a {@link TournamentRound} instance
     * @param contestants: list of contestants. Empty in case of no winner
     *        prediction (odd game)
     * @throws CoreException
     */
    void addPrediction(String type, Member member, Kup kup,
            TournamentRound round, List<SportContestant> constestants)
            throws CoreException;

    /**
     * Records a prediction.
     * 
     * @param type: the prediction type. Can be whatever the application decides
     *        it to be. It is useful to allow several predictions on the same
     *        sports events.
     * @param member: a {@link Member} instance
     * @param kup: a {@link Kup} instance
     * @param stage: a {@link TournamentStage} instance
     * @param contestants: list of contestants. Empty in case of no winner
     *        prediction (odd game)
     * @throws CoreException
     */
    void addPrediction(String type, Member member, Kup kup,
            TournamentStage stage, List<SportContestant> constestants)
            throws CoreException;

    /**
     * Records a prediction.
     * 
     * @param type: the prediction type. Can be whatever the application decides
     *        it to be. It is useful to allow several predictions on the same
     *        sports events.
     * @param member: a {@link Member} instance
     * @param kup: a {@link Kup} instance
     * @param season: a {@link TournamentSeason} instance
     * @param contestants: list of contestants. Empty in case of no winner
     *        prediction (odd game)
     * @throws CoreException
     */
    void addPrediction(String type, Member member, Kup kup,
            TournamentSeason season, List<SportContestant> constestants)
            throws CoreException;

    /**
     * Does {@link Member} have at least one prediction in the {@link Kup}
     * 
     * @param member: a {@link Member} instance.
     * @param kup: a {@link Kup} instance.
     * @return true or false.
     */
    boolean hasPrediction(Member member, Kup kup);

    /**
     * Returns all {@link Kup}s given a list of types.
     * 
     * 
     * @see {@link KupType}
     * 
     * @param types: Kup's type
     * @param status: Kup's status
     * 
     * @return: a {@link List} of {@link Kup} instance.
     * @throws CoreException
     */
    List<Kup> getKupsByStatus(byte[] status, String[] types)
            throws CoreException;

    /**
     * Returns all {@link Kup}s that are active.
     * 
     * @see {@link KupType}
     * 
     * @return: a {@link List} of {@link Kup} instance.
     * @throws CoreException
     */
    List<Kup> getActiveKups() throws CoreException;

    /**
     * Computes and returns the points corresponding to a prediction.
     * 
     * <p/>
     * 
     * The service will check for a registered plugin for this prediction type
     * in the Kup's context.
     * 
     * @param kup: a {@link Kup} instance.
     * @param prediction: a {@link Prediction} instance.
     * @return a number of points as an integer.
     * @throws CoreException if missing rules
     */
    int getPointsPredictionFor(Kup kup, Prediction prediction)
            throws CoreException;

    /**
     * Kup administrator can request to cancel a Kup for various reasons.
     * 
     * 
     * 
     * @param kup: a {@link Kup} instance.
     */
    void cancelKup(Kup kup);

    /**
     * Returns the credentials for a Member in a given Kup.
     * 
     * 
     * @param kup: a {@link Kup} instance
     * @param member: a {@link Member} instance
     * @return a set of unique string
     */
    Set<String> getCredentials(Kup kup, Member member);

    /**
     * Create a new Kup from a `meta` Kup.
     * 
     * <p/>
     * 
     * Typically, a team admin creates a Kup for its own team.
     * 
     * @see KupWinningsRepartitionRuleType
     * 
     * @param kup: a {@link Kup} instance
     * @param team: a {@link Team} instance
     * @param stake: stake value or 0 if free Kup
     * @param repartitionType: predefined repartition type.
     * @return a new {@link Kup} instance
     */
    Kup createKupFromMeta(Kup kup, Team team, float stake, byte repartitionType)
            throws CoreException;

    /**
     * Returns the predictions (all of them) of a given member in a given kup.
     * 
     * @param member: a {@link Member} instance
     * @param kup: a {@link Kup} instance
     * @return list of {@link Prediction} instance.
     * @throws CoreException
     */
    List<Prediction> getPredictionsFor(Member member, Kup kup)
            throws CoreException;

    /**
     * Returns all Kups given a Kup's name.
     * 
     * @param name: name of the Kup.
     * @return a {@link List} if Kup.
     */
    List<Kup> getKupsByName(String name);

    /**
     * Returns the Kup's template for a given Kup.
     * 
     * @param kup: a {@link Kup} instance
     * @return a {@link Kup} instance or null if the Kup given as a parameter is
     *         a template
     */
    Kup getTemplateFor(Kup kup);

    /**
     * Sync up Kups with their respective templates.
     * 
     * @throws CoreException
     */
    void syncKupsWithTemplate() throws CoreException;

    /**
     * Sync up Kup's templates content.
     * 
     * @throws CoreException
     */
    void syncTemplates() throws CoreException;

    /**
     * Returns when was the last time a given member in a given kup updated its
     * predictions.
     * 
     * @param member: a {@link Member} instance
     * @param kup: a {@link Kup} instance
     * @return a {@link Date} instance or null if no predictions
     * @throws CoreException
     */
    Date getPredictionsLastModifiedFor(Member member, Kup kup)
            throws CoreException;

    /**
     * Member sends invitation.
     * 
     * @param kup: a {@link Kup} instance.
     * @param inviter: a {@link Member} instance. Member sending invites.
     * @param params: parameters including email recipients, etc.
     */
    void invite(Kup kup, Member inviter, Map<String, String> params)
            throws CoreException;

    /**
     * Returns the number of correct predictions for a given member.
     * 
     * @param kup: a {@link Kup} instance
     * @param member: a {@link Member} instance.
     * @return an integer >= 0
     * @throws CoreException
     */
    int countCorrectPredictionsFor(Kup kup, Member member) throws CoreException;

    /**
     * Returns the earliest {@link Prediction} recorded for a given
     * {@link Member} within a {@link Kup}.
     * 
     * @param member: a {@link Member} instance.
     * @param kup: a {@link Kup} instance.
     * @return a {@link Date} or null if no predictions found (which is not
     *         possible)
     */
    Date getFirstPredictionDateFor(Member member, Kup kup) throws CoreException;

    /**
     * Returns the ranking table with statistics for a given Kup.
     * 
     * @param kup: a {@link Kup} instance
     * @param offset: offset
     * @param batchSize: batch size
     * @return {@link KupRankingTable} instance
     * @throws CoreException
     */
    KupRankingTable getKupRanking(Kup kup, int offset, int batchSize)
            throws CoreException;

    /**
     * Update statistics for a given Kup.
     * 
     * @param kup: a {@link Kup} instance
     * @throws CoreException
     */
    void updateKupStats(Kup kup) throws CoreException;

    /**
     * Update statistics for all Kups.
     * 
     * @throws CoreException
     */
    void updateKupsStats() throws CoreException;

    /**
     * Returns a Kup given a transaction identifier.
     * 
     * @param txnId: a transaction id
     * @return a {@link Kup} instance or null if none bound.
     */
    Kup getKupByTransactionId(long txnId);

}
