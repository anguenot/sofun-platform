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

package org.sofun.platform.web.rest.api.tournament;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.sofun.platform.web.rest.api.sport.ReSTSportContestant;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface ReSTTournamentGame extends Comparable<ReSTTournamentGame>, Serializable {

    String getUuid();

    void setUuid(String uuid);

    Date getStartDate();

    void setStartDate(Date startDate);

    Date getEndDate();

    void setEndDate(Date endDate);

    String getStatus();

    void setStatus(String status);

    List<ReSTSportContestant> getTeams();

    void setTeams(List<ReSTSportContestant> teams);

    ReSTSportContestant getWinner();

    void setWinner(ReSTSportContestant winner);

    ReSTTournamentStage getStage();

    void setStage(ReSTTournamentStage stage);

    ReSTTournamentRound getRound();

    void setRound(ReSTTournamentRound round);

    Date getScoreLastUpdated();

    void setScoreLastUpdated(Date scoreLastUpdated);

    List<Integer> getScore();

    void setScore(List<Integer> score);

    String getDescription();

    void setDescription(String description);
}
