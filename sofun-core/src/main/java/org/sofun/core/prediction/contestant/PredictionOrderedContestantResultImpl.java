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

package org.sofun.core.prediction.contestant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.sofun.core.api.prediction.Prediction;
import org.sofun.core.api.prediction.contestant.PredictionOrderedContestantResult;
import org.sofun.core.prediction.PredictionResultImpl;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "predictions_results_ordered_contestants")
public class PredictionOrderedContestantResultImpl extends PredictionResultImpl implements PredictionOrderedContestantResult {

    private static final long serialVersionUID = 6506397641621797196L;

    @Column(name = "position", columnDefinition = "int default 0")
    protected int position;

    public PredictionOrderedContestantResultImpl() {
        super();
    }

    public PredictionOrderedContestantResultImpl(String label, int points, Prediction prediction, int position) {
        super(label, points, prediction);
        this.position = position;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

}
