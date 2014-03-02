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

package org.sofun.core.team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Timeout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupStatus;
import org.sofun.core.api.local.TeamServiceLocal;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.TeamService;

/**
 * Team points timer.
 * 
 * <p>
 * Clock triggering team ranking's update.
 * </p>
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Singleton
public class TeamPointsTimer {

    private static final Log log = LogFactory.getLog(TeamPointsTimer.class);

    private boolean available = true;

    @EJB(beanName = "TeamServiceImpl", beanInterface = TeamServiceLocal.class)
    private TeamService teams;

    @Timeout
    // XXX DISABLE
    // @Schedule(minute = "*/59", hour = "*", persistent = false)
    @Lock(LockType.READ)
    public void check() throws Exception {

        if (!available) {
            return;
        } else {
            available = false;
        }

        try {

            List<Team> actives = teams.getTeamsWithActiveKups();
            log.info("Found " + actives.size()
                    + " teams for which we need to update ranking");

            List<Long> computed = new ArrayList<Long>();
            ListIterator<Team> teamItr = actives.listIterator();
            while (teamItr.hasNext()) {
                Team team = teamItr.next();
                if (computed.contains(team.getId())) {
                    continue;
                }
                team.setNbMembers(team.getMembers().size());
                long entriesTotalPoints = 0;
                log.info("Updating team ranking. Team UUID=" + team.getId()
                        + " name=" + team.getName());
                Iterator<MemberRankingTableEntry> teamEntriesItr = team
                        .getRankingTable().getEntries().iterator();
                while (teamEntriesItr.hasNext()) {
                    MemberRankingTableEntry entry = teamEntriesItr.next();
                    int total = 0;
                    Iterator<Kup> kupItr = team.getKups().iterator();
                    while (kupItr.hasNext()) {
                        Kup kup = kupItr.next();
                        if (kup.getStatus() == KupStatus.CANCELED) {
                            continue;
                        }
                        MemberRankingTableEntry teamEntry = kup
                                .getRankingTable().getEntryForMember(
                                        entry.getMember());
                        if (teamEntry != null) {
                            total += teamEntry.getValue();
                        }
                    }
                    entry.setValue(total);
                    entriesTotalPoints += total;
                    log.debug("Member with email="
                            + entry.getMember().getEmail()
                            + " in team with id=" + team.getId()
                            + " has now value=" + total);
                }
                team.getRankingTable()
                        .setEntriesTotalPoints(entriesTotalPoints);
                computed.add(team.getId());
            }

        } catch (Throwable t) {
            t.printStackTrace();
            log.error(t.getMessage());
        } finally {
            available = true;
        }
    }
}
