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

package org.sofun.platform.arjel.banned.api.exception;

/**
 * Base ARJEL Banned Exception.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ARJELBannedException extends Exception {

    private static final long serialVersionUID = 5048420246765416433L;

    public ARJELBannedException() {
        fillInStackTrace();
    }

    public ARJELBannedException(String message) {
        super(message);
        fillInStackTrace();
    }

    public ARJELBannedException(Exception e) {
        super(e.getMessage());
    }

}
