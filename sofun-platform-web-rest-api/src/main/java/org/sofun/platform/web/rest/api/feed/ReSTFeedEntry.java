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
import java.util.Date;

import org.sofun.core.api.feed.FeedEntry;
import org.sofun.platform.web.rest.api.member.ReSTMember;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTFeedEntry implements Serializable {

    private static final long serialVersionUID = 9124568343281201271L;

    protected ReSTMember member;

    protected Date date;

    protected String content;

    public ReSTFeedEntry() {
        super();
    }

    public ReSTFeedEntry(FeedEntry coreEntry) {
        super();
        if (coreEntry != null) {
            setMember(new ReSTMember(coreEntry.getMember()));
            setDate(coreEntry.getDate());
            setContent(coreEntry.getContent());
        }
    }

    public ReSTMember getMember() {
        return member;
    }

    public void setMember(ReSTMember member) {
        this.member = member;
    }

    public Date getDate() {
        if (date != null) {
            return (Date) date.clone();
        }
        return null;
    }

    public void setDate(Date date) {
        if (date != null) {
            this.date = (Date) date.clone();
        } else {
            this.date = null;
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
