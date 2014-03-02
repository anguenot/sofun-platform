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

package org.sofun.platform.web.rest.api.team;

import java.io.Serializable;

import org.sofun.core.api.community.table.MemberRankingTableEntryStats;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTMemberRankingTableEntryStats implements Serializable {

    private static final long serialVersionUID = 1L;

    protected double percentageSuccess = 0.0;

    protected long numberOfPredictions = 0;

    public ReSTMemberRankingTableEntryStats() {

    }

    public ReSTMemberRankingTableEntryStats(MemberRankingTableEntryStats coreStats) {
        if (coreStats != null) {
            setPercentageSuccess(coreStats.getPercentageSuccess());
            setNumberOfPredictions(coreStats.getNumberOfPredictions());
        }
    }

    public double getPercentageSuccess() {
        return percentageSuccess;
    }

    public void setPercentageSuccess(double percentageSuccess) {
        this.percentageSuccess = percentageSuccess;
    }

    public long getNumberOfPredictions() {
        return numberOfPredictions;
    }

    public void setNumberOfPredictions(long numberOfPredictions) {
        this.numberOfPredictions = numberOfPredictions;
    }

}
