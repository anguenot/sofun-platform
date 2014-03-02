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

package org.sofun.platform.opta.api;

import java.io.Serializable;
import java.util.Date;

/**
 * Opta Processed Feed.
 * 
 * <p/>
 * 
 * It keeps track of what Opta FTP feeds have been processed. Useful to
 * selectively decide if whether or not a feed has been processed or if a feed
 * needs re-processing in case if updates.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface OptaProcessedFeed extends Serializable {

    /**
     * Returns the feed's filename.
     * 
     * <p/>
     * 
     * Filename is root relative. Currently we are using a flat hierarchy.
     * 
     * @return the feed's filename.
     */
    String getFileName();

    /**
     * Returns the date at which the feed had been processed by the platform.
     * 
     * @return a {@link Date} instance
     */
    Date getLastUpdated();

    /**
     * Sets the date at which the feed had been processed by the platform.
     * 
     * @param lastUpdated: a {@link Date} instance
     */
    void setLastUpdated(Date lastUpdated);

    /**
     * Returns the feed time stamp as specified within the XML
     * 
     * @return a {@link Date} instance (GMT)
     */
    Date getTimestamp();

    /**
     * Set the feed time stamp as specified within the XML
     * 
     * @param timestamp: a {@link Date} instance
     */
    void setTimestamp(Date timestamp);

}
