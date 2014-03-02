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

package org.sofun.core.sport.tournament;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sofun.core.api.sport.tournament.TournamentGameScore;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_games_scores")
public class TournamentGameScoreImpl implements TournamentGameScore {

    private static final long serialVersionUID = 3388322454588707283L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "last_updated", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastUpdated;

    @Column(name = "score_team_1")
    protected int scoreTeam1 = 0;

    @Column(name = "score_team_2")
    protected int scoreTeam2 = 0;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int getScoreTeam1() {
        return scoreTeam1;
    }

    @Override
    public void setScoreTeam1(int score) {
        this.scoreTeam1 = score;
    }

    @Override
    public int getScoreTeam2() {
        return scoreTeam2;
    }

    @Override
    public void setScoreTeam2(int score) {
        this.scoreTeam2 = score;
    }

    @Override
    public Date getLastUpdated() {
        if (lastUpdated == null) {
            return null;
        }
        return (Date) lastUpdated.clone();
    }

    @Override
    public void setLastUpdated(Date lastUpdated) {
        if (lastUpdated != null) {
            this.lastUpdated = (Date) lastUpdated.clone();
        }
    }

    @Override
    public List<Integer> getScore() {
        final List<Integer> score = new ArrayList<Integer>();
        score.add(getScoreTeam1());
        score.add(getScoreTeam2());
        return score;
    }

    @PrePersist
    @PreUpdate
    protected void onCreate() {
        this.lastUpdated = new Date();
    }

}
