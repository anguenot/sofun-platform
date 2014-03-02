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

package org.sofun.core.api.kup;

import org.sofun.core.api.community.table.MemberRankingTable;

/**
 * Kup Ranking Table.
 * 
 * <p/>
 * 
 * A {@link KupRankingtable} is unique for a given {@link Kup} during the life
 * cycle of Kup.
 * 
 * <p/>
 * 
 * The ranking table is used for the winnings when the Kup is closed and
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface KupRankingTable extends MemberRankingTable {

    /**
     * Returns the corresponding {@link Kup}
     * 
     * @return a {@link Kup} instance.
     */
    Kup getKup();

}
