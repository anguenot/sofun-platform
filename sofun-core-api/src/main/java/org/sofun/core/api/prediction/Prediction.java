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

package org.sofun.core.api.prediction;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;

/**
 * Prediction.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface Prediction extends Serializable {

    long getId();

    Member getMember();

    void setMember(Member member);

    Date getCreated();

    void setCreated(Date created);

    Date getLastModified();

    void setLastModified(Date date);

    int getPoints();

    void setPoints(int points);

    boolean isPointsComputed();

    void setPointsComputed(boolean pointsComputed);

    String getType();

    void setType(String type);

    List<PredictionResult> getResults();

    void setResults(List<PredictionResult> results);
    
    void addResult(PredictionResult result);
    
    Kup getKup();
    
    void setKup(Kup kup);
}
