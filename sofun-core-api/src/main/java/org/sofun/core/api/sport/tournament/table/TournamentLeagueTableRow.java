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

package org.sofun.core.api.sport.tournament.table;

import java.io.Serializable;
import java.util.List;

import org.sofun.core.api.sport.SportContestant;

/**
 * Tournament league table row.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface TournamentLeagueTableRow extends Serializable {

    long getId();

    /**
     * Returns the sport contestant.
     * 
     * @return a {@link SportContestant}
     */
    SportContestant getSportContestant();

    /**
     * Sets the sport contestant.
     * 
     * @return a {@link SportContestant}
     */
    void setSportContestant(SportContestant contestants);

    /**
     * Returns the corresponding league table.
     * 
     * @return a {@link TournamentLeagueTable}
     */
    TournamentLeagueTable getLeagueTable();

    /**
     * Sets the corresponding league table.
     * 
     * @param table: a {@link TournamentLeagueTable}
     */
    void setLeagueTable(TournamentLeagueTable table);

    /**
     * Returns the columns.
     * 
     * @return a {@link List} of {@link TournamentLeagueTableColumn}
     */
    List<TournamentLeagueTableColumn> getColumns();

    /**
     * Sets the columns.
     * 
     * @param columns a {@link List} of {@link TournamentLeagueTableColumn}
     */
    void setColumns(List<TournamentLeagueTableColumn> columns);

    TournamentLeagueTableColumn getColumn(String key);

    void addColumn(TournamentLeagueTableColumn column);

}
