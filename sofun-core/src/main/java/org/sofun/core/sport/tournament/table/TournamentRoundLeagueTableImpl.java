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

import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.table.TournamentRoundLeagueTable;
import org.sofun.core.sport.tournament.TournamentRoundImpl;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_league_tables_rounds")
public class TournamentRoundLeagueTableImpl extends TournamentLeagueTableImpl implements TournamentRoundLeagueTable {

    private static final long serialVersionUID = 5154091546441715347L;

    @ManyToOne(targetEntity = TournamentRoundImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "round_id")
    protected TournamentRound round;

    public TournamentRoundLeagueTableImpl() {
        super();
    }

    public TournamentRoundLeagueTableImpl(String name) {
        super(name);
    }
    
    public TournamentRoundLeagueTableImpl(String name, String type) {
        this(name);
        this.type = type;
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
