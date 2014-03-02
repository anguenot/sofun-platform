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

package org.test.sofun.core.security.oauth;

import org.junit.Test;
import org.sofun.core.api.OAuthJPAStorage;
import org.sofun.core.security.oauth.OAuthSofunAccessToken;
import org.sofun.core.security.oauth.OAuthSofunConsumer;
import org.sofun.core.security.oauth.storage.OAUthJPAStorageImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestOAuthSofunToken extends SofunCoreTestCase {

    private OAuthJPAStorage storage;
    
    public TestOAuthSofunToken(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {    
        super.setUp();
        storage = new OAUthJPAStorageImpl(em);
    }

    @Test
    public void testTokenPersistence() {

        String appKey = "CONSUMER_KEY";
        String appSecret = "CONSUMER_SECRET";
        String displayName = "An application";
        String connectURI = "http://www.betkup.fr";

        OAuthSofunConsumer consumer = new OAuthSofunConsumer(appKey, appSecret, displayName, connectURI);

        em.persist(consumer);
        em.flush();
        em.refresh(consumer);

        String token = "abcd";
        String secret = "";
        String[] scopes = null;
        String[] permissions = new String[1];
        permissions[0] = "member";
        long timeToLive = 300;

        OAuthSofunAccessToken tokenInstance = new OAuthSofunAccessToken(token, secret, scopes, null, timeToLive, consumer);
        
        assertEquals(0, tokenInstance.getPermissions().length);
        tokenInstance.setPermissions(permissions);
        
        assertEquals(1, tokenInstance.getPermissions().length);

        em.persist(tokenInstance);
        em.flush();

        tokenInstance = storage.getAccessToken(token);
        assertEquals(1, tokenInstance.getPermissions().length);
        assertTrue(tokenInstance.getPermissions()[0].equals("member"));
        assertEquals(300, tokenInstance.getTimeToLive());
        
    }

}
