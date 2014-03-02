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


/**
 * Tournament league table key.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface TournamentLeagueTableKey extends Serializable {

    /**
     * Returns the table key.
     * <p />
     * For instance, positionTotal, goalsForHome, etc...
     * 
     * @return a {@link String}
     */
    String getKey();

    /**
     * Sets the table key.
     * <p />
     * For instance, positionTotal, goalsForHome, etc...
     * 
     * @return a {@link String}
     */
    void setKey(String key);

    /**
     * Returns the table column name.
     * <p />
     * For instance, Pos, Change, Goals, etc...
     * 
     * @return a {@link String}
     */
    String getColumnName();

    /**
     * Sets the table key.
     * <p />
     * For instance, Pos, Change, Goals, etc...
     * 
     * @return a {@link String}
     */
    void setColumnName(String columnName);
    
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

}
