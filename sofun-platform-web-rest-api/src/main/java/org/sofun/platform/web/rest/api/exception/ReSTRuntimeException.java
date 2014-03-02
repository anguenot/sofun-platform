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

package org.sofun.platform.web.rest.api.exception;

/**
 * ReST runtime exception.
 * 
 * <p />
 * 
 * Wraps core exceptions.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTRuntimeException extends ReSTException {

    private static final long serialVersionUID = 3500540399613782747L;

    public ReSTRuntimeException(int statusCode) {
        this.statusCode = statusCode;
        fillInStackTrace();
    }

    public ReSTRuntimeException() {
        this(500);
    }

    public ReSTRuntimeException(String message) {
        super(message);
        this.statusCode = 500;
        fillInStackTrace();
    }

    public ReSTRuntimeException(int statusCode, String message) {
        this(message);
        this.statusCode = statusCode;
        fillInStackTrace();
    }

}
