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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.sofun.core.api.community.table.MemberRankingTableEntryStats;

/**
 * Member Ranking Table Entry Statistics Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members_ranking_tables_entries_stats")
public class MemberRankingTableEntryStatsImpl implements
        MemberRankingTableEntryStats {

    private static final long serialVersionUID = -6097725052658937652L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(
            name = "number_of_predictions",
            nullable = false,
            columnDefinition = "int default 0")
    protected long numberOfPredictions = 0;

    @Column(
            name = "percentage_success",
            columnDefinition = "Decimal(10,2) default '0.0'",
            nullable = false)
    protected double percentageSuccess = 0.0;

    public MemberRankingTableEntryStatsImpl() {
        super();
    }

    @Override
    public long getNumberOfPredictions() {
        return numberOfPredictions;
    }

    @Override
    public void setNumberOfPredictions(long numberOfPredictions) {
        this.numberOfPredictions = numberOfPredictions;
    }

    @Override
    public double getPercentageSuccess() {
        return percentageSuccess;
    }

    @Override
    public void setPercentageSuccess(double percentageSuccess) {
        this.percentageSuccess = percentageSuccess;
    }

}
