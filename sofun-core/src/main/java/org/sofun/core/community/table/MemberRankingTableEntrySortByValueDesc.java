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

package org.sofun.core.community.table;

import java.io.Serializable;
import java.util.Comparator;

import org.sofun.core.api.community.table.MemberRankingTableEntry;

/**
 * Member ranking table entry comparator in value desc order.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class MemberRankingTableEntrySortByValueDesc implements
        Comparator<MemberRankingTableEntry>, Serializable {

    private static final long serialVersionUID = -7928715295099587295L;

    @Override
    public int compare(MemberRankingTableEntry o1, MemberRankingTableEntry o2) {
        return o2.getValue() - o1.getValue();
    }

}
