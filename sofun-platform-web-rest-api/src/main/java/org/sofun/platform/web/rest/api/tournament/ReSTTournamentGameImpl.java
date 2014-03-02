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

package org.sofun.platform.web.rest.api.tournament;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameScore;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.platform.web.rest.api.sport.ReSTSportContestant;

/**
 * Tournament Game Web API.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTTournamentGameImpl implements ReSTTournamentGame {

    private static final long serialVersionUID = -2595959484660819263L;

    protected String uuid;

    protected Date startDate;

    protected Date endDate;

    protected String status;

    protected String description;

    protected List<ReSTSportContestant> teams = new ArrayList<ReSTSportContestant>();

    protected ReSTSportContestant winner;

    protected ReSTTournamentStage stage;

    protected ReSTTournamentRound round;

    protected Date scoreLastUpdated;

    protected List<Integer> score = new ArrayList<Integer>();

    protected Map<String, String> properties = new HashMap<String, String>();

    public ReSTTournamentGameImpl() {
        super();
    }

    public ReSTTournamentGameImpl(TournamentGame coreGame) {
        this();
        if (coreGame != null) {
            setUuid(coreGame.getUUID());
            setStartDate(coreGame.getStartDate());
            setEndDate(coreGame.getEndDate());
            setStatus(coreGame.getGameStatus());
            setDescription(coreGame.getDescription());
            setProperties(coreGame.getProperties());
            final long seasonId = coreGame.getRound().getStage().getSeason().getUUID();
            if (coreGame.getContestants() != null) {
                for (SportContestant c : coreGame.getContestants()) {
                    teams.add(new ReSTSportContestant(c, seasonId, false));
                }
            }

            String status = coreGame.getGameStatus();
            if (status != null && status.equals(TournamentGameStatus.TERMINATED)) {
                if (coreGame.getWinner() != null) {
                    setWinner(new ReSTSportContestant(coreGame.getWinner(), seasonId, false));
                }
            }

            if (coreGame.getRound() != null) {
                setRound(new ReSTTournamentRound(coreGame.getRound()));
                if (coreGame.getRound().getStage() != null) {
                    setStage(new ReSTTournamentStage(coreGame.getRound().getStage()));
                }
            }

            TournamentGameScore coreScore = coreGame.getScore();
            if (coreScore != null) {
                score.add(coreScore.getScoreTeam1());
                score.add(coreScore.getScoreTeam2());
            } else {
                score.add(0);
                score.add(0);
            }

        }

    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public Date getStartDate() {
        if (startDate == null) {
            return null;
        }
        return (Date) startDate.clone();
    }

    @Override
    public void setStartDate(Date startDate) {
        if (startDate != null) {
            this.startDate = (Date) startDate.clone();
        }

    }

    @Override
    public Date getEndDate() {
        if (endDate == null) {
            return null;
        }
        return (Date) endDate.clone();
    }

    @Override
    public void setEndDate(Date endDate) {
        if (endDate != null) {
            this.endDate = (Date) endDate.clone();
        }
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public List<ReSTSportContestant> getTeams() {
        return teams;
    }

    @Override
    public void setTeams(List<ReSTSportContestant> teams) {
        this.teams = teams;
    }

    @Override
    public ReSTSportContestant getWinner() {
        return winner;
    }

    @Override
    public void setWinner(ReSTSportContestant winner) {
        this.winner = winner;
    }

    @Override
    public ReSTTournamentStage getStage() {
        return stage;
    }

    @Override
    public void setStage(ReSTTournamentStage stage) {
        this.stage = stage;
    }

    @Override
    public ReSTTournamentRound getRound() {
        return round;
    }

    @Override
    public void setRound(ReSTTournamentRound round) {
        this.round = round;
    }

    @Override
    public Date getScoreLastUpdated() {
        if (scoreLastUpdated == null) {
            return null;
        }
        return (Date) scoreLastUpdated.clone();
    }

    @Override
    public void setScoreLastUpdated(Date scoreLastUpdated) {
        if (scoreLastUpdated != null) {
            this.scoreLastUpdated = (Date) scoreLastUpdated.clone();
        }
    }

    @Override
    public int compareTo(ReSTTournamentGame other) {
        if (getStartDate() != null && other != null && other.getScore() != null) {
            return getStartDate().after(other.getStartDate()) ? 1 : 0;
        } else {
            return 0;
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
        if (obj instanceof ReSTTournamentGame) {
            ReSTTournamentGame g = (ReSTTournamentGame) obj;
            return g.getUuid().equals(getUuid());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getUuid()).hashCode();
    }

    @Override
    public List<Integer> getScore() {
        return score;
    }

    @Override
    public void setScore(List<Integer> score) {
        this.score = score;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
