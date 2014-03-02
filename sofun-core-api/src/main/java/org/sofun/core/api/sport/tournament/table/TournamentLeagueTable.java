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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.sofun.core.api.sport.SportContestant;

/**
 * Tournament League Table.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface TournamentLeagueTable extends Serializable {

    long getId();

    long getUUID();

    void setUUID(long uuid);

    /**
     * Returns the name of the league table.
     * 
     * @return a {@link String} not null.
     */
    String getName();

    /**
     * Sets the name of the league table.
     * 
     * @param name: a {@link String} not null.
     */
    void setName(String name);

    /**
     * Returns the league table array keys.
     * 
     * @return a {@link Collection} of {@link TournamentLeagueTableKey}
     */
    List<TournamentLeagueTableKey> getKeys();

    /**
     * Sets the league table array keys.
     * 
     * @param keys: a {@link TournamentLeagueTableKeys} not null.
     */
    void setKeys(List<TournamentLeagueTableKey> keys);

    /**
     * Returns the league table actual rows.
     * 
     * @return a {@link List} {@link TournamentLeagueTableRow}
     */
    List<TournamentLeagueTableRow> getRows();

    /**
     * Sets the league table actual rows.
     * 
     * @param rows: a {@link List} of {@link TournamentLeagueTableRow}
     */
    void setRows(List<TournamentLeagueTableRow> rows);

    void addRow(TournamentLeagueTableRow row);

    TournamentLeagueTableRow getRowFor(SportContestant contestant);

    /**s
     * Returns the last time the league table has been updated.
     * 
     * @return a {@link Date} not null.
     */
    Date getModified();

    /**
     * Sets the last time the league table has been updated.
     * 
     * @param date: a {@link Date} not null.
     */
    void setModified(Date modified);

    /**
     * Returns the type of league table.
     * 
     * @return a {@link String}
     */
    String getType();

    /**
     * Sets the type of league table.
     * 
     * @param type: a {@link String}
     */
    void setType(String type);
    
    List<SportContestant> getOrderedContestantsForKey(String key);

}
