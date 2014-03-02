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

package org.sofun.platform.opta.ftp;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.sofun.platform.opta.api.OptaProcessedFeed;
import org.sofun.platform.opta.api.OptaService;

/**
 * FTP File Filter Implementation.
 * 
 * <p>
 * 
 * Takes into account feed processing information specifics (in case feeds have
 * been already processed) in addition to a filename re pattern.
 * 
 * <p>
 * 
 * Depends on an {@link OptaService} instance if one wants to leverage the feed
 * processing time stamp. It can be used without any dependencies w/ re pattern
 * only.
 * 
 * <p>
 * 
 * It assumes the FTP server has time stamp capability.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class FTPFileFilterImpl implements FTPFileFilter {

    /* Optional dependency (see java doc above) */
    private OptaService service;

    private String filePattern;

    public FTPFileFilterImpl() {
        service = null;
    }

    public FTPFileFilterImpl(String pattern) {
        this();
        this.filePattern = pattern;
    }

    public FTPFileFilterImpl(String pattern, OptaService service) {
        this(pattern);
        this.service = service;
    }

    @Override
    public boolean accept(FTPFile file) {
        if (file == null || filePattern == null) {
            return false;
        }
        final Pattern p = Pattern.compile(filePattern);
        final Matcher m = p.matcher(file.getName());
        if (!m.find()) {
            // Filename does not match pattern. Do not include.
            return false;
        }
        if (service == null) {
            // No service available to filter further. Filename matches pattern:
            // do include.
            return true;
        }
        OptaProcessedFeed pfeed = service.getProcessedFeedFor(file.getName());
        if (pfeed == null) {
            // File has not been processed yet: do include
            return true;
        } else {
            Date formerTimestamp = pfeed.getTimestamp();
            if (formerTimestamp == null) {
                // No record of last processing: do include.
                return true;
            }
            if (formerTimestamp.compareTo(file.getTimestamp().getTime()) < 0) {
                // Former time stamp earlier than new one: do include
                return true;
            }
        }
        return false;
    }

}
