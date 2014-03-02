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

package org.sofun.core.kup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.banking.CurrencyType;
import org.sofun.core.api.feed.Feed;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupRankingTable;
import org.sofun.core.api.kup.KupStatus;
import org.sofun.core.api.kup.KupType;
import org.sofun.core.api.kup.bet.KupWinningsRepartitionRuleType;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.question.Question;
import org.sofun.core.api.question.QuestionKupTiebreaker;
import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.SportCategory;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentRoundStatus;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentSeasonStatus;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.TournamentStageStatus;
import org.sofun.core.api.team.Team;
import org.sofun.core.feed.FeedImpl;
import org.sofun.core.kup.table.KupRankingTableImpl;
import org.sofun.core.member.MemberImpl;
import org.sofun.core.question.QuestionImpl;
import org.sofun.core.question.QuestionKupTiebreakerImpl;
import org.sofun.core.sport.SportCategoryImpl;
import org.sofun.core.sport.SportImpl;
import org.sofun.core.sport.tournament.TournamentGameImpl;
import org.sofun.core.sport.tournament.TournamentImpl;
import org.sofun.core.sport.tournament.TournamentRoundImpl;
import org.sofun.core.sport.tournament.TournamentSeasonImpl;
import org.sofun.core.sport.tournament.TournamentStageImpl;
import org.sofun.core.team.TeamImpl;

