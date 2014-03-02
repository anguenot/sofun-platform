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

package org.sofun.core.team;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.PasswordUtil;
import org.sofun.core.api.community.Community;
import org.sofun.core.api.community.table.MemberRankingTable;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.feed.Feed;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.TeamPrivacy;
import org.sofun.core.api.team.TeamTag;
import org.sofun.core.api.team.TeamType;
import org.sofun.core.api.team.table.TeamRankingTable;
import org.sofun.core.community.CommunityImpl;
import org.sofun.core.feed.FeedImpl;
import org.sofun.core.kup.KupImpl;
import org.sofun.core.member.MemberImpl;
import org.sofun.core.team.table.TeamRankingTableImpl;

/**
 * Team (a.k.a. `room`) implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 */
@Entity
@Table(name = "teams")
public class TeamImpl implements Team {

    private static final long serialVersionUID = 2395423811479854937L;

    private static final Log log = LogFactory.getLog(TeamImpl.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "name", nullable = false)
    protected String name;

    @Column(name = "description")
    protected String description;

    @Column(name = "privacy", nullable = false)
    protected String privacy = TeamPrivacy.PUBLIC;

    @Column(name = "avatar")
    protected String avatar;

    @Column(name = "password")
    protected String password;

    @Column(name = "created", nullable = false)
    protected Date created;

    @ManyToMany(targetEntity = MemberImpl.class, fetch = FetchType.LAZY)
    @JoinTable(name = "teams_members", joinColumns = { @JoinColumn(
            name = "team_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "member_id",
            referencedColumnName = "id") })
    protected Set<Member> members;

    @ManyToMany(targetEntity = MemberImpl.class, fetch = FetchType.LAZY)
    @JoinTable(name = "teams_admins", joinColumns = { @JoinColumn(
            name = "team_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "member_id",
            referencedColumnName = "id") })
    protected Set<Member> admins;

    @ManyToMany(
            targetEntity = CommunityImpl.class,
            fetch = FetchType.LAZY,
            mappedBy = "teams")
    protected Set<Community> communities;

    @OneToMany(
            targetEntity = KupImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "teams_kups", joinColumns = { @JoinColumn(
            name = "team_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "kup_id",
            referencedColumnName = "id") })
    protected Set<Kup> kups;

    @OneToOne(
            targetEntity = TeamRankingTableImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "table_id")
    protected TeamRankingTable rankingTable;

