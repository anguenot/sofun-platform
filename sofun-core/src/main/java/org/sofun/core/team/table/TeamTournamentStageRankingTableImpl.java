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

import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.table.TeamTournamentStageRankingTable;
import org.sofun.core.sport.tournament.TournamentStageImpl;

/**
 * Team ranking table for a given Tournament Stage.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "teams_ranking_tables_tournament_stages")
public class TeamTournamentStageRankingTableImpl extends TeamRankingTableImpl
        implements TeamTournamentStageRankingTable {

    private static final long serialVersionUID = -5045848368353921901L;

    @ManyToOne(
            targetEntity = TournamentStageImpl.class,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "stage_id")
    protected TournamentStage stage;

    public TeamTournamentStageRankingTableImpl() {
       super();
    }

    public TeamTournamentStageRankingTableImpl(Team team, TournamentStage stage) {
        super(team);
        setTournamentStage(stage);
    }

    @Override
    public TournamentStage getTournamentStage() {
        return stage;
    }

    @Override
    public void setTournamentStage(TournamentStage stage) {
        this.stage = stage;
    }

}
