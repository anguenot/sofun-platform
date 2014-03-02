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

package org.sofun.core.security.oauth;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuthProblemException;

import org.jboss.resteasy.auth.oauth.OAuthException;
import org.jboss.resteasy.auth.oauth.OAuthFilter;
import org.jboss.resteasy.auth.oauth.OAuthUtils;
import org.jboss.resteasy.auth.oauth.OAuthValidator;
import org.jboss.resteasy.logging.Logger;
import org.sofun.core.api.OAuthService;
import org.sofun.core.security.oauth.exception.AccessTokenNotFoundException;
import org.sofun.core.security.oauth.exception.ConsumerNotFoundException;

/**
 * OAuth Servlet Filter supporting OAuth Authentication.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class OAuthSofunFilter extends OAuthFilter {

    private final static Logger log = Logger.getLogger(OAuthSofunFilter.class);

    protected OAuthService service;

    protected OAuthValidator validator;

    /**
     * Can't use parent one since all members are private and we need the
     * validator below.
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        validator = OAuthUtils.getValidator(context, getProvider());
    }

    /**
     * Returns the OAuth token storage service.
     * 
     * @return a {@link OAuthJPAStorageImpl} instance.
     * @throws OAuthException
     */
    protected OAuthService getService() throws OAuthException {
        if (service == null) {
            InitialContext ctx;
            try {
                ctx = new InitialContext();
                return (OAuthService) ctx
                        .lookup("sofun/OAuthServiceImpl/local");
            } catch (NamingException e) {
                throw new OAuthException(500, e.getMessage());
            }
        }
        return service;
    }

    @Override
    protected void _doFilter(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        log.debug("Filtering " + request.getMethod() + " "
                + request.getRequestURL().toString());
        try {
            request = getService().filterRequest(request, getProvider(),
                    validator);
            filterChain.doFilter(request, response);
        } catch (OAuthException x) {
            OAuthUtils.makeErrorResponse(response, x.getMessage(),
                    x.getHttpCode(), getProvider());
        } catch (OAuthProblemException x) {
            OAuthUtils.makeErrorResponse(response, x.getProblem(),
                    OAuthUtils.getHttpCode(x), getProvider());
        } catch (ConsumerNotFoundException x) {
            OAuthUtils.makeErrorResponse(response, "Consumer not found", 401,
                    getProvider());
        } catch (AccessTokenNotFoundException x) {
            OAuthUtils.makeErrorResponse(response, "Token not found", 401,
                    getProvider());
        } catch (Exception x) {
            // x.printStackTrace();
            OAuthUtils.makeErrorResponse(response, x.getMessage(),
                    HttpURLConnection.HTTP_INTERNAL_ERROR, getProvider());
        }
    }

}
