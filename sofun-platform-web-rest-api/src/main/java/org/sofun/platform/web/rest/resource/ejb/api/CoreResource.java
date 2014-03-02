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

package org.sofun.platform.web.rest.resource.ejb.api;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.Local;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.sofun.platform.web.rest.api.ReSTCoreInfo;
import org.sofun.platform.web.rest.api.exception.ReSTException;

/**
 * Sofun Core Web Resources.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 */
@Local
@Path("/core")
public interface CoreResource extends Serializable {

    @GET
    @Path("/version")
    @Produces("text/html")
    Response getVersion() throws ReSTException;

    @GET
    @Path("/powered-by")
    @Produces("text/html")
    Response getPoweredBy() throws ReSTException;

    @GET
    @Path("/copyright")
    @Produces("text/html")
    Response getCopyright() throws ReSTException;

    @GET
    @Path("/copyright-url")
    @Produces("text/html")
    Response getCopyrightURL() throws ReSTException;

    @GET
    @Path("/email-contact")
    @Produces("text/html")
    Response getEmailContact() throws ReSTException;

    @GET
    @Path("/info")
    @Produces("application/json")
    ReSTCoreInfo getCoreInfo() throws ReSTException;

    @POST
    @Path("/echo")
    @Produces("application/json")
    Response echo(Map<String, String> map) throws ReSTException;

    /**
     * Used by SDK to check if wether or not the platform is available and working.
     * 
     * @return a {@link Response} instance.
     * @throws ReSTException
     */
    @GET
    @Path("/status")
    @Produces("application/json")
    Response getStatus() throws ReSTException;

}
