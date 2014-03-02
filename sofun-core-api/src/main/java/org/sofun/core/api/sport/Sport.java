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

 * @version $Id: Sport.java 3877 2014-03-02 07:24:54Z anguenot $
 */

package org.sofun.core.api.sport;

import java.io.Serializable;
import java.util.List;

/**
 * Sport.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface Sport extends Serializable {

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
     * @param uudi: {@link Long}
     */
    void setUUID(long uuid);

    /**
     * Returns the sport name.
     * 
     * @return a {@link String} not null
     */
    String getName();

    /**
     * Sets the sport name.
     * 
     * @param name: a {@link String} not null.
     */
    void setName(String name);

    /**
     * Returns the sport categories corresponding to this sport.
     * 
     * @return a {@link List} of {@link SportCategory}
     */
    List<SportCategory> getCategories();

    /**
     * Sets the sport categories corresponding to this sport.
     * 
     * @return a {@link List} of {@link SportCategory}
     */
    void setCategories(List<SportCategory> categories);

    /**
     * Adds a category to this sport.
     * 
     * <p />
     * 
     * If `this`is already part of this category then nothing will occur.
     * 
     * @param category: a {@link SportCategory}
     */
    void addCategory(SportCategory category);

}
