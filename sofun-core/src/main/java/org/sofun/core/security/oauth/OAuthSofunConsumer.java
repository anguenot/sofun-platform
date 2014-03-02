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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.jboss.resteasy.auth.oauth.OAuthConsumer;

/**
 * OAuth Sofun consumer.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "oauth_consumers")
public class OAuthSofunConsumer extends OAuthConsumer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "key", nullable = false, unique = true)
    private String key;

    @Column(name = "secret", nullable = false)
    private String secret;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "connect_uri")
    private String connectURI;

    @ElementCollection
    @CollectionTable(
            name = "oauth_consumers_scopes",
            joinColumns = @JoinColumn(name = "id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "scope_id")
    private List<String> scopes = new ArrayList<String>();

    @ElementCollection
    @CollectionTable(
            name = "oauth_consumers_permissions",
            joinColumns = @JoinColumn(name = "id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_id")
    private List<String> permissions = new ArrayList<String>();

    public OAuthSofunConsumer() {
        super(null, null, null, null);
    }

    public OAuthSofunConsumer(String key, String secret, String displayName,
            String connectURI) {
        super(key, secret, displayName, connectURI);
        this.key = key;
        this.secret = secret;
        this.displayName = displayName;
        this.connectURI = connectURI;
    }

    /**
     * Returns the consumer key
     * 
     * @return a unique not null key for this consumer.
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * Sets the consumer key.
     * 
     * @param key: a unique not null key for this consumer.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the consumer secret.
     * 
     * @return: a {@link String} not null.
     */
    @Override
    public String getSecret() {
        return secret;
    }

    /**
     * Sets the consumer secret.
     * 
     * @param secret: a not null {@link String}
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Returns the consumer display name.
     * 
     * @return: a {@link String}. Can be null.
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the connect URI
     * 
     * @return: a {@link String}. Can be null.
     */
    @Override
    public String getConnectURI() {
        return connectURI;
    }

    /**
     * Returns the consumer scopes.
     * 
     * @return: an array of string or null.
     */
    @Override
    public String[] getScopes() {
        return scopes.toArray(new String[scopes.size()]);
    }

    /**
     * Sets the consumer scopes.
     * 
     * @param: an array of string or null.
     */
    @Override
    public void setScopes(String[] scopes) {
        this.scopes = Arrays.asList(scopes);
    }

    /**
     * Returns the consumer permissions.
     * 
     * @return: an array of string or null.
     */
    @Override
    public String[] getPermissions() {
        return permissions.toArray(new String[permissions.size()]);
    }

    /**
     * Sets the consumer display name.
     * 
     * @param displayName: a {@link String} or null.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Sets the connection URI
     * 
     * @param connectURI: a {@link String} or null.
     */
    public void setConnectURI(String connectURI) {
        this.connectURI = connectURI;
    }

    /**
     * Sets the consumer permissions.
     * 
     * @param permissions: an array of string or null.
     */
    public void setPermissions(String[] permissions) {
        this.permissions = Arrays.asList(permissions);
    }

}
