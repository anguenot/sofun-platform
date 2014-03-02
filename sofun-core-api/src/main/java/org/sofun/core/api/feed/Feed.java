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

package org.sofun.core.api.feed;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface Feed extends Serializable {

    long getId();

    String getType();

    void setType(String type);

    Iterator<FeedEntry> getFeedEntries();

    List<FeedEntry> getBatchedFeedEntries(int offset, int size);

    void addFeedEntry(FeedEntry entry);

    void delFeedEntry(FeedEntry entry);

}
