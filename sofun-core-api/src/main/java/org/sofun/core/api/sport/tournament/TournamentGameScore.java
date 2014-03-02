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

package org.sofun.core.api.sport.tournament;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Base tournament game score interface.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface TournamentGameScore extends Serializable {
    
    long getId();
    
    void setId(long id);

    int getScoreTeam1();

    void setScoreTeam1(int score);

    int getScoreTeam2();

    void setScoreTeam2(int score);

    Date getLastUpdated();

    void setLastUpdated(Date lastUpdated);

    List<Integer> getScore();

}
