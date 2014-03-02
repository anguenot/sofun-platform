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
 * ReST validation exception.
 * 
 * <p />
 * 
 * Used in case of bad user parameters for instance.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTValidationException extends ReSTException {

    private static final long serialVersionUID = 5383296225300281398L;

    public ReSTValidationException(int statusCode) {
        this.statusCode = statusCode;
        fillInStackTrace();
    }

    public ReSTValidationException(int statusCode, String message) {
        this(message);
        this.statusCode = statusCode;
        fillInStackTrace();
    }

    public ReSTValidationException() {
        super(400);
    }

    public ReSTValidationException(String message) {
        super(message);
        this.statusCode = 400;
        fillInStackTrace();
    }

}
