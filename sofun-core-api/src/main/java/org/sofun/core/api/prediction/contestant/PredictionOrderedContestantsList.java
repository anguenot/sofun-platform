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

package org.sofun.core.api.prediction.contestant;

import java.util.List;

import org.sofun.core.api.prediction.Prediction;
import org.sofun.core.api.sport.SportContestant;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface PredictionOrderedContestantsList extends Prediction {

    List<SportContestant> getContestants();

    void setContestants(List<SportContestant> contestants);

    void addContestant(SportContestant contestant);

    void reset();

    PredictionOrderedContestantResult getResultsForPosition(int position);
    
    boolean isDrawn();
    
    void setDrawn(boolean drawn);

}
