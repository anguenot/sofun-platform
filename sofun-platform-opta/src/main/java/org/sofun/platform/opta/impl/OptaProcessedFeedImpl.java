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

package org.sofun.platform.opta.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.sofun.platform.opta.api.OptaProcessedFeed;

/**
 * Opta Processed Feed Implementation.
 * 
 * <p>
 * 
 * Hold feed processing information like time stamp, last modified and filename.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "opta_ftp_processed_files")
public class OptaProcessedFeedImpl implements OptaProcessedFeed {

    private static final long serialVersionUID = -6080412973994659345L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "filename")
    protected String fileName;

    @Column(name = "modified")
    protected Date lastModified;

    @Column(name = "feed_timestamp")
    protected Date feedTimestamp;

    public OptaProcessedFeedImpl() {
        super();
    }

    public OptaProcessedFeedImpl(String filename, Date feedTimestamp) {
        this();
        this.fileName = filename;
        setTimestamp(feedTimestamp);
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public Date getLastUpdated() {
        if (lastModified != null) {
            return (Date) lastModified.clone();
        }
        return null;
    }

    @PrePersist
    @PreUpdate
    protected void setDate() {
        this.lastModified = new Date();
    }

    @Override
    public Date getTimestamp() {
        if (feedTimestamp != null) {
            return (Date) feedTimestamp.clone();
        }
        return null;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        if (timestamp != null) {
            feedTimestamp = (Date) timestamp.clone();
        } else {
            feedTimestamp = null;
        }
    }

    @Override
    public void setLastUpdated(Date lastUpdated) {
        if (lastUpdated != null) {
            lastModified = (Date) lastUpdated.clone();
        }
        // No need to allow reseting this.
    }

}
