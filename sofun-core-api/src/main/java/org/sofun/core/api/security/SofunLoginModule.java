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

package org.sofun.core.api.security;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.logging.LogConfigurationException;

/**
 * Sofun Login Module.
 * 
 * <p/>
 * 
 * This ties into JAAS to perform the actual authentication against the database.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class SofunLoginModule implements LoginModule {

    protected Subject subject;

    protected CallbackHandler callbackHandler;

    protected Map<String, ?> sharedState;

    protected Map<String, ?> options;

    protected boolean commitSucceeded = false;

    protected boolean loginSucceeded = false;

    protected String username;

    protected SofunPrincipal user;

    protected SofunPrincipal[] roles;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
    }

    @Override
    public boolean abort() throws LoginException {
        return true;
    }

    @Override
    public boolean commit() throws LoginException {

        subject.getPrincipals().add(user);

        SofunGroup group = new SofunGroup("Roles"); // JBoss requires 'Roles'
        for (SofunPrincipal role : roles) {
            group.addMember(role);
        }
        subject.getPrincipals().add(group);

        return true;
    }

    @Override
    public boolean login() throws LoginException {

        NameCallback nameCallback = new NameCallback("Username");
        PasswordCallback passwordCallback = new PasswordCallback("Password", false);

        Callback[] callbacks = new Callback[] { nameCallback, passwordCallback };
        try {
            callbackHandler.handle(callbacks);
        } catch (IOException e) {
            throw new LogConfigurationException(e);
        } catch (UnsupportedCallbackException e) {
            throw new LogConfigurationException(e);
        }

        username = nameCallback.getName();

        /*
         * Member member; try { member = getMemberService().getMember(username); } catch (CoreException e) { throw new
         * LoginException(e.getMessage()); }
         * 
         * final char[] password = passwordCallback.getPassword(); passwordCallback.clearPassword();
         * 
         * 
         * if (member.getPassword() != null) { final char[] mPassword = member.getPassword().toCharArray(); if
         * (mPassword.equals(password)) { user = new SofunPrincipal(username); roles = new SofunPrincipal[] { new
         * SofunPrincipal(SofunRoles.MEMBER) }; loginSucceeded = true; } else { loginSucceeded = false; } } else {
         * loginSucceeded = false; }
         */

        // XXX: JAAS authentication. Application level responsibility for the moment
        // This is principal is not used. We rely on oauth token exchange as well application level credentials for now.
        user = new SofunPrincipal("admin@sofungaming.com");
        roles = new SofunPrincipal[] { new SofunPrincipal(SofunRoles.MEMBER) };
        loginSucceeded = true;

        return loginSucceeded;

    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().remove(user);
        user = null;
        return true;
    }

}
