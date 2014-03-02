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

package org.sofun.core.api.kup.bet;

import java.util.ArrayList;
import java.util.List;

import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupRankingTable;
import org.sofun.core.api.member.Member;

/**
 * Kyo Winnings Repartition Rule Type.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class KupWinningsRepartitionRuleType {

    /**
     * Type 1: One (1) winner. The {@link Member} ranking #1 in the
     * corresponding {@link KupRankingTable} gets 100% of {@link Kup} jackpot.
     */
    public static final byte TYPE_1 = 1;

    /**
     * Type 2: Two (2) winners. The 2 {@link Member} ranking #1 and #2 in the
     * corresponding {@link KupRankingTable} ge 50% each of the {@link Kup}
     * jackpot.
     */
    public static final byte TYPE_2 = 2;

    /**
     * Type 3: Three (3) winners. The 3 {@link Member} ranking #1, #2 and #3 in
     * the corresponding {@link KupRankingTable} get respectively 70%, 20% and
     * 10% of the {@link Kup} jackpot.
     */
    public static final byte TYPE_3 = 3;

    /**
     * Type4: Ten (10) winners. The 10 {@link Member} ranking from #1 to #10 in
     * the corresponding {@link KupRankingTable} get the following: 25%, 20%,
     * 15%, 10%, 5%, 5%, 5%, 5%, 5%, 5%
     */
    public static final byte TYPE_4 = 4;

    /**
     * Type5: Thirteen (13) winners. The 13 {@link Member} ranking from #1 to
     * #13 in the corresponding {@link KupRankingTable} get the following: 23%,
     * 15%, 13%, 10%, 7%, 4%, 4%, 4%, 4%, 4%, 4%, 4%, 4%,
     */
    public static final byte TYPE_5 = 5;

    /**
     * Type6: Twenty (20) winners. The 20 {@link Member} ranking from #1 to #20
     * in the corresponding {@link KupRankingTable} get the following: 20%, 12%,
     * 10%, 8%, 5%, 5%, 5%, 5%, 5%, 5%, 2%, 2%, 2%, 2%, 2%, 2%, 2%, 2%, 2%, 2%
     */
    public static final byte TYPE_6 = 6;

    /**
     * Type55: Five (5) winners. The 5 {@link Member} ranking from #1 to #5 in
     * the corresponding {@link KupRankingTable} get the following: 30%, 25%,
     * 20%, 15%, 10%.
     */
    public static final byte TYPE_55 = 55;

    /**
     * Type30: Thirty (30) winners. The 30 {@link Member} ranking from #1 to #30
     * in the corresponding {@link KupRankingTable} get the following:
     * <ul>
     * <li>1er : 15%</li>
     * <li>2e : 10 %</li>
     * <li>3e : 7%</li>
     * <li>4e up to 10e : 4%</li>
     * <li>11e up to 30e : 2%</li>
     * </ul>
     */
    public static final byte TYPE_30 = 30;

    public static final List<Byte> getTypes() {
        final List<Byte> types = new ArrayList<Byte>();
        types.add(TYPE_1);
        types.add(TYPE_2);
        types.add(TYPE_3);
        types.add(TYPE_4);
        types.add(TYPE_5);
        types.add(TYPE_6);
        types.add(TYPE_55);
        types.add(TYPE_30);
        return types;
    }

}