    @OneToOne(
            targetEntity = FeedImpl.class,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    protected Feed feed;

    @Column(
            name = "has_ranking_table",
            nullable = false,
            columnDefinition = "boolean default true")
    protected boolean hasRankingTable;

    @ManyToMany(
            targetEntity = TeamTagImpl.class,
            fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST)
    @JoinTable(name = "teams_tags_teams", joinColumns = { @JoinColumn(
            name = "team_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "tag_id",
            referencedColumnName = "id") })
    protected Set<TeamTag> tags;

    @ManyToMany(targetEntity = TeamTypeImpl.class, fetch = FetchType.EAGER)
    @JoinTable(name = "teams_types_teams", joinColumns = { @JoinColumn(
            name = "team_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "type_id",
            referencedColumnName = "id") })
    protected Set<TeamType> types;

    @Column(name = "nb_members", columnDefinition = "int default 0")
    protected int nbMembers = 0;

    public TeamImpl() {
        setPrivacy(TeamPrivacy.PRIVATE);
    }

    public TeamImpl(String name) {
        this(name, true);
    }

    public TeamImpl(String name, boolean hasRankingTable) {
        this();
        this.name = name;
        this.hasRankingTable = hasRankingTable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Date getCreated() {
        if (created == null) {
            return null;
        }
        return (Date) created.clone();
    }

    @Override
    public Set<Member> getAdmins() {
        if (admins == null) {
            admins = new HashSet<Member>();
        }
        return admins;
    }

    @Override
    public boolean isAdmin(Member member) {
        return getAdmins().contains(member);
    }

    @Override
    public Set<Member> getMembers() {
        if (members == null) {
            members = new HashSet<Member>();
        }
        return members;
    }

    @Override
    public int countMembers() {
        return getMembers().size();
    }

    @Override
    public boolean isMember(Member member) {
        return member.getMemberTeams().contains(this);
    }

    @Override
    public String getPrivacy() {
        return privacy;
    }

    @Override
    public Set<Kup> getKups() {
        if (kups == null) {
            kups = new HashSet<Kup>();
        }
        return kups;
    }

    @Override
    public void addKup(Kup kup) throws CoreException {
        if (!kups.contains(kup)) {
            kups.add(kup);
        }
    }

    @Override
    public void delKup(Kup kup) throws CoreException {
        if (kups.contains(kup)) {
            kups.remove(kup);
        }
    }

    @Override
    public Set<Community> getCommunities() {
        if (communities == null) {
            communities = new HashSet<Community>();
        }
        return communities;
    }

    @Override
    public void addAdmin(Member admin) {
        if (!isAdmin(admin)) {
            admins.add(admin);
            admin.addAdminTeam(this);
        }
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Member addMember(Member member) {
        getMembers().add(member);
        member.addMemberTeam(this);
        if (hasRankingTable) {
            getRankingTable().addEntryForMember(member);
        }
        return member;
    }

    @Override
    public void removeMember(Member member) {
        if (isMember(member)) {
            members.remove(member);
            member.getMemberTeams().remove(this);
        }
    }

    @Override
    public void removeAdmin(Member admin) {
        if (isAdmin(admin)) {
            admins.remove(admin);
            admin.getAdminTeams().remove(this);
        }
    }

    @Override
    public int countAdmins() {
        return getAdmins().size();
    }

    @Override
    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    @Override
    public TeamRankingTable getTeamRanking() {
        if (hasRankingTable && rankingTable == null) {
            // ranking table lazy initialization
            rankingTable = new TeamRankingTableImpl(this);
        }
        return rankingTable;
    }

    @Override
    public void setTeamRanking(TeamRankingTable table) {
        this.rankingTable = table;
    }

    @PrePersist
    protected void onCreate() {
        Date now = Calendar.getInstance().getTime();
        setCreated(now);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Team) {
            Team t = (Team) obj;
            if (t.getName() != null && getName() != null) {
                return t.getName().equals(getName()) ? true : false;
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public void setAdmins(Set<Member> admins) {
        this.admins = admins;
    }

    @Override
    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    @Override
    public MemberRankingTable getRankingTable() {
        return getTeamRanking();
    }

    @Override
    public void setCommunities(Set<Community> communities) {
        this.communities = communities;
    }

    @Override
    public void setCreated(Date created) {
        if (created != null) {
            this.created = (Date) created.clone();
        }
    }

    @Override
    public Feed getActivityFeed() {
        if (feed == null) {
            feed = new FeedImpl();
        }
        return feed;
    }

    @Override
    public Kup getKupById(long kupId) {
        for (Kup kup : getKups()) {
            if (kup.getId() == kupId) {
                return kup;
            }
        }
        return null;
    }

    @Override
    public Kup getKupByName(String name) {
        for (Kup kup : getKups()) {
            if (kup.getName().equals(name)) {
                return kup;
            }
        }
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) throws CoreException {
        // Team password does not need to comply to any rules.
        // There is no security risk here for the application.
        this.password = PasswordUtil.getHashFor(password);
    }

    @Override
    public String resetPassword() throws CoreException {
        final String newPassword = PasswordUtil.generatePassword();
        final String hash = PasswordUtil.getHashFor(newPassword);
        setPassword(hash);
        log.info("Administrator of Kup w/ id=" + String.valueOf(getId())
                + " reset its password.");
        return newPassword;
    }

    @Override
    public boolean verifyPassword(String password) throws CoreException {
        final String hash = PasswordUtil.getHashFor(password);
        return getPassword().equals(hash);
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public Set<TeamTag> getTags() {
        if (tags == null) {
            tags = new HashSet<TeamTag>();
        }
        return tags;
    }

    @Override
    public void setTags(Set<TeamTag> tags) {
        this.tags = tags;
    }

    @Override
    public void addTag(TeamTag tag) {
        getTags().add(tag);
    }

    @Override
    public void delTag(TeamTag tag) {
        if (getTags().contains(tag)) {
            getTags().remove(tag);
        }
    }

    @Override
    public Set<TeamType> getTypes() {
        if (types == null) {
            types = new HashSet<TeamType>();
        }
        return types;
    }

    @Override
    public void setTypes(Set<TeamType> types) {
        this.types = types;
    }

    @Override
    public void addType(TeamType type) {
        getTypes().add(type);
    }

    @Override
    public void delType(TeamType type) {
        if (getTypes().contains(type)) {
            getTypes().remove(type);
        }
    }

    @Override
    public void addCommmunity(Community community) {
        if (!getCommunities().contains(community)) {
            getCommunities().add(community);
        }
    }

    @Override
    public boolean hasRankingTable() {
        return hasRankingTable;
    }

    @Override
    public void delCommunity(Community community) {
        if (community != null && getCommunities().contains(community)) {
            getCommunities().remove(community);
        }
    }

    @Override
    public int getNbMembers() {
        return nbMembers;
    }

    @Override
    public void setNbMembers(int nbMembers) {
        this.nbMembers = nbMembers;
    }

}
