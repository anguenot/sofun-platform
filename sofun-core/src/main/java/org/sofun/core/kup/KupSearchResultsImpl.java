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

package org.sofun.core.kup;

import java.util.List;

import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupSearchResults;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class KupSearchResultsImpl implements KupSearchResults {

    private static final long serialVersionUID = 4635196767772964744L;

    private final int offset;

    private final int batchSize;

    private final long totalResults;

    private final List<Kup> results;

    public KupSearchResultsImpl(int offset, int batchSize, long totalResults,
            List<Kup> results) {
        this.offset = offset;
        this.batchSize = batchSize;
        this.totalResults = totalResults;
        this.results = results;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public long getTotalResults() {
        return totalResults;
    }

    @Override
    public List<Kup> getResults() {
        return results;
    }

}
