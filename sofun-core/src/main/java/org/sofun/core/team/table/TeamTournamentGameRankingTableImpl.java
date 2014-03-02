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

import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.table.TeamTournamentGameRankingTable;
import org.sofun.core.sport.tournament.TournamentGameImpl;

/**
 * Team ranking table for a given Tournament Stage.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "teams_ranking_tables_tournament_games")
public class TeamTournamentGameRankingTableImpl extends TeamRankingTableImpl
        implements TeamTournamentGameRankingTable {

    private static final long serialVersionUID = -5045848368353921901L;

    @ManyToOne(targetEntity = TournamentGameImpl.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    protected TournamentGame game;

    public TeamTournamentGameRankingTableImpl() {
        super();
    }

    public TeamTournamentGameRankingTableImpl(Team team, TournamentGame game) {
        super(team);
        setTournamentGame(game);
    }

    @Override
    public TournamentGame getTournamentGame() {
        return game;
    }

    @Override
    public void setTournamentGame(TournamentGame game) {
        this.game = game;
    }

}
