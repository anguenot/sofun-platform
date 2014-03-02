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

package org.sofun.core.feed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.sofun.core.api.feed.Feed;
import org.sofun.core.api.feed.FeedEntry;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "feeds")
public class FeedImpl implements Feed {

    private static final long serialVersionUID = -8051717448523338194L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "type")
    protected String type;

    @OneToMany(
            targetEntity = FeedEntryImpl.class,
            cascade = CascadeType.ALL,
            mappedBy = "feed")
    @OrderBy("date DESC")
    protected List<FeedEntry> feedEntries = new LinkedList<FeedEntry>();

    public FeedImpl() {
        super();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Iterator<FeedEntry> getFeedEntries() {
        return feedEntries.listIterator();
    }

    @Override
    public List<FeedEntry> getBatchedFeedEntries(int offset, int size) {
        List<FeedEntry> batch = new ArrayList<FeedEntry>();

        ListIterator<FeedEntry> it;
        try {
            it = feedEntries.listIterator(offset);
        } catch (IndexOutOfBoundsException e) {
            return batch;
        }

        int i = 0;
        while (it.hasNext() && i < size) {
            batch.add(it.next());
            i += 1;
        }
        return batch;
    }

    @Override
    public void addFeedEntry(FeedEntry entry) {
        if (entry != null) {
            feedEntries.add(0, entry);
        }

    }

    @Override
    public void delFeedEntry(FeedEntry entry) {
        feedEntries.remove(entry);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

}
