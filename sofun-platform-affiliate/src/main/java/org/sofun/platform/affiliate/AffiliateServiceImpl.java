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

package org.sofun.platform.affiliate;

import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.sofun.platform.affiliate.api.AffiliateService;
import org.sofun.platform.affiliate.api.ejb.AffiliateServiceLocal;
import org.sofun.platform.affiliate.api.ejb.AffiliateServiceRemote;

/**
 * Affiliate service implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(AffiliateServiceLocal.class)
@Remote(AffiliateServiceRemote.class)
public class AffiliateServiceImpl implements AffiliateService {

    private static final long serialVersionUID = 630170875149538568L;

    @Override
    public String generateTrackingCode() {
        return UUID.randomUUID().toString();
    }

}
