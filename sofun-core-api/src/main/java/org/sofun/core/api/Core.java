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

 * @version $Id: Core.java 3877 2014-03-02 07:24:54Z anguenot $
 */

package org.sofun.core.api;

import java.io.Serializable;
import java.net.URL;

/**
 * Sofun Core: THE App.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface Core extends Serializable {

    /**
     * Returns the {@link Core} version.
     * 
     * @return a {@link String}
     */
    String getVersion();

    /**
     * Returns who's powering this.
     * 
     * @return Sofun :-)
     */
    String getPoweredBy();

    /**
     * Returns copyright information.
     * 
     * @return a {@link String}
     */
    String getCopyright();

    /**
     * Returns the copyright URL.
     * 
     * @return a {@link URL} instance.
     */
    URL getCopyrightURL();

    /**
     * Returns the contact emaiL.
     * 
     * @return a {@link String}
     */
    String getEmailContact();

    /**
     * Returns the server URL.
     * 
     * @return a {@link URL} instance.
     */
    URL getServerURL();

}
