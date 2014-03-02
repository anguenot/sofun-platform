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

package org.sofun.platform.web.rest.api.member;

import java.io.Serializable;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTMemberRakingStats implements Serializable {

    private static final long serialVersionUID = 2641208091393478886L;

    protected long generalRankingPosition;

    protected long totalCommunityMembers;

    protected long friendsRankingPosition;

    protected long totalFriendsMembers;

    protected long totalPoints = 0;

    protected long numberOfPreditions = 0;

    protected double percentageSuccess = 0.0;

    public ReSTMemberRakingStats() {

    }

    public ReSTMemberRakingStats(long generalRankingPosition,
            long totalCommunityMembers, long friendsRankingPosition,
            long totalFriendsMembers, int totalPoints,
            long numberOfPredictions, double percentageSuccess) {
        this();
        this.generalRankingPosition = generalRankingPosition;
        this.totalCommunityMembers = totalCommunityMembers;
        this.friendsRankingPosition = friendsRankingPosition;
        this.totalFriendsMembers = totalFriendsMembers;
        this.totalPoints = totalPoints;
        this.numberOfPreditions = numberOfPredictions;
        this.percentageSuccess = percentageSuccess;
    }

    public long getGeneralRankingPosition() {
        return generalRankingPosition;
    }

    public void setGeneralRankingPosition(long generalRankingPosition) {
        this.generalRankingPosition = generalRankingPosition;
    }

    public long getTotalCommunityMembers() {
        return totalCommunityMembers;
    }

    public void setTotalCommunityMembers(long totalCommunityMembers) {
        this.totalCommunityMembers = totalCommunityMembers;
    }

    public long getFriendsRankingPosition() {
        return friendsRankingPosition;
    }

    public void setFriendsRankingPosition(long friendsRankingPosition) {
        this.friendsRankingPosition = friendsRankingPosition;
    }

    public long getTotalFriendsMembers() {
        return totalFriendsMembers;
    }

    public void setTotalFriendsMembers(long totalFriendsMembers) {
        this.totalFriendsMembers = totalFriendsMembers;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public long getNumberOfPreditions() {
        return numberOfPreditions;
    }

    public void setNumberOfPreditions(long numberOfPreditions) {
        this.numberOfPreditions = numberOfPreditions;
    }

    public double getPercentageSuccess() {
        return percentageSuccess;
    }

    public void setPercentageSuccess(double precentageSuccess) {
        this.percentageSuccess = precentageSuccess;
    }

}
