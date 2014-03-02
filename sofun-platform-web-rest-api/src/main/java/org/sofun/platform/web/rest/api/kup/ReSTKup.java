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

package org.sofun.platform.web.rest.api.kup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.platform.web.rest.api.member.ReSTMember;

/**
 * ReST Kup API.
 * 
 * <p/>
 * 
 * Serializable wrapper on {@link Kup}
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTKup implements Serializable {

    private static final long serialVersionUID = 5546652063873267851L;

    protected long uuid;

    protected String name;

    protected String description;

    protected String type;

    protected Date startDate;

    protected Date endDate;

    protected String avatarUrl;

    protected float stake;

    protected float jackpot;

    protected byte repartitionType;

    protected float guaranteedPrice;

    protected float rakePercentage;

    protected float rakeAmount;

    protected Collection<ReSTMember> admins = new ArrayList<ReSTMember>();

    protected Collection<ReSTMember> participants = new ArrayList<ReSTMember>();

    protected int numberOfAdmins;

    protected int numberOfParticipants;

    protected String category;

    protected String sport;

    protected boolean isTemplate = false;

    protected int status = 0;

    protected long teamUUID;

    protected String teamName;

    public ReSTKup() {
        super();
    }

    public ReSTKup(Kup coreKup) {
        super();

        if (coreKup != null) {

            setUuid(coreKup.getId());
            setName(coreKup.getName());
            setDescription(coreKup.getDescription());
            setType(coreKup.getType());
            setStartDate(coreKup.getEffectiveStartDate());
            setEndDate(coreKup.getEffectiveEndDate());
            setAvatarUrl(coreKup.getAvatar());
            setStake(coreKup.getStake());
            setGuaranteedPrice(coreKup.getGuaranteedPrice());
            setJackpot(coreKup.getEffectiveJackpot());
            setStatus(coreKup.getStatus());
            setRepartitionType(coreKup.getRepartitionRuleType());
            setRakeAmount(coreKup.getRakeAmount());
            setRakePercentage(coreKup.getRakePercentage());

            Set<Member> coreAdmins = coreKup.getAdmins();
            if (coreAdmins != null) {
                synchronized (coreAdmins) {
                    for (Member m : coreAdmins) {
                        admins.add(new ReSTMember(m));
                    }
                }
            }
            setNumberOfAdmins(admins.size());

            setNumberOfParticipants(coreKup.getNbParticipants());
            setTemplate(coreKup.isTemplate());

            setTeamUUID(coreKup.getTeam().getId());
            setTeamName(coreKup.getTeam().getName());

        }
    }

    public ReSTKup(Kup coreKup, boolean withParticipants) {
        this(coreKup);
        if (coreKup != null) {
            if (withParticipants) {
                Set<Member> coreParticipants = coreKup.getParticipants();
                if (coreParticipants != null) {
                    synchronized (coreParticipants) {
                        for (Member m : coreParticipants) {
                            participants.add(new ReSTMember(m));
                        }
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartDate() {
        if (startDate != null) {
            return (Date) startDate.clone();
        }
        return null;
    }

    public void setStartDate(Date startDate) {
        if (startDate != null) {
            this.startDate = (Date) startDate.clone();
        } else {
            this.startDate = null;
        }
    }

    public Date getEndDate() {
        if (endDate != null) {
            return (Date) endDate.clone();
        }
        return null;
    }

    public void setEndDate(Date endDate) {
        if (endDate != null) {
            this.endDate = (Date) endDate.clone();
        } else {
            this.endDate = null;
        }
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public float getStake() {
        return stake;
    }

    public void setStake(float stake) {
        this.stake = stake;
    }

    public float getJackpot() {
        return jackpot;
    }

    public void setJackpot(float jackpot) {
        this.jackpot = jackpot;
    }

    public Collection<ReSTMember> getAdmins() {
        return admins;
    }

    public void setAdmins(Collection<ReSTMember> admins) {
        this.admins = admins;
    }

    public int getNumberOfAdmins() {
        return numberOfAdmins;
    }

    public void setNumberOfAdmins(int numberOfAdmins) {
        this.numberOfAdmins = numberOfAdmins;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Collection<ReSTMember> getParticipants() {
        return participants;
    }

    public void setParticipants(Collection<ReSTMember> participants) {
        this.participants = participants;
    }

    public byte getRepartitionType() {
        return repartitionType;
    }

    public void setRepartitionType(byte repartitionType) {
        this.repartitionType = repartitionType;
    }

    public float getGuaranteedPrice() {
        return guaranteedPrice;
    }

    public void setGuaranteedPrice(float guaranteedPrice) {
        this.guaranteedPrice = guaranteedPrice;
    }

    public float getRakeAmount() {
        return rakeAmount;
    }

    public void setRakeAmount(float rakeAmount) {
        this.rakeAmount = rakeAmount;
    }

    public float getRakePercentage() {
        return rakePercentage;
    }

    public void setRakePercentage(float rakePercentage) {
        this.rakePercentage = rakePercentage;
    }

    public long getTeamUUID() {
        return teamUUID;
    }

    public void setTeamUUID(long teamUUID) {
        this.teamUUID = teamUUID;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

}
