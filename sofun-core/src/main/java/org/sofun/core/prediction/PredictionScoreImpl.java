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

package org.sofun.core.prediction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.sofun.core.api.prediction.PredictionScore;

/**
 * Simple prediction score implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "predictions_scores")
@Inheritance(strategy = InheritanceType.JOINED)
public class PredictionScoreImpl extends PredictionImpl implements PredictionScore {

    private static final long serialVersionUID = 3752635930954240285L;

    @Column(name = "score_team_1", columnDefinition = "int default 0")
    protected int scoreTeam1;

    @Column(name = "score_team_2", columnDefinition = "int default 0")
    protected int scoreTeam2;

    public PredictionScoreImpl() {
        super();
    }

    @Override
    public int getScoreTeam1() {
        return scoreTeam1;
    }

    @Override
    public int getScoreTeam2() {
        return scoreTeam2;
    }

    @Override
    public void setScoreTeam1(int score) {
        this.scoreTeam1 = score;
    }

    @Override
    public void setScoreTeam2(int score) {
        this.scoreTeam2 = score;
    }

}
