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
import org.sofun.platform.web.rest.api.member.post.PasswordLogin;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Local
@Path("/member")
public interface MemberResource extends Serializable {

    @POST
    @Path("/{email}/account/close")
    @Produces("application/json")
    Response closeRequest(@PathParam("email") String email)
            throws ReSTException;

    @POST
    @Path("/credit")
    @Produces("application/json")
    Response creditMember(Map<String, String> params) throws ReSTException;

    @POST
    @Path("/debit")
    @Produces("application/json")
    Response debitMember(Map<String, String> params) throws ReSTException;

    @POST
    @Path("/edit")
    @Produces("application/json")
    Response edit(Map<String, String> params) throws Exception;

    @GET
    @Path("/email/exists/{email}")
    @Produces("application/json")
    Response existsEmail(@PathParam("email") String email) throws ReSTException;

    @GET
    @Path("/facebook/exists/{facebookId}")
    @Produces("application/json")
    Response existsFacebookId(@PathParam("facebookId") String facebookId)
            throws ReSTException;

    @GET
    @Path("/pseudo/exists/{pseudo}")
    @Produces("application/json")
    Response existsPseudo(@PathParam("pseudo") String pseudo)
            throws ReSTException;

    @GET
    @Path("/{email}/credit/bonus/history")
    @Produces("application/json")
    Response getMemberBonusCreditHistory(@PathParam("email") String email)
            throws ReSTException;

    @GET
    @Path("/{email}/credit/history")
    @Produces("application/json")
    Response getMemberCreditHistory(@PathParam("email") String email)
            throws ReSTException;

    @GET
    @Path("/{email}/debit/history")
    @Produces("application/json")
    Response getMemberDebitHistory(@PathParam("email") String email)
            throws ReSTException;

    @GET
    @Path("/{email}/debit/bet/history")
    @Produces("application/json")
    Response getMemberDebitBetHistory(@PathParam("email") String email)
            throws ReSTException;

    @GET
    @Path("/{email}/debit/bet/amount")
    @Produces("application/json")
    Response getMemberDebitCurrentBetAmount(@PathParam("email") String email)
            throws ReSTException;

    @GET
    @Path("/{email}/credit/bet/history")
    @Produces("application/json")
    Response getMemberCreditBetHistory(@PathParam("email") String email)
            throws ReSTException;

    @GET
    @Path("/{email}/transaction/bonus/unack")
    @Produces("application/json")
    Response getMemberUnAckBonusTransactions(@PathParam("email") String email)
            throws ReSTException;

    @GET
    @Path("/{email}/transaction/winnings/unack")
    @Produces("application/json")
    Response getMemberUnAckWinningsTransactions(@PathParam("email") String email)
            throws ReSTException;

    @POST
    @Path("/transactions/ack")
    @Produces("application/json")
    Response ackTransaction(Map<String, String> params) throws Exception;
    
    @GET
    @Path("/{email}/properties")
    @Produces("application/json")
    Response getMemberProperties(@PathParam("email") String email)
            throws ReSTException;

    @GET
    @Path("/{email}/transaction/history")
    @Produces("application/json")
    Response getMemberTransactionHistory(@PathParam("email") String email)
            throws ReSTException;

    @GET
    @Path("/{email}/wire/history")
    @Produces("application/json")
    Response getMemberWireHistory(@PathParam("email") String email)
            throws ReSTException;

    @POST
    @Path("/login")
    @Produces("application/json")
    Response login(PasswordLogin login) throws ReSTException;

    @POST
    @Path("/login/facebook")
    @Produces("application/json")
    Response loginFacebook(Map<String, String> params) throws ReSTException;

    @POST
    @Path("/{email}/logout")
    @Produces("application/json")
    Response logout(@PathParam("email") String email) throws ReSTException;

    @POST
    @Path("/facebook/{facebookId}/logout")
    @Produces("application/json")
    Response logoutFacebook(@PathParam("facebookId") String facebookId)
            throws ReSTException;

    @POST
    @Path("/{email}/password/reset")
    @Produces("application/json")
    Response passwordReset(@PathParam("email") String email,
            Map<String, String> params) throws ReSTException;

    @POST
    @Path("/{email}/password/forgotten/")
    @Produces("application/json")
    Response passwordForgotten(@PathParam("email") String email,
            Map<String, String> params) throws ReSTException;

    @GET
    @Path("/{email}/password/{hash}/verify")
    @Produces("application/json")
    Response passwordVerify(@PathParam("email") String email,
            @PathParam("hash") String hash) throws ReSTException;

    @POST
    @Path("/register")
    @Produces("application/json")
    Response register(Map<String, String> params) throws ReSTException;

    @POST
    @Path("/register/facebook")
    @Produces("application/json")
    Response registerFacebook(Map<String, String> params) throws ReSTException;

    @POST
    @Path("/link/facebook")
    @Produces("application/json")
    Response linkFacebook(Map<String, String> params) throws ReSTException;

    @POST
    @Path("/unlink/facebook")
    @Produces("application/json")
    Response unlinkFacebook(Map<String, String> params) throws ReSTException;

    @GET
    @Path("/{email}/notifications")
    @Produces("application/json")
    Response getMemberNotificationsScheme(@PathParam("email") String email)
            throws ReSTException;

    @POST
    @Path("/{email}/notifications/edit")
    @Produces("application/json")
    Response setMemberNotificationsScheme(@PathParam("email") String email,
            Map<String, Boolean> scheme) throws ReSTException;

    @GET
    @Path("/{email}/predictions/history/type/{type}/{offset}/{batch}")
    @Produces("application/json")
    Response getMemberPredictionsHistory(@PathParam("email") String email,
            @PathParam("type") String type, @PathParam("offset") int offset,
            @PathParam("batch") int batch) throws ReSTException;

    @GET
    @Path("/{email}/kups/history/status/{status}/{offset}/{batch}")
    @Produces("application/json")
    Response getMemberKupsParticipationHistory(
            @PathParam("email") String email,
            @PathParam("status") String status,
            @PathParam("offset") int offset, @PathParam("batch") int batch)
            throws ReSTException;

}
