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

package org.sofun.core.team.table;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.table.TeamRankingTable;
import org.sofun.core.community.table.MemberRankingTableImpl;
import org.sofun.core.team.TeamImpl;

/**
 * Team Ranking Table Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "teams_ranking_tables")
public class TeamRankingTableImpl extends MemberRankingTableImpl implements
        TeamRankingTable {

    private static final long serialVersionUID = 2586718915801609664L;

    @ManyToOne(
            targetEntity = TeamImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    protected Team team;

    public TeamRankingTableImpl() {
        super();
    }

    public TeamRankingTableImpl(Team team) {
        this();
        this.team = team;
        for (Member member : team.getMembers()) {
            addEntryForMember(member);
        }
    }

    @Override
    public Team getTeam() {
        return team;
    }

}
