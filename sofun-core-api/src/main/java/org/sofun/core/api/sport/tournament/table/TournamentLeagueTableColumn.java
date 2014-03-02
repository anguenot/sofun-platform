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
 * Tournament league table column.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface TournamentLeagueTableColumn extends
        Comparable<TournamentLeagueTableColumn>, Serializable {

    long getId();

    /**
     * Returns the column key.
     * 
     * @see {@link TournamentLeagueTableKey}
     * 
     * @return a {@link String}
     */
    String getColumnKey();

    /**
     * Sets the column key.
     * 
     * @see {@link TournamentLeagueTableKey}
     * 
     * @param key: a {@link String}
     */
    void setColumnKey(String key);

    /**
     * Returns the column value.
     * 
     * @return a {@link String}
     */
    String getColumnValue();

    /**
     * Sets the column value.
     * 
     * @param value: a {@link String}
     */
    void setColumnValue(String value);

    /**
     * Returns the corresponding row.
     * 
     * @return a {@link TournamentLeagueTableRow}
     */
    TournamentLeagueTableRow getRow();

    /**
     * Sets the corresponding row.
     * 
     * @param row: a {@link TournamentLeagueTableRow}
     */
    void setRow(TournamentLeagueTableRow row);

}
