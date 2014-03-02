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

package org.sofun.core.kup.policy;

import java.util.Calendar;

import org.sofun.core.CoreConstants;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupService;
import org.sofun.core.api.prediction.PredictionService;

/**
 * Abstract Kup Life Cycle Manager
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public abstract class AbstractKupLifeCycleManager {

    /* Is the manager busy computing already? */
    protected boolean available = true;

    /* Kups Service */
    protected abstract KupService getKups();

    /* Prediction's service */
    protected abstract PredictionService getPredictions();

    /**
     * Returns the reference time at which a {@link Kup} should be on going.
     * 
     * @return: a {@link Calendar} instance.
     */
    protected Calendar getReferenceTime() {
        final Calendar ref = Calendar.getInstance();
        ref.add(Calendar.SECOND, CoreConstants.TIME_TO_BET_BEFORE_EVENT_STARTS);
        return ref;
    }

}
