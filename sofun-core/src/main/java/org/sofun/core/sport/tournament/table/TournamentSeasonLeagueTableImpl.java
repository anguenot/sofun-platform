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

import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.table.TournamentSeasonLeagueTable;
import org.sofun.core.sport.tournament.TournamentSeasonImpl;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_league_tables_seasons")
public class TournamentSeasonLeagueTableImpl extends TournamentLeagueTableImpl
        implements TournamentSeasonLeagueTable {

    private static final long serialVersionUID = 8877391060268350316L;

    @ManyToOne(
            targetEntity = TournamentSeasonImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "season_id")
    protected TournamentSeason season;

    public TournamentSeasonLeagueTableImpl() {
        super();
    }

    public TournamentSeasonLeagueTableImpl(String name) {
        super(name);
    }

    public TournamentSeasonLeagueTableImpl(String name, String type) {
        this(name);
        this.type = type;
    }

    @Override
    public TournamentSeason getTournamentSeason() {
        return season;
    }

    @Override
    public void setTournamentSeason(TournamentSeason season) {
        this.season = season;
    }

}
