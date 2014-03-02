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

package org.sofun.platform.web.rest.api.member;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jboss.resteasy.logging.Logger;
import org.sofun.core.api.member.Member;
import org.sofun.platform.facebook.FacebookUtils;

/**
 * Member Web API.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTMember implements Serializable {

    private final static Logger log = Logger.getLogger(ReSTMember.class);

    private static final long serialVersionUID = 735903598937796566L;

    protected long uuid;

    protected String firstName;

    protected String middleNames;

    protected String lastName;

    protected String nickName;

    protected String email;

    protected String type;

    protected String status;

    protected String facebookId;

    protected Date birthDate;

    protected int gender;

    protected String avatarSmall;

    protected String avatarBig;

    protected List<ReSTMember> friends = new ArrayList<ReSTMember>();

    protected boolean hasBet = false;

    protected boolean hasPredictions = false;

    protected Date policyAcceptanceDate;

    public ReSTMember() {
        super();
    }

    public ReSTMember(Member coreMember) {
        this();
        if (coreMember != null) {
            this.setUuid(coreMember.getId());
            this.setEmail(coreMember.getEmail());
            this.setFirstName(coreMember.getFirstName());
            this.setMiddleNames(coreMember.getMiddleNames());
            this.setLastName(coreMember.getLastName());
            this.setNickName(coreMember.getNickName());
            this.setFacebookId(coreMember.getFacebookId());
            this.setGender(coreMember.getGender());
            this.setBirthDate(coreMember.getBirthDate());
            this.setPolicyAcceptanceDate(coreMember.getPolicyAcceptanceDate());
            this.setStatus(coreMember.getAccountStatus());
            this.setType(coreMember.getAccountType());
            if (coreMember.getAvatar() != null
                    && !coreMember.getAvatar().toString()
                            .contains("graph.facebook.com")) {
                this.avatarBig = String.valueOf(coreMember.getAvatar());
                this.avatarSmall = String.valueOf(coreMember.getAvatar());
            } else if (coreMember.getFacebookId() != null) {
                try {
                    Long fbId = Long.valueOf(coreMember.getFacebookId());
                    this.avatarBig = FacebookUtils
                            .getPictureFor(fbId, "normal").toString();
                    this.avatarSmall = FacebookUtils.getPictureFor(fbId,
                            "small").toString();
                } catch (NumberFormatException e) {
                    log.debug("FacebookId for member=" + coreMember.getEmail()
                            + " is invalid.");
                    this.avatarBig = "";
                    this.avatarSmall = "";
                }
            } else {
                this.avatarBig = "";
                this.avatarSmall = "";
            }

        }

    }

    public ReSTMember(Member coreMember, boolean withFriends) {
        this(coreMember);
        if (coreMember != null && withFriends) {
            Set<Member> coreFriends = coreMember.getFriends();
            synchronized (coreFriends) {
                for (Member friend : coreFriends) {
                    if (!this.equals(new ReSTMember(friend))) {
                        friends.add(new ReSTMember(friend, false));
                    } else {
                        friends.add(this);
                    }
                }
            }
        }
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(String middleNames) {
        this.middleNames = middleNames;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public Date getBirthDate() {
        if (birthDate == null) {
            return null;
        }
        return (Date) birthDate.clone();
    }

    public void setBirthDate(Date birthDate) {
        if (birthDate != null) {
            this.birthDate = (Date) birthDate.clone();
        }
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAvatarSmall() {
        return avatarSmall;
    }

    public void setAvatarSmall(String avatar) {
        this.avatarSmall = avatar;
    }

    public String getAvatarBig() {
        return avatarBig;
    }

    public void setAvatarBig(String avatar) {
        this.avatarBig = avatar;
    }

    public List<ReSTMember> getFriends() {
        return friends;
    }

    public void setFriends(List<ReSTMember> friends) {
        this.friends = friends;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof ReSTMember) {
            ReSTMember m = (ReSTMember) obj;
            if (m.getEmail() != null && getEmail() != null) {
                if (m.getEmail().equals(getEmail())) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getEmail().hashCode();
    }

    @Deprecated
    public boolean getHasBet() {
        return hasBet;
    }

    @Deprecated
    public void setHasBet(boolean hasBet) {
        this.hasBet = hasBet;
    }

    public boolean isHasPredictions() {
        return hasPredictions;
    }

    public void setHasPredictions(boolean hasPredictions) {
        this.hasPredictions = hasPredictions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getPolicyAcceptanceDate() {
        return policyAcceptanceDate;
    }

    public void setPolicyAcceptanceDate(Date policyAcceptanceDate) {
        this.policyAcceptanceDate = policyAcceptanceDate;
    }

}
