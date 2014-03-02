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

package org.sofun.platform.web.rest.api.prediction.get;

import java.io.Serializable;

import org.sofun.core.api.prediction.Prediction;
import org.sofun.platform.web.rest.api.kup.ReSTKup;
import org.sofun.platform.web.rest.api.team.ReSTTeam;

/**
 * Prediction ReST API
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTPrediction implements Serializable {

    private static final long serialVersionUID = 5982159505305395398L;

    protected long id;

    protected ReSTTeam team;

    protected ReSTKup kup;

    protected int points = 0;

    protected String ranking;

    protected double winning = 0;

    public ReSTPrediction(Prediction corePrediction) {
        if (corePrediction != null) {
            setId(corePrediction.getId());
            setTeam(new ReSTTeam(corePrediction.getKup().getTeam(), false));
            setKup(new ReSTKup(corePrediction.getKup()));
            setPoints(corePrediction.getPoints());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ReSTTeam getTeam() {
        return team;
    }

    public void setTeam(ReSTTeam team) {
        this.team = team;
    }

    public ReSTKup getKup() {
        return kup;
    }

    public void setKup(ReSTKup kup) {
        this.kup = kup;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public double getWinning() {
        return winning;
    }

    public void setWinning(double winning) {
        this.winning = winning;
    }

}
