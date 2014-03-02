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

package org.sofun.core.kup.table;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupRankingTable;
import org.sofun.core.api.member.Member;
import org.sofun.core.community.table.MemberRankingTableImpl;
import org.sofun.core.kup.KupImpl;

/**
 * Kup Ranking table Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 */
@Entity
@Table(name = "kups_ranking_tables")
public class KupRankingTableImpl extends MemberRankingTableImpl implements KupRankingTable {

    private static final long serialVersionUID = 2905023991928297311L;

    @ManyToOne(targetEntity = KupImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "kup_id")
    protected Kup kup;

    public KupRankingTableImpl() {
        super();
    }

    public KupRankingTableImpl(Kup kup) {
        this();
        this.kup = kup;
        for (Member member : kup.getParticipants()) {
            addEntryForMember(member);
        }
    }

    @Override
    public Kup getKup() {
        return kup;
    }

}
