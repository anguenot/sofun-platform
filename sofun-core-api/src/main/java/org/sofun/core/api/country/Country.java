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

package org.sofun.core.api.country;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.sofun.core.api.sport.SportContestant;

/**
 * Country.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface Country extends Map<String, String>, Serializable {

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
     * Returns the country name.
     * <p />
     * Local is english.
     * 
     * @return a {@link String}
     */
    String getName();

    /**
     * Sets the country name.
     * <p />
     * Local is english.
     * 
     * @param name a {@link String}
     */
    void setName(String name);

    /**
     * Returns the country IOC.
     * 
     * @return a {@link String}
     */
    String getIOC();

    /**
     * Sets the country IOC.
     * 
     * @param ioc: a {@link String}
     */
    void setIOC(String ioc);

    /**
     * Returns the country iso name. (unique)
     * 
     * @return a {@link String}
     */
    String getISO();

    /**
     * Sets the country iso name. (unique)
     * 
     * @param iso: a {@link String}
     */
    void setISO(String iso);

    /**
     * Returns the list of participants from a particular country.
     * 
     * @return a {@link List} of {@link SportContestant}
     */
    List<SportContestant> getContestants();

    /**
     * Sets the list of participants from a particular country.
     * 
     * @param contestants: a {@link List} of {@link SportContestant}
     */
    void setContestants(List<SportContestant> contestants);

    Map<String, String> getProperties();

    void setProperties(Map<String, String> properties);

}
