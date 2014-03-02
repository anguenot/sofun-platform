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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.platform.web.rest.api.sport.ReSTSport;

/**
 * Tournament Web API.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTTournament implements Serializable {

    private static final long serialVersionUID = -7651487708815211319L;

    protected long uuid;

    protected String name;

    protected Collection<ReSTSport> sports = new ArrayList<ReSTSport>();

    public ReSTTournament() {
        super();
    }

    public ReSTTournament(Tournament coreTournament) {

        this();

        setName(coreTournament.getName());
        setUuid(coreTournament.getUUID());

        if (coreTournament.getSports() != null) {
            for (Sport s : coreTournament.getSports()) {
                sports.add(new ReSTSport(s));
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

    public Collection<ReSTSport> getSports() {
        return sports;
    }

    public void setSports(List<ReSTSport> sports) {
        this.sports = sports;
    }

}
