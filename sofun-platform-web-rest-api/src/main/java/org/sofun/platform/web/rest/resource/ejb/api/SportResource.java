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

import javax.ejb.Local;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.sofun.platform.web.rest.api.exception.ReSTException;

/**
 * Sports Web APIs
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Local
@Path("/sport")
public interface SportResource extends Serializable {

    @GET
    @Path("/player/{playerId}/get")
    @Produces("application/json")
    Response getPlayer(@PathParam("playerId") String playerId)
            throws ReSTException;

    @GET
    @Path("/team/{teamId}/get")
    @Produces("application/json")
    Response getTeam(@PathParam("teamId") String teamId) throws ReSTException;

    @GET
    @Path("/team/{teamId}/players/get")
    @Produces("application/json")
    Response getTeamPlayers(@PathParam("teamId") String teamId)
            throws ReSTException;

    @GET
    @Path("/season/{seasonId}/teams/get")
    @Produces("application/json")
    Response getSeasonTeams(@PathParam("seasonId") long seasonId)
            throws ReSTException;

    @GET
    @Path("/season/{seasonId}/players/get")
    @Produces("application/json")
    Response getSeasonPlayers(@PathParam("seasonId") long seasonId)
            throws ReSTException;

    @GET
    @Path("/season/{seasonId}/round/{roundName}/results/{tableType}/get")
    @Produces("application/json")
    Response getRoundOrderedResultsFor(@PathParam("seasonId") long seasonId,
            @PathParam("roundName") String roundName,
            @PathParam("tableType") String tableType) throws ReSTException;

    @GET
    @Path("/season/{seasonId}/round/uuid/{roundUUID}/results/{tableType}/{offset}/{batchSize}/get")
    @Produces("application/json")
    Response getRoundOrderedResults(@PathParam("seasonId") long seasonId,
            @PathParam("roundUUID") String roundUUID,
            @PathParam("tableType") String tableType,
            @PathParam("offset") int offset,
            @PathParam("batchSize") int batchSize) throws ReSTException;

    @GET
    @Path("/season/{seasonId}/round/{roundName}/get")
    @Produces("application/json")
    Response getRound(@PathParam("seasonId") long seasonId,
            @PathParam("roundName") String roundName) throws ReSTException;

    @GET
    @Path("/game/{uuid}/get")
    @Produces("application/json")
    Response getGame(@PathParam("uuid") String uuid) throws ReSTException;

}
