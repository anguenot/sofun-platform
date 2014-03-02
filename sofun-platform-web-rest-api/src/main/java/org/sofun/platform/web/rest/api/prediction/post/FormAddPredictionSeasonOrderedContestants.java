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

package org.sofun.platform.web.rest.api.prediction.post;


/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class FormAddPredictionSeasonOrderedContestants extends
        FormAddPredictionOrderedContestants {

    private static final long serialVersionUID = -7676890090868666706L;

    protected long seasonUUID;

    public FormAddPredictionSeasonOrderedContestants() {
       super();
    }
    
    public long getSeasonUUID() {
        return seasonUUID;
    }

    public void setSeasonUUID(long seasonUUID) {
        this.seasonUUID = seasonUUID;
    }

}
