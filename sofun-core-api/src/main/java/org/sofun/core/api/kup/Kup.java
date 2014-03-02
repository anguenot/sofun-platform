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

import org.sofun.core.api.community.Community;
import org.sofun.core.api.feed.Feed;
import org.sofun.core.api.kup.bet.KupWinningsRepartitionRuleType;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.prediction.Prediction;
import org.sofun.core.api.question.Question;
import org.sofun.core.api.question.QuestionKupTiebreaker;
import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.SportCategory;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.team.Team;

/**
 * Kup interface.
 * 
 * A {@link Kup} takes place inside of a {@link Team}. It allows {@link Member}
 * to bet on one or more sport events part of one or several {@link Tournament}
 * that can be from various {@link Sport}
 * 
 * 
 * @see {@link Community}
 * @see {@link Team}
 * @see {@link Member}
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface Kup extends Serializable {

    /**
     * Returns the {@link Kup} identifier.
     * 
     * @return: a {@link Long}
     */
    public long getId();

    /**
     * Set the {@link Kup} identifier.
     * 
     * @param id: the identifier.
     */
    void setId(long id);

    /**
     * Returns the name of the {@link Kup}
     * 
     * @return a {@link String}
     */
    public String getName();

    /**
     * Sets the name of the {@link Kup}
     * 
     * @param name: {@link String}
     */
    void setName(String name);

    /**
     * Returns the description of the {@link Kup}
     * 
     * @return a {@link String}
     */
    public String getDescription();

    /**
     * Sets the descriptions of the {@link Kup}
     * 
     * @param description: {@link String}
     */
    void setDescription(String description);

    /**
     * Returns the date at which the {@link Kup} will be effective.
     * 
     * @return a {@link Date}
     */
    Date getStartDate();

    /**
     * Returns the effective start date for this Kup.
     * 
     * <p>
     * The effective start date will be the date at which the first bettable
     * sport event will start.
     * </p>
     * 
     * @return a {@link Date} instance in UTC.
     */
    Date getEffectiveStartDate();

    /**
     * Sets the date at which the {@link Kup} will be effective.
     * 
     * @param date: {@link Date}
     */
    void setStartDate(Date date);

    /**
     * Returns the date at which the {@link Kup} will end.
     * 
     * @return a {@link Date}
     */
    Date getEndDate();

    /**
     * Returns the effective end date of this Kup.
     * 
     * <p>
     * The effective end date for a Kup is either an arbitrary end date defined
     * by the administrator creating the Kup (i.e. Sofun) or the start date of
     * the last actual sport event. Implementation should take the earliest
     * date.
     * </p>
     * 
     * @return a {@link Date} instance in UTC.
     */
    Date getEffectiveEndDate();

    /**
     * Returns the date at which the kup must be closed.
     * 
     * @return a {@link Date} instance in UTC.
     */
    Date getCloseDate();

    /**
     * Sets the date at which the {@link Kup} will be end.
     * 
     * @param date: {@link Date}
     */
    void setEndDate(Date date);

    /**
     * Returns the list of {@link Kup} {@link Member}.
     * 
     * @return a {@link List} of {@link Member}
     */
    Set<Member> getMembers();

    /**
     * Sets the list of {@link Kup} {@link Member}.
     * 
     * @param a {@link List} of {@link Member}
     */
    void setMembers(Set<Member> members);

    /**
     * Returns the list of {@link Kup} {@link Member}.
     * 
     * @return a {@link List} of {@link Member}
     */
    Set<Member> getAdmins();

    /**
     * Sets the list of {@link Kup} {@link Member}.
     * 
     * @param a {@link List} of {@link Member}
     */
    void setAdmins(Set<Member> admins);

    /**
     * Returns the corresponding team.
     * 
     * @return a {@link Team} instance.
     */
    Team getTeam();

    /**
     * Sets the corresponding team.
     * 
     * @param team: a {@link Team} instance.
     */
    void setTeam(Team team);

    /**
     * Returns the Kup Type.
     * 
     * @return {@link KupType} {@link String}
     */
    String getType();

    /**
     * Sets the Kup type.
     * 
     * @param type: a {@link KupType} {@link String}
     */
    void setType(String type);

    void setAvatar(String avatar);

    String getAvatar();

    float getJackpot();

    float getEffectiveJackpot();

    void setJackpot(float jackpot);

    float getStake();

    void setStake(float stake);

    String getStakeCurrency();

    void setStakeCurrency(String currency);

    Set<Member> getParticipantsFriendsFor(Member member);

    Sport getSport();

    void setSport(Sport sport);

    SportCategory getSportCategory();

    void setSportCategory(SportCategory sportCategory);

    List<Tournament> getSportTournaments();

    void setSportTournaments(List<Tournament> tournaments);

    void addSportTournament(Tournament tournament);

    KupRankingTable getRankingTable();

    void setRankingTable(KupRankingTable table);

    Member addMember(Member member);

    Member delMember(Member member);

    Member addAdmin(Member member);

    Member delAdmin(Member member);

    boolean isAdmin(Member member);

    boolean isMember(Member member);

    boolean isParticipant(Member member);

    /**
     * Returns the Kup's activity feed.
     * 
     * @return a {@link Feed} instance.
     */
    Feed getActivityFeed();

    /**
     * Returns the community creation date.
     * 
     * @return {@link Date} in UTC.
     */
    Date getCreated();

    /**
     * Sets the community creation date.
     * 
     * @param created: a {@link Date} expected in UTC.
     */
    void setCreated(Date created);

    /**
     * Returns the Kup status.
     * 
     * @return {@link KupStatus} constant as a {@link Byte}.
     */
    byte getStatus();

    /**
     * Sets the Kup status.
     * 
     * @param status: a {@link KupStatus} constant as a {@link Byte}.
     */
    void setStatus(byte status);

    /**
     * Returns the repartition rule type
     * 
     * <p/>
     * A repartition rule includes ranking position and percentage of the
     * jackpot.
     * 
     * @see KupWinningsRepartitionRuleType
     * 
     * @return a {@link KupWinningsRepartitionRuleType} short value.
     */
    byte getRepartitionRuleType();

    /**
     * Sets the repartition rule type
     * 
     * <p/>
     * A repartition rule includes ranking position and percentage of the
     * jackpot.
     * 
     * @see KupWinningsRepartitionRuleType
     * 
     * @param rule: {@link KupWinningsRepartitionRuleType} short value.
     */
    void setRepartitionRuleType(byte rule);

    /**
     * Returns the rake subtracted percentage from the jackpot.
     * 
     * <p/>
     * The rack depends on the actual participants numbers.
     * 
     * @return: a {@link Float} number
     */
    float getRakePercentage();

    /**
     * Returns the rake subtracted amount from the jackpot.
     * 
     * <p/>
     * The rack depends on the actual participants numbers.
     * 
     * @return: a {@link Float} number
     */
    float getRakeAmount();

    /**
     * Returns the actual validated participants. (i.e.: member w/ predictions
     * AND a bet.
     * 
     * @return a {@link Set} of {@link Member} instances.
     */
    Set<Member> getParticipants();

    /**
     * Adds an actual participant. (i.e.: member w/ predictions AND a bet.
     * 
     * @param member: a {@link Member} instances.
     */
    void addParticipant(Member member);

    /**
     * Returns the list of {@link TournamentSeason} this {@link Kup} can take
     * its events from.
     * 
     * @return a list of {@link TournamentSeason} instances.
     */
    List<TournamentSeason> getBettableTournaments();

    /**
     * Sets the list of {@link TournamentSeason} this {@link Kup} can take its
     * events from.
     * 
     * @param tournaments: a list of {@link TournamentSeason} instances.
     */
    void setBettableTournaments(List<TournamentSeason> tournaments);

    /**
     * Adds a new season to the list of bettable tournaments.
     * 
     * @param season: a {@link TournamentSeason} instance
     */
    void addBettableTournament(TournamentSeason season);

    /**
     * Returns the bettable {@link TournamentGame} within this {@link Kup}
     * 
     * @return a {@link List} of {@link TournamentGame}
     */
    List<TournamentGame> getBettableGames();

    /**
     * Sets the bettable {@link TournamentGame} within this {@link Kup}
     * 
     * @param games: a {@link List} of {@link TournamentGame}
     */
    void setBettableGames(List<TournamentGame> games);

    /**
     * Adds a new game to the list of bettable games
     * 
     * @param game: a {@link TournamentGame} instance
     */
    void addBettableGame(TournamentGame game);

    /**
     * Returns the bettable {@link TournamentRounds} within this {@link Kup}
     * 
     * @return a {@link List} of {@link TournamentRound}
     */
    List<TournamentRound> getBettableRounds();

    /**
     * Sets the bettable {@link TournamentRound} within this {@link Kup}
     * 
     * @param rounds: a {@link List} of {@link TournamentRound}
     */
    void setBettableRounds(List<TournamentRound> rounds);

    /**
     * Adds a new round to the list of bettable rounds
     * 
     * @param round: a {@link TournamentRound} instance
     */
    void addBettableRound(TournamentRound round);

    /**
     * Returns the bettable {@link TournamentStage} within this {@link Kup}
     * 
     * @return a {@link List} of {@link TournamentStage}
     */
    List<TournamentStage> getBettableStages();

    /**
     * Sets the bettable {@link TournamentStage} within this {@link Kup}
     * 
     * @param stages: a {@link List} of {@link TournamentStage}
     */
    void setBettableStages(List<TournamentStage> stages);

    /**
     * Adds a new stage to the list of bettable stages
     * 
     * @param stage: a {@link TournamentStage} instance
     */
    void addBettableStage(TournamentStage stage);

    /**
     * Returns the {@link Question} within this {@link KupType}
     * 
     * @return a {@link List} of {@link Question} instance
     */
    List<Question> getBettableQuestion();

    /**
     * Sets the bettable {@link Question} within this {@link KupType}
     * 
     * @param questions: a {@link List} of {@link Question} instance
     */
    void setBettableQuestion(List<Question> questions);

    /**
     * Adds a bettable {@link Question} within this {@link KupType}
     * 
     * @param question: a{@link Question} instance
     */
    void addBettableQuestion(Question question);

    /**
     * Returns the points distribution rules for this Kup.
     * 
     * <p/>
     * 
     * This is configurable by the Kup admin.
     * 
     * @see {@link Prediction} for the actual prediction type.
     * 
     * @return: Map from prediction type to number of points.
     */
    Map<String, Integer> getPredictionPointsDistributionRule();

    /**
     * Sets the points distribution rules for this Kup.
     * 
     * <p/>
     * 
     * This is configurable by the Kup admin.
     * 
     * @see {@link Prediction} for the actual prediction type.
     * 
     * @param rule: Map from prediction type to number of points.
     * 
     */
    void setPredictionPointsDistributionRule(Map<String, Integer> rules);

    /**
     * Returns the Kup meta type.
     * 
     * <p/>
     * 
     * A kup meta type is mostly used to determine the points computation rules.
     * 
     * @return a {@link String}
     */
    String getMetaType();

    /**
     * Sets the Kup meta type.
     * 
     * @param metaType: a {@link String}
     */
    void setMetaType(String metaType);

    /**
     * Whether or not the Kup is a template.
     * 
     * @return true or false
     */
    boolean isTemplate();

    /**
     * Sets if whether or not the Kup is a template.
     * 
     * @param template: true or false.
     */
    void setTemplate(boolean isTemplate);

    /**
     * Whether or not the Kup is a partner's Kup.
     * 
     * @return true or false
     */
    boolean isPartner();

    /**
     * Sets if wether or not the Kup os a template.
     * 
     * @param isPartner: true or false
     */
    void setPartner(boolean isPartner);

    /**
     * Returns the minimum guaranteed price for this Kup.
     * 
     * <p>
     * 
     * The minimum guaranteed price is the value of the Jackpot at the very
     * least even if the sum of all player's bets does not cover this amount.
     * 
     * @return an {@link Integer} value
     */
    int getGuaranteedPrice();

    /**
     * Sets the minimum guaranteed price for this Kup.
     * 
     * <p>
     * 
     * The minimum guaranteed price is the value of the Jackpot at the very
     * least even if the sum of all player's bets does not cover this amount.
     * 
     * @param guaranteedPrice: an {@link Integer} value
     */
    void setGuaranteedPrice(int guaranteedPrice);

    /**
     * Is the Kup final?
     * 
     * <p>
     * 
     * Final here means that there will be no new event added to this kup.
     * 
     * @return true / false
     */
    boolean isFinal();

    /**
     * Sets the final status of the kup.
     * 
     * @param isFinal: true / false
     */
    void setFinal(boolean isFinal);

    /**
     * Returns the number of participants.
     * 
     * <p>
     * 
     * Used as a search sorting index.
     * 
     */
    int getNbParticipants();

    /**
     * Sets the number of participants.
     * 
     * <p>
     * 
     * Used as a search sorting index.
     * 
     * @param nbParticipants: positive integer
     */
    void setNbParticipants(int nbParticipants);

    /**
     * Returns the Kup duration in minutes.
     * 
     * @return the kup
     */
    int getDuration();

    /**
     * Sets the Kup duration.
     * 
     * @param duration: the Kup's duration in minutes.
     */
    void setDuration(int duration);

    /**
     * Compute de duration of the Kup. Difference in between the first event
     * start date and the last event start date.
     * 
     * @return an integer in minutes
     */
    int computeDuration();

    /**
     * Returns the list of questions used to part winners in case of Ex aequo.
     * 
     * <p>
     * 
     * These questions are not included in ranking and do not add points.
     * 
     * @return a {@link List} of {@link QuestionKupTiebreaker}. Can be empty.
     */
    List<QuestionKupTiebreaker> getQuestionsTiebreaker();

    /**
     * Sets the list of questions used to part winners in case of Ex aequo.
     * 
     * <p>
     * 
     * These questions are not included in ranking and do not add points.
     * 
     * @param questions
     */
    void setQuestionsTiebreaker(List<QuestionKupTiebreaker> questions);

    /**
     * Adds a {@link QuestionKupTiebreaker}
     * 
     * @param question: a {@link QuestionKupTiebreaker} instance
     */
    void addQuestionTiebreaker(QuestionKupTiebreaker question);

    /**
     * Is this Kup "Moderated"?
     * 
     * <p>
     * 
     * Moderated here means that before gains will be distributed a human
     * verification must happen.
     * 
     * <p>
     * 
     * true is the default.
     * 
     * @return true or false.
     */
    boolean isModerated();

    /**
     * 
     * Set Kup's moderation.
     * 
     * <p>
     * 
     * Moderated here means that before gains will be distributed a human
     * verification must happen.
     * 
     * <p>
     * 
     * true is the default.
     * 
     * @param moderated: true or false
     */
    void setModerated(boolean moderated);

}
