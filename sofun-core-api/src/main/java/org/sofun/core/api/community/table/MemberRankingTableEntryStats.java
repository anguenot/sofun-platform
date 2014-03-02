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

package org.sofun.core.api.community.table;

import java.io.Serializable;

import org.sofun.core.api.prediction.Prediction;

/**
 * Member Ranking Table Entry Statistics.
 * 
 * <p/>
 * Typically bound to a {@link MemberRankingTableEntry}.
 * 
 * @see {@link MemberRankingTableEntry}
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface MemberRankingTableEntryStats extends Serializable {

    /**
     * Returns the number of predictions to which these statistics apply.
     * 
     * @return a {@link Long}
     */
    long getNumberOfPredictions();

    /**
     * Sets the number of predictions to which these statistics apply.
     * 
     * @param numberOfPredictions: a postive {@link Long}
     */
    void setNumberOfPredictions(long numberOfPredictions);

    /**
     * Returns the percentage of succcess.
     * 
     * <p/>
     * Typically bound to a {@link MemberRankingTableEntry} and
     * {@link Prediction}
     * 
     * @return a {@link Double}
     */
    double getPercentageSuccess();

    /**
     * Sets the percentage of succcess.
     * 
     * @param percentageSuccess: a positive {@link Double}
     */
    void setPercentageSuccess(double percentageSuccess);

}
