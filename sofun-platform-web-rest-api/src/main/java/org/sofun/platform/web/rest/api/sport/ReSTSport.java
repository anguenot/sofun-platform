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

package org.sofun.platform.web.rest.api.sport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.SportCategory;

/**
 * Sport Web API.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTSport implements Serializable {

    private static final long serialVersionUID = 1248099219932808263L;

    protected long uuid;

    protected String name;

    protected List<ReSTSportCategory> categories = new ArrayList<ReSTSportCategory>();

    public ReSTSport() {
        super();
    }

    public ReSTSport(Sport coreSport) {
        this();
        setUuid(coreSport.getUUID());
        setName(coreSport.getName());
        if (coreSport.getCategories() != null) {
            for (SportCategory c : coreSport.getCategories()) {
                categories.add(new ReSTSportCategory(c));
            }
        }
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ReSTSportCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<ReSTSportCategory> categories) {
        this.categories = categories;
    }

}
