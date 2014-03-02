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

package org.sofun.platform.web.rest.api.team;

import java.io.Serializable;

import org.sofun.core.api.team.TeamTag;

/**
 * Team Tag
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTTeamTag implements Serializable {

    private static final long serialVersionUID = 2581553798940706531L;

    protected String name;

    protected String msgId;
    
    protected int score;

    public ReSTTeamTag(TeamTag coreTag) {
        if (coreTag != null) {
            this.name = coreTag.getName();
            this.msgId = coreTag.getMsgid();
            this.score = coreTag.getScore();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
