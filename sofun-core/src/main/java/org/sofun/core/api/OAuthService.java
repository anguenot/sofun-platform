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

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.jboss.resteasy.auth.oauth.OAuthProvider;
import org.jboss.resteasy.auth.oauth.OAuthValidator;

/**
 * OAuth Sofun Sesrvice.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface OAuthService extends Serializable {

    /**
     * Verify tokens and destroy the ones that expired.
     * 
     * @throws Exception: if something bad happened.
     */
    void cleanAccessTokens() throws Exception;

    /**
     * Filter request.
     * 
     * <p/>
     * 
     * Invoked from the ServletFilter. We need to have these operations performed by an EJB so that can delegate the persistence
     * context to the container.
     * 
     * <p/>
     * 
     * This method does:
     * 
     * <ul>
     * <li>Create and validate OAuth tokens</li>
     * <li>Create a security context</li>
     * </ul>
     * 
     * <p/>
     * 
     * @param request: a HttpServletRequest object prior to any oauth ops.
     * @param provide: {@link OAuthProvider}. The servlet filter decides which one to apply.
     * @param validator: the {@link OAuthValidator}. The servlet filter decides which one to apply.
     * @return an HttpServletRequest object that will be forwarded in the filter chain by the servlet filter.
     * @throws Exception: if something bad happened.
     */
    HttpServletRequest filterRequest(HttpServletRequest request, OAuthProvider provider, OAuthValidator validator)
            throws Exception;

}
