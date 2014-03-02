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

package org.sofun.core.team.table;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.table.TeamTournamentRoundRankingTable;
import org.sofun.core.sport.tournament.TournamentRoundImpl;

/**
 * Team ranking table for a given Tournament Stage.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "teams_ranking_tables_tournament_rounds")
public class TeamTournamentRoundRankingTableImpl extends TeamRankingTableImpl
        implements TeamTournamentRoundRankingTable {

    private static final long serialVersionUID = -5045848368353921901L;

    @ManyToOne(
            targetEntity = TournamentRoundImpl.class,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "round_id")
    protected TournamentRound round;

    public TeamTournamentRoundRankingTableImpl() {
        super();
    }

    public TeamTournamentRoundRankingTableImpl(Team team, TournamentRound round) {
        super(team);
        setTournamentRound(round);
    }

    @Override
    public TournamentRound getTournamentRound() {
        return round;
    }

    @Override
    public void setTournamentRound(TournamentRound round) {
        this.round = round;
    }

}
