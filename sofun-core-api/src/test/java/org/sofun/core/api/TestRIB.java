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

package org.sofun.core.api;

import junit.framework.TestCase;

import org.junit.Test;
import org.sofun.core.api.banking.RIB;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestRIB extends TestCase {

    @Test
    public void testRIB2IBAN() {

        final String bank = "10278";

        final String branch = "07570";

        final String number = "00020241640";

        final String key = "92";

        assertEquals("FR7610278075700002024164092",
                RIB.toIBAN(bank, branch, number, key));

    }

}
