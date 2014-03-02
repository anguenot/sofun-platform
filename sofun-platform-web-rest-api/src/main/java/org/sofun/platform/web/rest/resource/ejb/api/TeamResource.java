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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.sofun.platform.web.rest.api.exception.ReSTException;

/**
 * Teams (aka "rooms" Web resources)
 * 
 * <p/>
 * 
 * Define ReST Web API producing JSON
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 */
@Local
@Path("/team")
public interface TeamResource extends Serializable {

    @POST
    @Path("/add")
    @Produces("application/json")
    Response addTeam(Map<String, String> params) throws ReSTException;

    @GET
    @Path("/{teamId}/get")
    @Produces("application/json")
    Response getTeam(@PathParam("teamId") long teamId) throws ReSTException;

    @GET
    @Path("/name/{teamName}/get")
    @Produces("application/json")
    Response getTeamByName(@PathParam("teamName") String name)
            throws ReSTException;

    @POST
    @Path("/edit")
    @Produces("application/json")
    Response editTeam(Map<String, String> params) throws ReSTException;

    @POST
    @Path("/del")
    @Produces("application/json")
    Response delTeam(Map<String, String> params) throws ReSTException;

    @POST
    @Path("/{teamId}/member/add")
    @Produces("application/json")
    Response addTeamMember(@PathParam("teamId") long teamId,
            Map<String, String> params) throws ReSTException;

    @POST
    @Path("/{teamId}/member/del")
    @Produces("application/json")
    Response delTeamMember(@PathParam("teamId") long teamId,
            Map<String, String> params) throws ReSTException;

    @GET
    @Path("/tags/{limit}/get")
    @Produces("application/json")
    Response getTeamTags(@PathParam("limit") int limit);

    @GET
    @Path("/types/get")
    @Produces("application/json")
    Response getTeamTypes();

    @GET
    @Path("/privacy/get")
    @Produces("application/json")
    Response getTeamPrivacyTypes();

    @GET
    @Path("/exists/name/{teamName}")
    @Produces("application/json")
    Response existsTeam(@PathParam("teamName") String teamName)
            throws ReSTException;

    @POST
    @Path("/search")
    @Produces("application/json")
    Response searchTeams(Map<String, String> params) throws ReSTException;

    @POST
    @Path("/search/count/get")
    @Produces("application/json")
    Response countSearchTeams(Map<String, String> params) throws ReSTException;

    @GET
    @Path("/{teamId}/ranking/{offset}/{batchSize}/get")
    @Produces("application/json")
    Response getTeamRankingTable(@PathParam("teamId") long teamId,
            @PathParam("offset") int offset,
            @PathParam("batchSize") int batchSize) throws ReSTException;

    @GET
    @Path("/{teamId}/ranking/member/{email}/{offset}/{batchSize}/get")
    @Produces("application/json")
    Response getTeamRankingTableFor(@PathParam("teamId") long teamId,
            @PathParam("email") String email, @PathParam("offset") int offset,
            @PathParam("batchSize") int batchSize) throws ReSTException;

    @GET
    @Path("/{teamId}/ranking/facebook/member/{email}/{offset}/{batchSize}/get")
    @Produces("application/json")
    Response getTeamRankingTableFacebookFor(@PathParam("teamId") long teamId,
            @PathParam("email") String email, @PathParam("offset") int offset,
            @PathParam("batchSize") int batchSize) throws ReSTException;

    @GET
    @Path("/{teamId}/feed/{offset}/{size}/get")
    @Produces("application/json")
    Response getTeamFeed(@PathParam("teamId") long teamId,
            @PathParam("offset") int offset, @PathParam("size") int size)
            throws ReSTException;

    @GET
    @Path("/{teamId}/member/{email}/credentials")
    @Produces("application/json")
    Response getTeamCredentialsFor(@PathParam("teamId") long teamId,
            @PathParam("email") String email);

    @GET
    @Path("/{teamId}/member/{email}/security")
    @Produces("application/json")
    Response getTeamCredentialsAndPrivacyFor(@PathParam("teamId") long teamId,
            @PathParam("email") String email);

    @POST
    @Path("/{teamId}/kup/add")
    @Produces("application/json")
    Response addTeamKup(@PathParam("teamId") long teamId,
            Map<String, String> params) throws ReSTException;

    @POST
    @Path("/{teamId}/kup/del")
    @Produces("application/json")
    Response delTeamKup(@PathParam("teamId") long teamId,
            Map<String, String> params) throws ReSTException;

    @GET
    @Path("/{teamId}/kups/{offset}/{size}/get")
    @Produces("application/json")
    Response getTeamKups(@PathParam("teamId") long teamId,
            @PathParam("offset") int offset, @PathParam("size") int size)
            throws ReSTException;

    @POST
    @Path("/{teamId}/invite")
    @Produces("application/json")
    Response invite(@PathParam("teamId") long teamId, Map<String, String> params)
            throws ReSTException;

}
