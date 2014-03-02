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

package org.sofun.platform.web.rest.api.feed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sofun.core.api.feed.Feed;
import org.sofun.core.api.feed.FeedEntry;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTFeed implements Serializable {

    private static final long serialVersionUID = -2412054591078828138L;

    protected String type;

    protected List<ReSTFeedEntry> feedEntries = new ArrayList<ReSTFeedEntry>();

    public ReSTFeed() {
        super();
    }

    public ReSTFeed(Feed coreFeed, int offset, int size) {
        this();
        if (coreFeed != null) {
            setType(coreFeed.getType());
            for (FeedEntry coreEntry : coreFeed.getBatchedFeedEntries(offset,
                    size)) {
                feedEntries.add(new ReSTFeedEntry(coreEntry));
            }
        }
    }

    public List<ReSTFeedEntry> getFeedEntries() {
        return feedEntries;
    }

    public void setFeedEntries(List<ReSTFeedEntry> feedEntries) {
        this.feedEntries = feedEntries;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
