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

package org.sofun.core;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.sofun.core.api.Core;
import org.sofun.core.api.local.CoreLocal;
import org.sofun.core.api.remote.CoreRemote;

/**
 * Sofun Core Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(CoreLocal.class)
@Remote(CoreRemote.class)
public class CoreImpl implements Core {

    private static final long serialVersionUID = -9104747359212708790L;

    public static final String RemoteJNDIName = CoreImpl.class.getSimpleName()
            + "/remote";

    public static final String LocalJNDIName = CoreImpl.class.getSimpleName()
            + "/local";

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    public CoreImpl() {
        super();
    }

    public CoreImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public String getVersion() {
        return CoreConstants.VERSION;
    }

    @Override
    public String getPoweredBy() {
        return CoreConstants.POWERED_BY;
    }

    @Override
    public String getCopyright() {
        return CoreConstants.COPYRIGHT;
    }

    @Override
    public URL getCopyrightURL() {
        try {
            return new URL(CoreConstants.COPYRIGHT_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getEmailContact() {
        return CoreConstants.SOFUN_EMAIL_CONTACT;
    }

    @Override
    public URL getServerURL() {
        try {
            return new URL(CoreConstants.API_PROTOCOL + CoreConstants.API_DOMAIN);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
