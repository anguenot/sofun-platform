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

package org.sofun.platform.legigame.api;

import java.io.Serializable;
import java.util.Date;

import org.sofun.core.api.member.Member;

/**
 * Member Legigame Status
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface MemberLegigameStatus extends Serializable {

    /**
     * Returns the {@link Member} having this status.
     * 
     * @return a {@link Member} instance
     */
    Member getMember();

    /**
     * Sets the {@link Member} having this statu
     * 
     * @param membe: a {@link Member} instance
     */
    void setMember(Member member);

    /**
     * Returns the {@link Member} status (legigame side)
     * 
     * @return a 2 digits status
     */
    String getDocumentStatus();

    /**
     * Sets the {@link Member} status (legigame side)
     * 
     * @param status a 2 digits status
     */
    void setDocumentStatus(String status);

    /**
     * Returns the date at which the account has been created in Legigame
     * 
     * @return a {@link Date} instance
     */
    Date getCreated();

    /**
     * Returns the date at which the account has been last updated Legigame
     * side.
     * 
     * @return a Raphael Lakafia{@link Date} instance
     */
    Date getUpdated();

    /**
     * Sets the date at which the account has been last updated Legigame side.
     * 
     * @param lastUpdated: a {@link Date} instance
     */
    void setLastUpdated(Date lastUpdated);

    /**
     * Returns the iteration number.
     * 
     * @return the iteration number (from 0 to n)
     */
    int getIteration();

    /**
     * Sets the iteration number
     * 
     * @param iteration: from 0 to n
     */
    void setIteration(int iteration);

}
