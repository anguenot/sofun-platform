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

package org.sofun.platform.web.rest.api.kup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupSearchResults;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class RestKupSearchResults implements Serializable {

    private static final long serialVersionUID = -3418652670464467040L;

    private int offset;

    private int batchSize;

    private long totalResults;

    private List<ReSTKup> results = new ArrayList<ReSTKup>();

    public RestKupSearchResults() {
    }

    public RestKupSearchResults(KupSearchResults coreResults) {
        if (coreResults != null) {
            setOffset(coreResults.getOffset());
            setBatchSize(coreResults.getBatchSize());
            setTotalResults(coreResults.getTotalResults());
            for (Kup coreKup : coreResults.getResults()) {
                results.add(new ReSTKup(coreKup));
            }
        }

    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public List<ReSTKup> getResults() {
        return results;
    }

    public void setResults(List<ReSTKup> results) {
        this.results = results;
    }

}
