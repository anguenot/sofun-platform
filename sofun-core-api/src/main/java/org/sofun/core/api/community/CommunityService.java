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

package org.sofun.core.api.community;

import java.io.Serializable;
import java.util.List;

/**
 * Community Service
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface CommunityService extends Serializable {

    /**
     * Persists a {@link Community} instance.
     * 
     * @param community: a {@link Community} instance.
     * @return the newly persisted {@link Community} instance or null if already
     *         exists
     */
    Community addCommunity(Community community);

    /**
     * Add a new {@link Community} given its unique name.
     * 
     * @see {@link CommunityService#exists(String)}
     * 
     * @param name: name of the {@link Community}.
     * @return a {@link Community} instance or null if already exists
     */
    Community addCommunity(String name);

    /**
     * Returns the total number of {@link Community} instances.
     * 
     * @return the total number of {@link Community} instances.
     */
    long count();

    /**
     * Remove a {@link Community} given its instance.
     * 
     * @param community: a {@link Community} instance.
     * @return a {@link Community} instance or null if not found
     */
    Community delCommunity(Community community);

    /**
     * Remove a community given its unique name
     * 
     * @param name: a unique {@link Community} instance's name.
     * @return the deleted {@link Community} instance or null if not found.
     */
    Community delCommunity(String name);

    /**
     * Does a {@link Community} instance carry this unique name already?
     * 
     * @param name: then name of the {@link Community}
     * @return true or false.
     */
    boolean exists(String name);

    /**
     * Returns a {@link Community} given its id.
     * 
     * @param id: the id of the {@link Community}
     * @return a {@link Community} or null if not found.
     */
    Community getCommunityById(long id);

    /**
     * Returns a {@link Community} given its name.
     * 
     * @param name: the name of the {@link Community}
     * @return a {@link Community} or null if not found.
     */
    Community getCommunityByName(String name);

    /**
     * Returns all names for registered {@link Community}s.
     * 
     * @return a {@link List} of unique {@link Community} names.
     */
    List<String> getCommunityNames();

}
