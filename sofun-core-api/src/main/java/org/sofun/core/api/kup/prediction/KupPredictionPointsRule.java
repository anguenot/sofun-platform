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

package org.sofun.core.api.kup.prediction;

import java.io.Serializable;

import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupService;
import org.sofun.core.api.prediction.Prediction;

/**
 * Prediction points rule base interface.
 * 
 * <p/>
 * 
 * Can be extended by plugins.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface KupPredictionPointsRule extends Serializable {

    /**
     * Computes and returns the amount of points for a given prediction in the
     * context of a given kup.
     * 
     * @param kup: a {@link Kup} instance.
     * @param predicton: a {@link Prediction}.
     * @param kups: {@link KupService} instance
     * @return a number of points as an integer.
     * @throws CoreException if missing rules.
     */
    int getPointsFor(Kup kup, Prediction prediction, KupService kups)
            throws CoreException;

}
