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
 * ReST API for Kups.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Local
@Path("/kup")
public interface KupResource extends Serializable {

    @POST
    @Path("/search")
    @Produces("application/json")
    Response searchKups(Map<String, String> params) throws ReSTException;

    @GET
    @Path("/{kupId}/member/{email}/bet/place")
    @Produces("application/json")
    Response placeKupBet(@PathParam("kupId") long kupId,
            @PathParam("email") String email) throws ReSTException;

    @GET
    @Path("/{kupId}/member/{email}/credentials")
    @Produces("application/json")
    Response getKupCredentials(@PathParam("kupId") long kupId,
            @PathParam("email") String email);

    @GET
    @Path("/{kupId}/member/{email}/hasPredictions")
    @Produces("application/json")
    Response hasPredictions(@PathParam("kupId") long kupId,
            @PathParam("email") String email) throws ReSTException;

    @GET
    @Path("/{kupId}/member/{email}/hasBet")
    @Produces("application/json")
    Response hasBet(@PathParam("kupId") long kupId,
            @PathParam("email") String email) throws ReSTException;

    @GET
    @Path("/{kupId}/get")
    @Produces("application/json")
    Response getKup(@PathParam("kupId") long kupId) throws ReSTException;

    @GET
    @Path("/{kupId}/seasons/get")
    @Produces("application/json")
    Response getKupSeasons(@PathParam("kupId") long kupId) throws ReSTException;

    @GET
    @Path("/{kupId}/stages/get")
    @Produces("application/json")
    Response getKupStages(@PathParam("kupId") long kupId) throws ReSTException;

    @GET
    @Path("/{kupId}/rounds/get")
    @Produces("application/json")
    Response getKupRounds(@PathParam("kupId") long kupId) throws ReSTException;

    @GET
    @Path("/{kupId}/games/get")
    @Produces("application/json")
    Response getKupGames(@PathParam("kupId") long kupId) throws ReSTException;

    @GET
    @Path("/{kupId}/games/status/{gameStatus}/get")
    @Produces("application/json")
    Response getKupGamesByStatus(@PathParam("kupId") long kupId,
            @PathParam("gameStatus") String gameStatus) throws ReSTException;

    @GET
    @Path("/{kupId}/questions/get")
    @Produces("application/json")
    Response getKupQuestions(@PathParam("kupId") long kupId)
            throws ReSTException;

    @GET
    @Path("/{kupId}/questions/tiebreaker/get")
    @Produces("application/json")
    Response getKupQuestionsTiebreaker(@PathParam("kupId") long kupId)
            throws ReSTException;

    @POST
    @Path("/{kupId}/member/{email}/predictions/add")
    @Produces("application/json")
    Response addKupPredictions(@PathParam("kupId") long kupId,
            @PathParam("email") String email, Map<String, String> params)
            throws ReSTException;

    @GET
    @Path("/{kupId}/member/{email}/predictions/{type}/get")
    @Produces("application/json")
    Response getKupPredictions(@PathParam("kupId") long kupId,
            @PathParam("email") String email, @PathParam("type") String type)
            throws ReSTException;

    @GET
    @Path("/{kupId}/member/{email}/predictions/lastmodified/get")
    @Produces("application/json")
    Response getKupPredictionsLastModifiedFor(@PathParam("kupId") long kupId,
            @PathParam("email") String email) throws ReSTException;

    @GET
    @Path("/{kupId}/ranking/{offset}/{batchSize}/get")
    @Produces("application/json")
    Response getKupRanking(@PathParam("kupId") long kupId,
            @PathParam("offset") int offset,
            @PathParam("batchSize") int batchSize) throws ReSTException;

    @GET
    @Path("/{kupId}/ranking/member/{email}/{offset}/{batchSize}/get")
    @Produces("application/json")
    Response getKupRankingFor(@PathParam("kupId") long kupId,
            @PathParam("email") String email, @PathParam("offset") int offset,
            @PathParam("batchSize") int batchSize) throws ReSTException;

    @GET
    @Path("/{kupId}/ranking/facebook/member/{email}/{offset}/{batchSize}/get")
    @Produces("application/json")
    Response getKupRankingFacebookFor(@PathParam("kupId") long kupId,
            @PathParam("email") String email, @PathParam("offset") int offset,
            @PathParam("batchSize") int batchSize) throws ReSTException;

    @POST
    @Path("/{kupId}/invite")
    @Produces("application/json")
    Response invite(@PathParam("kupId") long kupId, Map<String, String> params)
            throws ReSTException;

}
