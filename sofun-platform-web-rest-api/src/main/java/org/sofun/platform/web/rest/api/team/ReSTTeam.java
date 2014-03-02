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

package org.sofun.platform.web.rest.api.team;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupStatus;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.TeamTag;
import org.sofun.core.api.team.TeamType;
import org.sofun.platform.web.rest.api.kup.ReSTKup;
import org.sofun.platform.web.rest.api.member.ReSTMember;

/**
 * Team Web API.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTTeam implements Serializable, Comparable<ReSTTeam> {

    private static final long serialVersionUID = -2806322406135271966L;

    protected long uuid;

    protected String name;

    protected String description;

    protected Date created;

    protected String privacy;

    protected String avatar;

    protected List<ReSTTeamTag> tags = new ArrayList<ReSTTeamTag>();

    protected List<ReSTTeamType> types = new ArrayList<ReSTTeamType>();

    protected List<ReSTMember> admins = new ArrayList<ReSTMember>();

    protected int numberOfMembers = 0;

    protected List<ReSTMember> members = new ArrayList<ReSTMember>();

    protected int numberOfAdmins = 0;

    protected List<ReSTKup> kups = new ArrayList<ReSTKup>();

    protected int numberOfKups = 0;

    public ReSTTeam() {
        super();
    }

    public ReSTTeam(Team coreTeam, boolean withMembers) {

        this();

        if (coreTeam != null) {

            setUuid(coreTeam.getId());
            setName(coreTeam.getName());
            setDescription(coreTeam.getDescription());
            setCreated(coreTeam.getCreated());
            setPrivacy(coreTeam.getPrivacy());
            setAvatar(coreTeam.getAvatar());

            Set<Member> coreAdmins = coreTeam.getAdmins();
            if (coreAdmins != null) {
                for (Member m : coreAdmins) {
                    admins.add(new ReSTMember(m));
                }
            }
            setNumberOfAdmins(admins.size());

            if (withMembers) {
                Set<Member> coreMembers = coreTeam.getMembers();
                int total = 0;
                if (coreMembers != null) {
                    for (Member m : coreMembers) {
                        members.add(new ReSTMember(m));
                        total += 1;
                    }
                }
                setNumberOfMembers(total);
            } else {
                setNumberOfMembers(coreTeam.getNbMembers());
            }

            Set<Kup> coreKups = coreTeam.getKups();
            int total = 0;
            if (coreKups != null) {
                for (Kup k : coreKups) {
                    // TMP FIX to match getRoomKups()
                    if (k.getStatus() == KupStatus.CANCELED) {
                        continue;
                    }
                    kups.add(new ReSTKup(k));
                    total += 1;
                }
            }
            setNumberOfKups(total);

            for (TeamTag coreTag : coreTeam.getTags()) {
                tags.add(new ReSTTeamTag(coreTag));
            }

            for (TeamType coreType : coreTeam.getTypes()) {
                types.add(new ReSTTeamType(coreType));
            }

        }

    }

    public ReSTTeam(Team coreTeam) {
        this(coreTeam, true);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        if (created == null) {
            return null;
        }
        return (Date) created.clone();
    }

    public void setCreated(Date created) {
        if (created != null) {
            this.created = (Date) created.clone();
        }
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public Collection<ReSTMember> getMembers() {
        return members;
    }

    public void setMembers(List<ReSTMember> members) {
        this.members = members;
    }

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }

    public Collection<ReSTMember> getAdmins() {
        return admins;
    }

    public void setAdmins(List<ReSTMember> admins) {
        this.admins = admins;
    }

    public int getNumberOfAdmins() {
        return numberOfAdmins;
    }

    public void setNumberOfAdmins(int numberOfAdmins) {
        this.numberOfAdmins = numberOfAdmins;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof ReSTTeam) {
            ReSTTeam t = (ReSTTeam) obj;
            return t.getUuid() == getUuid();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getUuid()).hashCode();
    }

    @Override
    public int compareTo(ReSTTeam t) {

        if (t == null) {
            return 1;
        }

        if (getNumberOfMembers() > t.getNumberOfMembers()) {
            return 1;
        } else if (getNumberOfMembers() < t.getNumberOfMembers()) {
            return -1;
        } else {
            return 0;
        }

    }

    public boolean isMember(ReSTMember member) {
        return getMembers().contains(member);
    }

    public List<ReSTTeamTag> getTags() {
        return tags;
    }

    public void setTags(List<ReSTTeamTag> tags) {
        this.tags = tags;
    }

    public List<ReSTTeamType> getTypes() {
        return types;
    }

    public void setTypes(List<ReSTTeamType> types) {
        this.types = types;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<ReSTKup> getKups() {
        return kups;
    }

    public void setKups(List<ReSTKup> kups) {
        this.kups = kups;
    }

    public int getNumberOfKups() {
        return numberOfKups;
    }

    public void setNumberOfKups(int numberOfKups) {
        this.numberOfKups = numberOfKups;
    }

}
