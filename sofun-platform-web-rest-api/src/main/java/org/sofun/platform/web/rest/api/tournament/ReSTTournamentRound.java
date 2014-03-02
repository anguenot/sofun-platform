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

import org.sofun.core.api.sport.tournament.TournamentRound;

/**
 * Tournament Round Web API.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTTournamentRound implements Serializable,
        Comparable<ReSTTournamentRound> {

    private static final long serialVersionUID = 6628824264166934075L;

    protected long uuid;

    protected String roundLabel;

    protected int roundNumber;

    protected String name;

    protected String description;

    protected Date startDate;

    protected Date endDate;

    protected String status;

    protected Map<String, String> properties = new HashMap<String, String>();

    public ReSTTournamentRound() {
        super();
    }

    public ReSTTournamentRound(TournamentRound coreRound) {
        this();
        if (coreRound != null) {
            setUuid(coreRound.getId());
            setRoundLabel(coreRound.getRoundLabel());
            setRoundNumber(coreRound.getRoundNumber());
            setDescription(coreRound.getDescription());
            setName(coreRound.getName());
            setStartDate(coreRound.getStartDate());
            setEndDate(coreRound.getEndDate());
            setStatus(coreRound.getStatus());
            setProperties(coreRound.getProperties());
        }
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public String getRoundLabel() {
        return roundLabel;
    }

    public void setRoundLabel(String roundLabel) {
        this.roundLabel = roundLabel;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    @Override
    public int compareTo(ReSTTournamentRound o) {
        if (o == null) {
            return 1;
        }
        return getRoundNumber() - o.getRoundNumber();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof ReSTTournamentRound) {
            ReSTTournamentRound r = (ReSTTournamentRound) obj;
            return r.getUuid() == getUuid();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getUuid()).hashCode();
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
        if (startDate != null) {
            return (Date) startDate.clone();
        }
        return null;
    }

    public void setStartDate(Date startDate) {
        if (startDate != null) {
            this.startDate = (Date) startDate.clone();
        } else {
            this.startDate = null;
        }
    }

    public Date getEndDate() {
        if (endDate != null) {
            return (Date) endDate.clone();
        }
        return null;
    }

    public void setEndDate(Date endDate) {
        if (endDate != null) {
            this.endDate = (Date) endDate.clone();
        } else {
            this.endDate = null;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
