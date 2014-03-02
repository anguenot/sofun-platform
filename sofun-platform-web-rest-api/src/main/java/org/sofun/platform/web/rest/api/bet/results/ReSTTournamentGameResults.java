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

package org.sofun.platform.web.rest.api.bet.results;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sofun.platform.web.rest.api.member.ReSTMember;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTTournamentGameResults implements Serializable {

    private static final long serialVersionUID = -3478061138121329530L;

    protected int totalPoints = 0;

    protected int percentageSuccess = 0;

    protected ReSTMember member;

    protected List<ReSTTournamentGameResult> results = new ArrayList<ReSTTournamentGameResult>();

    public ReSTTournamentGameResults() {
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getPercentageSuccess() {
        return percentageSuccess;
    }

    public void setPercentageSuccess(int percentageSuccess) {
        this.percentageSuccess = percentageSuccess;
    }

    public ReSTMember getMember() {
        return member;
    }

    public void setMember(ReSTMember member) {
        this.member = member;
    }

    public List<ReSTTournamentGameResult> getResults() {
        return results;
    }

    public void setResults(List<ReSTTournamentGameResult> results) {
        this.results = results;
    }

}
