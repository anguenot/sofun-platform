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
import java.util.Collection;

import javax.ejb.Local;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.sofun.platform.web.rest.api.exception.ReSTException;
import org.sofun.platform.web.rest.api.member.ReSTMember;
import org.sofun.platform.web.rest.api.team.ReSTMemberRankingTable;

/**
 * Community Web Resources.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 */
@Local
@Path("/community")
public interface CommunityResource extends Serializable {
    
    @GET
    @Path("/{communityId}/ranking/{offset}/{batchSize}/get")
    @Produces("application/json")
    Response getCommunityRankingTable(@PathParam("communityId") long communityId, @PathParam("offset") int offset, @PathParam("batchSize") int batchSize)
            throws ReSTException;
    
    
    @GET
    @Path("/{communityId}/ranking/member/{memberEmail}/{offset}/{batchSize}")
    @Produces("application/json")
    Response getMemberCommunityRankingTable(@PathParam("communityId") long communityId,
            @PathParam("memberEmail") String memberEmail, @PathParam("offset") int offset, @PathParam("batchSize") int batchSize)
            throws ReSTException;

    @GET
    @Path("/{communityId}/ranking/friends/{memberEmail}/{offset}/{batchSize}")
    @Produces("application/json")
    ReSTMemberRankingTable getFriendsCommunityRankingTable(@PathParam("communityId") long communityId,
            @PathParam("memberEmail") String memberEmail, @PathParam("offset") int offset, @PathParam("batchSize") int bachSize)
            throws ReSTException;

    @GET
    @Path("/{communityId}/members")
    @Produces("application/json")
    Collection<ReSTMember> getMembers(@PathParam("communityId") long communityId) throws ReSTException;

    @GET
    @Path("/{communityId}/member/{email}")
    @Produces("application/json")
    ReSTMember getMember(@PathParam("communityId") long communityId, @PathParam("email") String email) throws ReSTException;

    @GET
    @Path("/{communityId}/member/{email}/friends")
    @Produces("application/json")
    Response getMemberFriends(@PathParam("communityId") long communityId, @PathParam("email") String email)
            throws ReSTException;

    @GET
    @Path("/{communityId}/member/uuid/{uuid}")
    @Produces("application/json")
    ReSTMember getMemberById(@PathParam("communityId") long communityId, @PathParam("uuid") long uuid) throws ReSTException;

    @GET
    @Path("/{communityId}/members/del/{email}")
    @Produces("application/json")
    Response delMember(@PathParam("communityId") long communityId, @PathParam("email") String email) throws ReSTException;

    @GET
    @Path("/{communityId}/{memberEmail}/ranking-stats")
    @Produces("application/json")
    Response getRankingInfoForMember(@PathParam("communityId") long communityId, @PathParam("memberEmail") String memberEmail)
            throws ReSTException;

    @GET
    @Path("/{communityId}/feed/{offset}/{size}/get")
    @Produces("application/json")
    Response getCommunityFeed(@PathParam("communityId") long communityId, @PathParam("offset") int offset,
            @PathParam("size") int size) throws ReSTException;
    
    @GET
    @Path("/community/{communityId}/teams/{memberEmail}/{offset}/{batchSize}")
    @Produces("application/json")
    Response getBatchedTeamsFor(@PathParam("communityId") long communityId, @PathParam("memberEmail") String memberEmail,
            @PathParam("offset") int offset, @PathParam("batchSize") int batchSize) throws ReSTException;


}
