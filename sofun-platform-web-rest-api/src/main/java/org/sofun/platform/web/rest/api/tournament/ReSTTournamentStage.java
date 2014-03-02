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

package org.sofun.platform.web.rest.api.tournament;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.sofun.core.api.sport.tournament.TournamentStage;

/**
 * Tournament stage Web API.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTTournamentStage implements Comparable<ReSTTournamentStage>,
        Serializable {

    private static final long serialVersionUID = -1513581606361324134L;

    protected long uuid;

    protected String name;

    protected String description;

    protected Date startDate;

    protected Date endDate;

    protected String status;

    protected Map<String, String> properties = new HashMap<String, String>();

    public ReSTTournamentStage() {
        super();
    }

    public ReSTTournamentStage(TournamentStage coreStage) {
        this();
        if (coreStage != null) {
            setUuid(coreStage.getUUID());
            setName(coreStage.getName());
            setDescription(coreStage.getDescription());
            setStartDate(coreStage.getStartDate());
            setEndDate(coreStage.getEndDate());
            setProperties(coreStage.getProperties());
            setStatus(coreStage.getStatus());
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        if (startDate == null) {
            return null;
        }
        return (Date) startDate.clone();
    }

    public void setStartDate(Date startDate) {
        if (startDate != null) {
            this.startDate = (Date) startDate.clone();
        }
    }

    public Date getEndDate() {
        if (endDate == null) {
            return null;
        }
        return (Date) endDate.clone();
    }

    public void setEndDate(Date endDate) {
        if (endDate != null) {
            this.endDate = (Date) endDate.clone();
        }
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(ReSTTournamentStage o) {
        return o.getStartDate().compareTo(o.getStartDate());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof ReSTTournamentStage)) {
            return false;
        }
        ReSTTournamentStage otherStage = (ReSTTournamentStage) other;
        if (getUuid() != 0 && otherStage.getUuid() != 0) {
            return getUuid() == otherStage.getUuid();
        }
        return false;

    }
    
    @Override
    public int hashCode() {
        if (getUuid() != 0) {
            return Long.valueOf(getUuid()).hashCode();
        }
        return super.hashCode();
    }

}
