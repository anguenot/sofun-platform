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

package org.sofun.core.sport.tournament.table;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.table.TournamentGameLeagueTable;
import org.sofun.core.sport.tournament.TournamentGameImpl;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_league_tables_games")
public class TournamentGameLeagueTableImpl extends TournamentLeagueTableImpl
        implements TournamentGameLeagueTable {

    private static final long serialVersionUID = 6609875977246285099L;

    @ManyToOne(
            targetEntity = TournamentGameImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id")
    protected TournamentGame game;

    public TournamentGameLeagueTableImpl() {
        super();
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