/**
 * Kup Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "kups")
public class KupImpl implements Kup {

    private static final long serialVersionUID = -1502507059363020226L;

    private static final Log log = LogFactory.getLog(KupImpl.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "integer")
    protected long id;

    @Column(name = "name", nullable = false)
    protected String name;

    @Column(name = "description")
    protected String description;

    @Column(name = "created", nullable = false)
    protected Date created;

    @Column(name = "type", nullable = false)
    protected String type;

    @Column(name = "meta_type", nullable = true)
    protected String metaType = "";

    @Column(name = "status", nullable = false)
    protected byte status = KupStatus.CREATED;

    @Column(
            name = "is_template",
            nullable = false,
            columnDefinition = "boolean default false")
    protected boolean isTemplate = false;

    @Column(
            name = "is_partner",
            nullable = false,
            columnDefinition = "boolean default false")
    protected boolean isPartner = false;

    @Column(name = "avatar_url")
    protected String avatarUrl;

    @Column(name = "stake", columnDefinition = "float default 0")
    protected float stake = 0;

    @Column(name = "guaranteed_price", columnDefinition = "int default 0")
    protected int guaranteedPrice = 0;

    @Column(name = "stake_currency")
    protected String stakeCurrency = CurrencyType.EURO;

    @Column(name = "jackpot", columnDefinition = "float default 0")
    protected float jackpot = 0;

    @Column(name = "winnings_repartition_type", nullable = false)
    protected byte repartitionType = KupWinningsRepartitionRuleType.TYPE_3;

    @ManyToOne(targetEntity = TeamImpl.class)
    @JoinColumn(name = "team_id", nullable = false)
    protected Team team;

    @ManyToOne(targetEntity = SportImpl.class)
    @JoinColumn(name = "sport_id")
    protected Sport sport;

    @ManyToOne(targetEntity = SportCategoryImpl.class)
    @JoinColumn(name = "sport_category_id")
    protected SportCategory sportCategory;

    @Column(
            name = "is_final",
            nullable = false,
            columnDefinition = "boolean default false")
    protected boolean isFinal = false;

    @Column(
            name = "start_date",
            nullable = false,
            columnDefinition = "DATE DEFAULT CURRENT_DATE")
    protected Date startDate;

    @Column(
            name = "end_date",
            nullable = false,
            columnDefinition = "DATE DEFAULT CURRENT_DATE")
    protected Date endDate;

    @Column(name = "nb_participants", columnDefinition = "int default 0")
    protected int nbParticipants = 0;

    @Column(name = "duration", columnDefinition = "int default 0")
    protected int duration = 0;

    @ManyToMany(targetEntity = MemberImpl.class, fetch = FetchType.LAZY)
    @JoinTable(name = "kups_members", joinColumns = { @JoinColumn(
            name = "kup_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "member_id",
            referencedColumnName = "id") })
    protected Set<Member> members;

    @ManyToMany(targetEntity = MemberImpl.class, fetch = FetchType.LAZY)
    @JoinTable(name = "kups_participants", joinColumns = { @JoinColumn(
            name = "kup_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "member_id",
            referencedColumnName = "id") })
    protected Set<Member> participants;

    @ManyToMany(targetEntity = MemberImpl.class, fetch = FetchType.LAZY)
    @JoinTable(name = "kups_admins", joinColumns = { @JoinColumn(
            name = "kup_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "member_id",
            referencedColumnName = "id") })
    protected Set<Member> admins;

    @OneToOne(
            targetEntity = KupRankingTableImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "table_id")
    protected KupRankingTable rankingTable;

    @OneToOne(
            targetEntity = FeedImpl.class,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    protected Feed feed;

    @ManyToMany(
            targetEntity = TournamentSeasonImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "kups_tournaments_seasons", joinColumns = { @JoinColumn(
            name = "kup_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "tournament_season_id",
            referencedColumnName = "id") })
    protected List<TournamentSeason> tournamentSeasons;

    @ManyToMany(
            targetEntity = TournamentStageImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "kups_tournaments_stages", joinColumns = { @JoinColumn(
            name = "kup_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "tournament_stage_id",
            referencedColumnName = "id") })
    protected List<TournamentStage> stages;

    @ManyToMany(
            targetEntity = TournamentRoundImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "kups_tournaments_rounds", joinColumns = { @JoinColumn(
            name = "kup_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "tournament_round_id",
            referencedColumnName = "id") })
    protected List<TournamentRound> rounds;

    @ManyToMany(
            targetEntity = TournamentGameImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "kups_tournaments_games", joinColumns = { @JoinColumn(
            name = "kup_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "tournament_game_id",
            referencedColumnName = "id") })
    protected List<TournamentGame> games;

    @ManyToMany(
            targetEntity = QuestionImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "kups_questions", joinColumns = { @JoinColumn(
            name = "kup_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "question_id",
            referencedColumnName = "id") })
    protected List<Question> questions;

    @ManyToMany(
            targetEntity = QuestionKupTiebreakerImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "kups_questions_tiebreaker", joinColumns = { @JoinColumn(
            name = "kup_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "question_id",
            referencedColumnName = "id") })
    protected List<QuestionKupTiebreaker> questionsTiebreaker;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(
            name = "kups_predictions_points_rules",
            joinColumns = @JoinColumn(name = "id"))
    protected Map<String, Integer> pointsDistributionRules = new HashMap<String, Integer>();

    @ManyToMany(
            targetEntity = TournamentImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "kups_tournaments", joinColumns = { @JoinColumn(
            name = "kup_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "tournament_id",
            referencedColumnName = "id") })
    protected List<Tournament> tournaments;

    @Column(
            name = "moderated",
            nullable = false,
            columnDefinition = "boolean default true")
    protected boolean moderated = true;

    public KupImpl() {
        super();
        setType(KupType.FREE);
        setStatus(KupStatus.CREATED);
        setRepartitionRuleType(KupWinningsRepartitionRuleType.TYPE_3);
    }

    public KupImpl(String name) {
        this();
        this.name = name;
    }

    public KupImpl(String name, String description) {
        this(name);
        this.description = description;
    }

    public KupImpl(String name, String description, Date startDate, Date endDate) {
        this(name, description);
        setStartDate(startDate);
        setEndDate(endDate);
    }

    public KupImpl(Kup kup, Team team, float stake, byte repartitionType) {

        this(kup.getName(), kup.getDescription(), kup.getStartDate(), kup
                .getEndDate());

        setMetaType(kup.getMetaType());
        setStatus(KupStatus.OPENED);
        setModerated(kup.isModerated());

        this.setTeam(team);
        for (Member admin : team.getAdmins()) {
            addAdmin(admin);
        }

        // Admin allowed to customize the following
        setRepartitionRuleType(repartitionType);
        setStake(stake);
        setSportCategory(kup.getSportCategory());
        setAvatar(kup.getAvatar());
        setTemplate(false);

        if (stake == 0) {
            // We do not let room member's bootsrap freerolls with MG.
            setType(KupType.FREE);
        } else {
            // Only French gambling kup's type in Euro.
            setType(KupType.GAMBLING_FR);
            setStakeCurrency(CurrencyType.EURO);
        }

        for (TournamentGame game : kup.getBettableGames()) {
            if (TournamentGameStatus.SCHEDULED.equals(game.getGameStatus())) {
                addBettableGame(game);
            }
        }
        for (TournamentRound round : kup.getBettableRounds()) {
            if (TournamentRoundStatus.SCHEDULED.equals(round.getStatus())) {
                addBettableRound(round);
            }
        }
        for (TournamentStage stage : kup.getBettableStages()) {
            if (TournamentStageStatus.SCHEDULED.equals(stage.getStatus())) {
                addBettableStage(stage);
            }
        }
        for (TournamentSeason season : kup.getBettableTournaments()) {
            if (TournamentSeasonStatus.SCHEDULED.equals(season.getStatus())) {
                addBettableTournament(season);
            }
        }
        for (Question question : kup.getBettableQuestion()) {
            addBettableQuestion(question);
        }
        for (QuestionKupTiebreaker qTb : kup.getQuestionsTiebreaker()) {
            addQuestionTiebreaker(qTb);
        }

        // Mark it as final as the actual parent.
        if (kup.isFinal()) {
            setFinal(true);
        }

    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Date getStartDate() {
        if (startDate != null) {
            return (Date) startDate.clone();
        }
        return null;
    }

    @Override
    public void setStartDate(Date date) {
        if (date != null) {
            this.startDate = (Date) date.clone();
        } else {
            this.startDate = null;
        }
    }

    @Override
    public Date getEndDate() {
        if (endDate != null) {
            return (Date) endDate.clone();
        }
        return null;
    }

    @Override
    public void setEndDate(Date date) {
        if (date != null) {
            this.endDate = (Date) date.clone();
        } else {
            this.endDate = null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Kup) {
            Kup k = (Kup) obj;
            return k.getId() == getId() ? true : false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getId()).hashCode();
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Set<Member> getMembers() {
        if (members == null) {
            members = new HashSet<Member>();
        }
        return members;
    }

    @Override
    public Set<Member> getAdmins() {
        if (admins == null) {
            admins = new HashSet<Member>();
        }
        return admins;
    }

    @Override
    public void setAdmins(Set<Member> admins) {
        this.admins = admins;
    }

    @Override
    public void setAvatar(String avatar) {
        if (avatar != null) {
            this.avatarUrl = avatar;
        }
    }

    @Override
    public String getAvatar() {
        return avatarUrl;
    }

    @Override
    public float getJackpot() {
        return jackpot;
    }

    @Override
    public float getEffectiveJackpot() {
        float effective = getJackpot();
        if (effective <= getGuaranteedPrice()) {
            effective = getGuaranteedPrice();
        }
        if (effective == 0) {
            return 0;
        } else {
            final float rake = getRakePercentage();
            if (rake == 0) {
                return effective;
            } else {
                float theoritical = effective - (effective * rake);
                // Ensure we never distribute less than guaranteed price.
                if (theoritical < getGuaranteedPrice()) {
                    return getGuaranteedPrice();
                } else {
                    return theoritical;
                }
            }
        }
    }

    @Override
    public void setJackpot(float jackpot) {
        this.jackpot = jackpot;
    }

    @Override
    public float getStake() {
        return stake;
    }

    @Override
    public void setStake(float stake) {
        this.stake = stake;
    }

    @Override
    public Set<Member> getParticipantsFriendsFor(Member member) {
        Set<Member> friends = new HashSet<Member>();
        if (member != null && getParticipants().contains(member)) {
            for (Member friend : member.getFriends()) {
                if (getParticipants().contains(friend)) {
                    friends.add(friend);
                }
            }
        }
        return friends;
    }

    @Override
    public Sport getSport() {
        if (sport == null || getSportTournaments().isEmpty()) {
            try {
                for (TournamentSeason season : getBettableTournaments()) {
                    sport = season.getTournament().getSports().get(0);
                    addSportTournament(season.getTournament());
                }
                for (TournamentStage stage : getBettableStages()) {
                    sport = stage.getSeason().getTournament().getSports()
                            .get(0);
                    addSportTournament(stage.getSeason().getTournament());
                }
                for (TournamentRound round : getBettableRounds()) {
                    sport = round.getStage().getSeason().getTournament()
                            .getSports().get(0);
                    addSportTournament(round.getStage().getSeason()
                            .getTournament());
                }
                for (TournamentGame game : getBettableGames()) {
                    sport = game.getRound().getStage().getSeason()
                            .getTournament().getSports().get(0);
                    addSportTournament(game.getRound().getStage().getSeason()
                            .getTournament());
                }
            } catch (NullPointerException npe) {
                log.warn("Cannot find Sport for kup w/ uuid=" + getId());
            }
        }
        return sport;
    }

    @Override
    public void setSport(Sport sport) {
        this.sport = sport;
    }

    @Override
    public SportCategory getSportCategory() {
        return sportCategory;
    }

    @Override
    public void setSportCategory(SportCategory sportCategory) {
        this.sportCategory = sportCategory;
    }

    @Override
    public KupRankingTable getRankingTable() {
        if (rankingTable == null) {
            rankingTable = new KupRankingTableImpl(this);
        }
        return rankingTable;
    }

    @Override
    public void setRankingTable(KupRankingTable table) {
        this.rankingTable = table;
    }

    @Override
    public Member addMember(Member member) {
        if (!isMember(member)) {
            members.add(member);
        }
        return member;
    }

    @Override
    public Member delMember(Member member) {
        if (isMember(member)) {
            members.remove(member);
            return member;
        }
        return null;
    }

    @Override
    public Member addAdmin(Member member) {
        if (!isAdmin(member)) {
            admins.add(member);
        }
        return member;
    }

    @Override
    public Member delAdmin(Member member) {
        if (isAdmin(member)) {
            admins.remove(member);
            return member;
        }
        return null;
    }

    @Override
    public boolean isAdmin(Member member) {
        return getAdmins().contains(member);
    }

    @Override
    public boolean isMember(Member member) {
        return getMembers().contains(member);
    }

    @Override
    public boolean isParticipant(Member member) {
        return getParticipants().contains(member);
    }

    @Override
    public Feed getActivityFeed() {
        if (feed == null) {
            feed = new FeedImpl();
        }
        return feed;
    }

    @Override
    public Date getCreated() {
        if (created == null) {
            return null;
        }
        return (Date) created.clone();
    }

    @Override
    public void setCreated(Date created) {
        if (created != null) {
            this.created = (Date) created.clone();
        }
    }

    @PrePersist
    protected void onCreate() {
        Date now = Calendar.getInstance().getTime();
        setCreated(now);
    }

    @Override
    public byte getStatus() {
        return status;
    }

    @Override
    public void setStatus(byte status) {
        this.status = status;
    }

    @Override
    public float getRakePercentage() {
        float rake = 0;
        if (getJackpot() <= getGuaranteedPrice()) {
            return rake;
        }
        final int p = getParticipants().size();
        // Below are Betkup.fr rules as visible by the players.
        if (p > 2 && p <= 5) {
            rake = 13;
        } else if (p > 5 && p <= 50) {
            rake = 15;
        } else if (p > 50 && p <= 500) {
            rake = 20;
        } else if (p > 500 && p <= 1000) {
            rake = 25;
        } else if (p > 1000) {
            rake = 30;
        } else {
            // Only 1 participant. This case will cancel the Kup.
            return 0;
        }
        return rake / 100;
    }

    @Override
    public Set<Member> getParticipants() {
        if (participants == null) {
            participants = new HashSet<Member>();
        }
        return participants;
    }

    @Override
    public void addParticipant(Member member) {
        if (!getParticipants().contains(member)) {
            participants.add(member);
            getRankingTable().addEntryForMember(member);
        }
    }

    @Override
    public byte getRepartitionRuleType() {
        return repartitionType;
    }

    @Override
    public void setRepartitionRuleType(byte rule) {
        if (!KupWinningsRepartitionRuleType.getTypes().contains(rule)) {
            log.error("Kup Winninws Type " + String.valueOf(rule)
                    + " does not exist");
        } else {
            this.repartitionType = rule;
        }
    }

    @Override
    public String getStakeCurrency() {
        if (stakeCurrency == null) {
            stakeCurrency = CurrencyType.EURO;
        }
        return stakeCurrency;
    }

    @Override
    public void setStakeCurrency(String currency) {
        this.stakeCurrency = currency;
    }

    @Override
    public List<TournamentSeason> getBettableTournaments() {
        if (tournamentSeasons == null) {
            tournamentSeasons = new ArrayList<TournamentSeason>();
        }
        return tournamentSeasons;
    }

    @Override
    public void setBettableTournaments(List<TournamentSeason> tournaments) {
        this.tournamentSeasons = tournaments;
    }

    @Override
    public List<TournamentGame> getBettableGames() {
        if (games == null) {
            games = new ArrayList<TournamentGame>();
        }
        return games;
    }

    @Override
    public void setBettableGames(List<TournamentGame> games) {
        this.games = games;
    }

    @Override
    public List<TournamentRound> getBettableRounds() {
        if (rounds == null) {
            rounds = new ArrayList<TournamentRound>();
        }
        return rounds;
    }

    @Override
    public void setBettableRounds(List<TournamentRound> rounds) {
        this.rounds = rounds;
    }

    @Override
    public List<TournamentStage> getBettableStages() {
        if (stages == null) {
            stages = new ArrayList<TournamentStage>();
        }
        return stages;
    }

    @Override
    public void setBettableStages(List<TournamentStage> stages) {
        this.stages = stages;
    }

    @Override
    public Map<String, Integer> getPredictionPointsDistributionRule() {
        return pointsDistributionRules;
    }

    @Override
    public void setPredictionPointsDistributionRule(Map<String, Integer> rules) {
        this.pointsDistributionRules = rules;
    }

    @Override
    public Date getEffectiveStartDate() {
        final Date d = getFirstEventStartDate();
        final Calendar ref = Calendar.getInstance();
        ref.setTime(d);
        ref.add(Calendar.SECOND, -CoreConstants.TIME_TO_BET_BEFORE_EVENT_STARTS);
        return ref.getTime();
    }

    @Override
    public Date getEffectiveEndDate() {
        // Date at which the last betable sport event of the kup will start.
        Date d = getLastEventStartDate();
        final Calendar ref = Calendar.getInstance();
        if (d == null) {
            d = getEndDate();
        }
        if (d == null) {
            return null;
        }
        ref.setTime(d);
        ref.add(Calendar.SECOND, -CoreConstants.TIME_TO_BET_BEFORE_EVENT_STARTS);
        return ref.getTime();
    }

    @Override
    public Date getCloseDate() {

        // Date at which the last betable sport event of the kup will start.
        final Date d = getLastEventStartDate();
        final Calendar ref = Calendar.getInstance();
        ref.setTime(d);
        ref.add(Calendar.SECOND, -CoreConstants.TIME_TO_BET_BEFORE_EVENT_STARTS);

        // End date specified by the administrator (i.e. : Sofun Gaming).
        // For instance, players can be requested to save their predictions 1
        // week before the sport event(s) actual starts.
        // In this case they can only follow the results / loading after
        // effective end date but cannot save any predictions nor
        // bet anymore after this date is reached.
        final Date actualEndDate = getEndDate();
        final Calendar actualEndDateRef = Calendar.getInstance();
        actualEndDateRef.setTime(actualEndDate);

        // The effective end date will be the closest date from now.
        if (actualEndDateRef.compareTo(ref) < 0) {
            return actualEndDateRef.getTime();
        } else {
            return ref.getTime();
        }

    }

    @Override
    public String getMetaType() {
        return metaType;
    }

    @Override
    public void setMetaType(String metaType) {
        this.metaType = metaType;
    }

    /**
     * Returns the Kup's first betable event start date.
     * 
     * <p>
     * 
     * Returns the specified start date of the Kup if no events and / or start
     * date of events are unknown
     * 
     * @return a {@link Date} instance.
     */
    private Date getFirstEventStartDate() {
        Date d = null;
        for (TournamentSeason season : getBettableTournaments()) {
            Date startDate = season.getStartDate();
            if (d == null) {
                d = startDate;
            } else if (startDate != null && startDate.compareTo(d) < 0) {
                d = season.getStartDate();
            }
        }
        for (TournamentStage stage : getBettableStages()) {
            Date startDate = stage.getStartDate();
            if (d == null) {
                d = startDate;
            } else if (startDate != null && startDate.compareTo(d) < 0) {
                d = stage.getStartDate();
            }
        }
        for (TournamentRound round : getBettableRounds()) {
            Date startDate = round.getStartDate();
            if (d == null) {
                d = startDate;
            } else if (startDate != null && startDate.compareTo(d) < 0) {
                d = round.getStartDate();
            }
        }
        for (TournamentGame game : getBettableGames()) {
            Date startDate = game.getStartDate();
            if (d == null) {
                d = startDate;
            } else if (startDate != null && startDate.compareTo(d) < 0) {
                d = game.getStartDate();
            }
        }
        if (d == null) {
            d = getStartDate();
        }
        return d;
    }

    /**
     * Returns the Kup's last betable event start date.
     * 
     * <p>
     * 
     * Returns the specified start date of the Kup if no events and / or start
     * date of events are unknown
     * 
     * @return a {@link Date} instance.
     */
    private Date getLastEventStartDate() {
        Date d = null;
        for (TournamentSeason season : getBettableTournaments()) {
            Date startDate = season.getStartDate();
            if (startDate != null) {
                if (d == null) {
                    d = startDate;
                } else {
                    if (startDate.compareTo(d) > 0) {
                        d = startDate;
                    }
                }
            }
        }
        for (TournamentStage stage : getBettableStages()) {
            Date startDate = stage.getStartDate();
            if (startDate != null) {
                if (d == null) {
                    d = startDate;
                } else {
                    if (startDate.compareTo(d) > 0) {
                        d = startDate;
                    }
                }
            }
        }
        for (TournamentRound round : getBettableRounds()) {
            Date startDate = round.getStartDate();
            if (startDate != null) {
                if (d == null) {
                    d = startDate;
                } else {
                    if (startDate.compareTo(d) > 0) {
                        d = startDate;
                    }
                }
            }
        }
        for (TournamentGame game : getBettableGames()) {
            Date startDate = game.getStartDate();
            if (startDate != null) {
                if (d == null) {
                    d = startDate;
                } else {
                    if (startDate.compareTo(d) > 0) {
                        d = startDate;
                    }
                }
            }
        }
        return d;
    }

    @Override
    public boolean isTemplate() {
        return isTemplate;
    }

    @Override
    public void setTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    @Override
    public void addBettableTournament(TournamentSeason season) {
        if (!getBettableTournaments().contains(season)) {
            tournamentSeasons.add(season);
        }
    }

    @Override
    public void addBettableGame(TournamentGame game) {
        if (!getBettableGames().contains(game)) {
            games.add(game);
        }
    }

    @Override
    public void addBettableRound(TournamentRound round) {
        if (!getBettableRounds().contains(round)) {
            rounds.add(round);
        }
    }

    @Override
    public void addBettableStage(TournamentStage stage) {
        if (!getBettableStages().contains(stage)) {
            stages.add(stage);
        }
    }

    @Override
    public List<Question> getBettableQuestion() {
        if (questions == null) {
            questions = new ArrayList<Question>();
        }
        return questions;
    }

    @Override
    public void setBettableQuestion(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public void addBettableQuestion(Question question) {
        if (!getBettableQuestion().contains(question)) {
            questions.add(question);
        }
    }

    @Override
    public int getGuaranteedPrice() {
        return guaranteedPrice;
    }

    @Override
    public void setGuaranteedPrice(int guaranteedPrice) {
        this.guaranteedPrice = guaranteedPrice;
    }

    @Override
    public float getRakeAmount() {
        float effective = getJackpot();
        if (effective <= getGuaranteedPrice()) {
            effective = getGuaranteedPrice();
        }
        if (effective == 0 || getJackpot() <= getGuaranteedPrice()) {
            return 0;
        } else {
            final float rake = getRakePercentage();
            if (rake == 0) {
                return 0;
            } else {
                float theoritical = effective * rake;
                // If not enough to rake and stay above guaranteed price sofun
                // takes what above until the rack can be applied.
                if (getJackpot() - theoritical < getGuaranteedPrice()) {
                    return getJackpot() - getGuaranteedPrice();
                } else {
                    return theoritical;
                }
            }
        }
    }

    @Override
    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    @Override
    public int getNbParticipants() {
        return nbParticipants;
    }

    @Override
    public void setNbParticipants(int nbParticipants) {
        this.nbParticipants = nbParticipants;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int computeDuration() {
        final Calendar cstart = Calendar.getInstance();
        final Calendar cend = Calendar.getInstance();
        if (cstart != null && cend != null) {
            cstart.setTime(getEffectiveStartDate());
            cend.setTime(getEffectiveEndDate());
            final long milliseconds1 = cstart.getTimeInMillis();
            final long milliseconds2 = cend.getTimeInMillis();
            final long diff = milliseconds2 - milliseconds1;
            return Math.round(diff / (60 * 1000));
        }
        return 0;
    }

    @Override
    public List<QuestionKupTiebreaker> getQuestionsTiebreaker() {
        if (questionsTiebreaker == null) {
            questionsTiebreaker = new ArrayList<QuestionKupTiebreaker>();
        }
        return questionsTiebreaker;
    }

    @Override
    public void setQuestionsTiebreaker(List<QuestionKupTiebreaker> questions) {
        this.questionsTiebreaker = questions;
    }

    @Override
    public void addQuestionTiebreaker(QuestionKupTiebreaker question) {
        if (!getQuestionsTiebreaker().contains(question)) {
            questionsTiebreaker.add(question);
        }
    }

    @Override
    public boolean isPartner() {
        return isPartner;
    }

    @Override
    public void setPartner(boolean isPartner) {
        this.isPartner = isPartner;
    }

    @Override
    public List<Tournament> getSportTournaments() {
        if (tournaments == null) {
            tournaments = new ArrayList<Tournament>();
        }
        return tournaments;
    }

    @Override
    public void setSportTournaments(List<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

    @Override
    public void addSportTournament(Tournament tournament) {
        if (!getSportTournaments().contains(tournament)) {
            tournaments.add(tournament);
        }
    }

    @Override
    public boolean isModerated() {
        return moderated;
    }

    @Override
    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

}
