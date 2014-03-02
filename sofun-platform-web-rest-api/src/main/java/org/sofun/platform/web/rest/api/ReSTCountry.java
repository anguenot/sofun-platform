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

package org.sofun.platform.web.rest.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.sofun.core.api.country.Country;

/**
 * Country Web API.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTCountry implements Serializable {

    private static final long serialVersionUID = -6017768751068726421L;

    protected long uuid;

    protected String name;

    protected String iso;

    protected String ioc;

    protected Map<String, String> properties = new HashMap<String, String>();

    public ReSTCountry() {
        super();
    }

    public ReSTCountry(Country coreCountry, long seasonId) {
        if (coreCountry != null) {
            setUuid(coreCountry.getId());
            setName(coreCountry.getName());
            setIso(coreCountry.getISO());
            setIoc(coreCountry.getIOC());
            setProperties(coreCountry.getProperties());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getIoc() {
        return ioc;
    }

    public void setIoc(String ioc) {
        this.ioc = ioc;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
