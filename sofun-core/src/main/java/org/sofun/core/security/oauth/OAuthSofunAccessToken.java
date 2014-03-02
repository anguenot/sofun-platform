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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.jboss.resteasy.auth.oauth.OAuthConsumer;
import org.jboss.resteasy.auth.oauth.OAuthToken;
import org.sofun.core.api.member.Member;
import org.sofun.core.member.MemberImpl;

/**
 * Sofun OAuth token.
 * 
 * <p/>
 * 
 * Represents either a request or access token.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "oauth_tokens")
public class OAuthSofunAccessToken extends OAuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private String secret;

    @ElementCollection
    @CollectionTable(name = "oauth_tokens_scopes", joinColumns = @JoinColumn(
            name = "id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "scope_id")
    private List<String> scopes = new ArrayList<String>();

    @ElementCollection
    @CollectionTable(
            name = "oauth_tokens_permissions",
            joinColumns = @JoinColumn(name = "id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_id")
    private List<String> permissions = new ArrayList<String>();

    @Column(name = "time_to_live", nullable = false)
    private long timeToLive = -1;

    @Column(name = "timestamp", nullable = false)
    private long timestamp = 0;

    @OneToOne(targetEntity = MemberImpl.class)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    @ManyToOne(targetEntity = OAuthSofunConsumer.class)
    @JoinColumn(name = "consumer_id", nullable = false)
    private OAuthSofunConsumer consumer;

    public OAuthSofunAccessToken() {
        super(null, null, null, null, 0, null);
    }

    public OAuthSofunAccessToken(String token, String secret, String[] scopes,
            String[] permissions, long timeToLive, OAuthSofunConsumer consumer) {
        super(token, secret, scopes, permissions, timeToLive, consumer);
        this.token = token;
        this.secret = secret;
        if (scopes != null) {
            this.scopes = Arrays.asList(scopes);
        }
        if (permissions != null) {
            this.permissions = Arrays.asList(permissions);
        }
        this.timeToLive = timeToLive;
        this.consumer = consumer;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getSecret() {
        return secret;
    }

    @Override
    public String[] getScopes() {
        return scopes.toArray(new String[scopes.size()]);
    }

    @Override
    public String[] getPermissions() {
        return permissions.toArray(new String[permissions.size()]);
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public long getTimeToLive() {
        return timeToLive;
    }

    public Member getMember() {
        return member;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setScopes(String[] scopes) {
        if (scopes == null) {
            this.scopes = new ArrayList<String>();
        } else {
            this.scopes = Arrays.asList(scopes);
        }
    }

    /**
     * Sets the permissions bound with this token.
     * 
     * @param permissions: a list of {@link String}
     */
    public void setPermissions(String[] permissions) {
        if (permissions == null) {
            this.permissions = new ArrayList<String>();
        } else {
            this.permissions = Arrays.asList(permissions);
        }
    }

    /**
     * Sets the token time to live.
     * 
     * @param timeToLive: a time stamp in milliseconds or -1 if it never
     *        expires.
     */
    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    /**
     * Sets the token creation time stamp.
     * 
     * @param timestamp: a value in milliseconds since epoch
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets the {@link Member} holding this token.
     * 
     * @param member: a {@link Member} instance.
     */
    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * Returns the corresponding consumer.
     * 
     * @return a {@link OAuthConsumer} instance.
     */
    @Override
    public OAuthConsumer getConsumer() {
        return consumer;
    }

    /**
     * Has the token expired?
     * 
     * @return true of false.
     */
    public boolean isExpired() {
        final long current = System.currentTimeMillis();
        if (current >= getTimestamp() + getTimeToLive()) {
            return true;
        }
        return false;
    }

}
