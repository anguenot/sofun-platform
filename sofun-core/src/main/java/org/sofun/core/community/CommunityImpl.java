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

package org.sofun.core.community;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.sofun.core.api.community.Community;
import org.sofun.core.api.community.table.MemberRankingTable;
import org.sofun.core.api.feed.Feed;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.TeamPrivacy;
import org.sofun.core.team.TeamImpl;

/**
 * Community Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "communities")
public class CommunityImpl implements Community {

    private static final long serialVersionUID = 8142423855826731890L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "name", unique = true, nullable = false)
    protected String name;

    @Column(name = "created", nullable = false)
    protected Date created;

    @Column(name = "description")
    protected String description;

    @Column(
            name = "community_ranking_only",
            nullable = false,
            columnDefinition = "boolean default false")
    protected boolean communityRankingOnly;

    @Column(
            name = "community_feed_only",
            nullable = false,
            columnDefinition = "boolean default false")
    protected boolean communityFeedOnly;

    @ManyToMany(
            targetEntity = TeamImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "communities_teams", joinColumns = { @JoinColumn(
            name = "community_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "team_id",
            referencedColumnName = "id") })
    protected Set<Team> teams;

    @OneToOne(
            targetEntity = TeamImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "default_team_id")
    protected Team defaultTeam;

    public CommunityImpl() {
        super();
    }

    public CommunityImpl(String name) {
        this();
        this.name = name;
    }

    @Override
    public void addAdmin(Member admin) {
        getDefaultTeam().addAdmin(admin);
    }

    @Override
    public Member addMember(Member member) {
        getDefaultTeam().addMember(member);
        return member;
    }

    @Override
    public void addTeam(Team team) {
        if (team != null) {
            getTeams().add(team);
        }
    }

    @Override
    public int countAdmins() {
        return getAdmins().size();
    }

    @Override
    public int countMembers() {
        return getMembers().size();
    }

    @Override
    public void delTeam(Team team) {
        if (team != null) {
            getTeams().remove(team);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Community) {
            Community c = (Community) obj;
            return c.getName().equals(getName()) ? true : false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return "Community: " + getName();
    }

    @Override
    public Set<Member> getAdmins() {
        return getDefaultTeam().getAdmins();
    }

    @Override
    public Date getCreated() {
        if (created == null) {
            return null;
        }
        return (Date) created.clone();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Set<Member> getMembers() {
        return getDefaultTeam().getMembers();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<Team> getTeams() {
        if (teams == null) {
            teams = new HashSet<Team>();
        }
        return teams;
    }

    @Override
    public boolean isAdmin(Member member) {
        return getDefaultTeam().isAdmin(member);
    }

    @Override
    public boolean isMember(Member member) {
        return getDefaultTeam().isMember(member);
    }

    @Override
    public void removeAdmin(Member admin) {
        getDefaultTeam().removeAdmin(admin);
    }

    @Override
    public void removeMember(Member member) {
        getDefaultTeam().removeMember(member);
    }

    @Override
    public void setCreated(Date date) {
        if (date != null) {
            this.created = (Date) date.clone();
        }
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    protected void setId(long id) {
        this.id = id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Team getDefaultTeam() {
        if (defaultTeam == null) {
            defaultTeam = new TeamImpl(getName(), true);
            defaultTeam.setName(getName());
            defaultTeam.setDescription("Default Team For Community "
                    + getName());
            defaultTeam.setPrivacy(TeamPrivacy.PRIVATE);
            defaultTeam.addCommmunity(this);
        }
        return defaultTeam;
    }

    @Override
    public void setDefaultTeam(Team team) {
        this.defaultTeam = team;
    }

    @Override
    public MemberRankingTable getRankingTable() {
        return getDefaultTeam().getTeamRanking();
    }

    @PrePersist
    public void onCreate() {
        Calendar now = Calendar.getInstance();
        setCreated(now.getTime());
    }

    @Override
    public boolean isCommunityRankingOnly() {
        return communityRankingOnly;
    }

    @Override
    public void setCommunityRankingOnly(boolean communityRankingOnly) {
        this.communityRankingOnly = communityRankingOnly;
    }

    @Override
    public Feed getActivityFeed() {
        return getDefaultTeam().getActivityFeed();
    }

    @Override
    public boolean isCommunityFeedOnly() {
        return communityFeedOnly;
    }

    @Override
    public void setCommunityFeedOnly(boolean communityFeedOnly) {
        this.communityFeedOnly = communityFeedOnly;
    }

    @Override
    public void setAdmins(Set<Member> admins) {
        getDefaultTeam().setAdmins(admins);
    }

    @Override
    public void setMembers(Set<Member> members) {
        getDefaultTeam().setMembers(members);
    }

}
