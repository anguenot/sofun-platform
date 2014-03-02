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

package org.sofun.core.api.team;

import java.io.Serializable;

/**
 * Team Tag
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface TeamTag extends Comparable<TeamTag>, Serializable {

    /**
     * Returns the tag name.
     * 
     * @return a unique name.
     */
    String getName();

    /**
     * Sets the tag name.
     * 
     * @param name: a unique name
     */
    void setName(String name);

    /**
     * Returns the tag msgid for i18n
     * 
     * @return a unique msgid
     */
    String getMsgid();

    /**
     * Sets the tag msgid for i18n
     * 
     * @param msgid: a unique msgid
     */
    void setMsgid(String msgid);

    /**
     * Returns the tag score.
     * 
     * <p/>
     * 
     * The score is incremented each time a new room adds this tag. The score is decremented each time a new room dels this tag.
     * 
     * @return the tag score value. Default is 0
     */
    int getScore();

    /**
     * Sets the tag score.
     * 
     * <p/>
     * 
     * The score is incremented each time a new room adds this tag. The score is decremented each time a new room dels this tag.
     * 
     * @param score: the tag score value. Default is 0
     */
    void setScore(int score);

}
