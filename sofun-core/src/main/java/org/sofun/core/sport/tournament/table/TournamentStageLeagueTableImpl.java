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

import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.table.TournamentStageLeagueTable;
import org.sofun.core.sport.tournament.TournamentStageImpl;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_league_tables_stages")
public class TournamentStageLeagueTableImpl extends TournamentLeagueTableImpl
        implements TournamentStageLeagueTable {

    private static final long serialVersionUID = 3178043198798475942L;

    @ManyToOne(
            targetEntity = TournamentStageImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "stage_id")
    protected TournamentStage stage;

    public TournamentStageLeagueTableImpl() {
        super();
    }
    
    public TournamentStageLeagueTableImpl(String name) {
        super(name);
    }
    
    public TournamentStageLeagueTableImpl(String name, String type) {
        this(name);
        this.type = type;
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
