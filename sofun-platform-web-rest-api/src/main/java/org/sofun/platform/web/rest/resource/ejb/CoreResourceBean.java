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

package org.sofun.platform.web.rest.resource.ejb;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

import org.sofun.platform.web.rest.api.ReSTCoreInfo;
import org.sofun.platform.web.rest.api.exception.ReSTException;
import org.sofun.platform.web.rest.api.exception.ReSTRuntimeException;
import org.sofun.platform.web.rest.resource.ejb.api.CoreResource;

/**
 * Core ReST resource.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
public class CoreResourceBean extends AbstractResource implements CoreResource {

    private static final long serialVersionUID = 1767158472306351187L;

    public CoreResourceBean() {
    }

    @Override
    public Response getVersion() throws ReSTException {
        try {
            return Response.status(202).entity(getCore().getVersion()).build();
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
    }

    @Override
    public Response getPoweredBy() throws ReSTException {
        try {
            return Response.status(202).entity(getCore().getPoweredBy()).build();
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
    }

    @Override
    public Response getCopyright() throws ReSTException {
        try {
            return Response.status(202).entity(getCore().getCopyright()).build();
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
    }

    @Override
    public Response getCopyrightURL() throws ReSTException {
        try {
            return Response.status(202).entity(getCore().getCopyrightURL().getRef()).build();
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
    }

    @Override
    public Response getEmailContact() throws ReSTException {
        try {
            return Response.status(202).entity(getCore().getEmailContact()).build();
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
    }

    @Override
    public ReSTCoreInfo getCoreInfo() throws ReSTException {
        try {
            ReSTCoreInfo info = new ReSTCoreInfo();
            info.setCopyright(getCore().getCopyright());
            info.setCopyrightURL(getCore().getCopyrightURL().toString());
            info.setVersion(getCore().getVersion());
            info.setPoweredBy(getCore().getPoweredBy());
            info.setContactEmail(getCore().getEmailContact());
            return info;
        } catch (Exception e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
    }

    @Override
    public Response echo(Map<String, String> msg) throws ReSTException {
        return Response.status(202).entity(msg).build();

    }

    @Override
    public Response getStatus() throws ReSTException {
        return Response.status(202).entity(true).build();
    }
}
