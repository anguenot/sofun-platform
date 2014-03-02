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

package org.test.sofun.core.team.table;

import org.junit.Test;
import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.table.TeamRankingTable;
import org.sofun.core.api.team.table.TeamTournamentGameRankingTable;
import org.sofun.core.member.MemberImpl;
import org.sofun.core.sport.tournament.TournamentGameImpl;
import org.sofun.core.team.TeamImpl;
import org.sofun.core.team.table.TeamTournamentGameRankingTableImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestTournamentGameRankingTable extends SofunCoreTestCase {

    public TestTournamentGameRankingTable(String testName) {
        super(testName);
    }

    @Test
    public void testAddTeamTournamentRoundRankingTableEntry()
            throws CoreException {

        Team t = new TeamImpl("test");

        assertEquals(t, ((TeamRankingTable) t.getRankingTable()).getTeam());

        Member m1 = new MemberImpl("julien@anguenot.org", null, null);
        Member m2 = new MemberImpl("ja@sofungaming.com", null, null);

        t.addMember(m1);
        t.addMember(m2);

        assertEquals(2, t.getRankingTable().getEntries().size());

        for (MemberRankingTableEntry e : t.getRankingTable().getEntries()) {
            e.setValue(10);
        }

        for (MemberRankingTableEntry e : t.getRankingTable().getEntries()) {
            assertEquals(10, e.getValue());
        }

        TournamentGame game = new TournamentGameImpl("1");

        TeamTournamentGameRankingTable gameTable = new TeamTournamentGameRankingTableImpl(
                t, game);

        assertEquals(game, gameTable.getTournamentGame());
        assertEquals(t, gameTable.getTeam());

        assertEquals(2, gameTable.getEntries().size());
        assertEquals(2, t.getRankingTable().getEntries().size());

        for (MemberRankingTableEntry e : gameTable.getEntries()) {
            assertEquals(0, e.getValue());
        }

        for (MemberRankingTableEntry e : t.getRankingTable().getEntries()) {
            assertEquals(10, e.getValue());
        }

    }
}
