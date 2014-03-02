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

import org.sofun.core.api.sport.tournament.TournamentSeason;

/**
 * Tournament Season Web API.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTTournamentSeason implements Serializable {

    private static final long serialVersionUID = -5492730044475363219L;

    protected long uuid;

    protected String name;

    protected String yearLabel;

    protected Date startDate;

    protected Date endDate;

    public ReSTTournamentSeason() {
        super();
    }

    public ReSTTournamentSeason(TournamentSeason coreSeason) {

        this();

        this.setUuid(coreSeason.getUUID());
        this.setName(coreSeason.getName());
        this.setYearLabel(coreSeason.getYearLabel());
        this.setStartDate(coreSeason.getStartDate());
        this.setEndDate(coreSeason.getEndDate());

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

    public String getYearLabel() {
        return yearLabel;
    }

    public void setYearLabel(String yearLabel) {
        this.yearLabel = yearLabel;
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

}
