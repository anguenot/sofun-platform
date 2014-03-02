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

package org.sofun.core.api.banking;

import org.sofun.core.api.kup.Kup;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface SofunTransaction extends Transaction {

    /**
     * Returns the Kup associated with this transaction.
     * 
     * @return a {@link Kup} instance
     */
    Kup getKup();

    /**
     * Sets the Kup associated with this transaction.
     * 
     * @param kup: a {@link Kup} instance
     */
    void setKup(Kup kup);

}
