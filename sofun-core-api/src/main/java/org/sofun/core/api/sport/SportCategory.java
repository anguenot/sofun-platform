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

package org.sofun.core.api.sport;

import java.io.Serializable;

/**
 * Sport category.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface SportCategory extends Serializable {

    /**
     * Returns the internal unique identifier.
     * 
     * @return {@link Long}
     */
    long getId();

    /**
     * Sets the internal unique identifier.
     * 
     * @param id: {@link Long}
     */
    void setId(long id);

    /**
     * Returns the UUID.
     * 
     * @return {@link Long}
     */
    long getUUID();

    /**
     * Sets the UUID.
     * 
     * @param uuid: {@link Long}
     */
    void setUUID(long uuid);

    /**
     * Returns the category name.
     * 
     * @return a {@link String} not null.
     */
    String getName();

    /**
     * Sets the category name.
     * 
     * @param name: a {@link String} not null.
     */
    void setName(String name);

}
