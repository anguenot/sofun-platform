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

package org.sofun.core.member;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.persistence.Basic;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.feed.FeedEntry;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberAccountStatus;
import org.sofun.core.api.member.MemberAccountType;
import org.sofun.core.api.member.MemberCredit;
import org.sofun.core.api.member.MemberPostalAddress;
import org.sofun.core.api.member.MemberRole;
import org.sofun.core.api.member.MemberTransaction;
import org.sofun.core.api.member.MemberTransactionStatus;
import org.sofun.core.api.member.MemberTransactionType;
import org.sofun.core.api.member.bank.MemberIBAN;
import org.sofun.core.api.member.bank.MemberRIB;
import org.sofun.core.api.team.Team;
import org.sofun.core.feed.FeedEntryImpl;
import org.sofun.core.team.TeamImpl;

/**
 * Sofun Core Member Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members")
public class MemberImpl implements Member {

    private static final long serialVersionUID = -6567997937226507898L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Basic(optional = false)
    @Column(name = "email", unique = true, nullable = false)
    protected String email;

    @Column(name = "password")
    protected String password;

    @Column(name = "type", nullable = false)
    protected String type;

    @Column(name = "status", nullable = false)
    protected String status;

    @Column(name = "fr_activation_key")
    protected String frenchActivationKey;

    @Column(name = "member_suspended_days", columnDefinition = "int default 0")
    protected int memberSuspendedDays = 0;

    @Column(name = "member_suspendedDate")
    protected Date memberSuspendedDate;

    @Column(name = "filename_id")
    protected String idFilename;

    @Column(name = "filename_rib")
    protected String ribFilename;

    @Column(name = "birth_country")
    protected String birthCountry;

    @Column(name = "birth_area")
    protected String birthArea;

    @Column(name = "birth_place")
    protected String birthPlace;

    @Column(name = "facebook_id")
    protected String facebookId;

    @Column(name = "facebook_token")
    protected String facebookToken;

    @Column(name = "twitter_id")
    protected String twitterId;

    @Column(name = "firstname")
    protected String firstName;

    @Column(name = "middlename")
    protected String middleName;

    @Column(name = "lastname")
    protected String lastName;

    @Column(name = "nickName", unique = true)
    protected String nickName;

    @Column(name = "birthdate")
    protected Date birthDate;

    @Column(name = "gender")
    protected int gender;

    @Column(name = "title")
    protected String title;

    @Column(name = "locale")
    protected String locale;

    @Column(name = "avatar_url")
    protected String avatarUrl;

    @Column(name = "timezone")
    protected Double timeZone;

    @Column(name = "location")
    protected String location;

    @Column(name = "bio")
    protected String bio;

    @Column(name = "mobilePhone")
    protected String mobilePhone;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date created;

    @Column(name = "modified")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modified;

    @Column(name = "lastLogin")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastLogin;

    @Column(name = "policy_acceptance_date")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date policyAcceptanceDate;

    @Column(name = "gambling_fr_account_creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date gamblingFrAccountCreationDate;

    @Column(name = "gambling_fr_account_activation_date")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date gamblingFrAccountActivationDate;

    @Column(name = "numberOfConnection", columnDefinition = "int default 0")
    protected int numberOfConnection = 0;

    @OneToOne(
            targetEntity = MemberPostalAddressImpl.class,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    protected MemberPostalAddress address;

    @OneToOne(
            targetEntity = MemberCreditImpl.class,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    protected MemberCredit credit;

    @OneToOne(
            targetEntity = MemberIBANImpl.class,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    protected MemberIBAN iban;

    @OneToOne(
            targetEntity = MemberRIBImpl.class,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    protected MemberRIB rib;

    @ManyToMany(targetEntity = MemberImpl.class, fetch = FetchType.LAZY)
    @JoinTable(name = "members_friends", joinColumns = { @JoinColumn(
            name = "member_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "member_id_r",
            referencedColumnName = "id") })
    protected Set<Member> friends;

    @ManyToMany(targetEntity = TeamImpl.class, mappedBy = "members")
    protected Set<Team> memberTeams;

    @ManyToMany(targetEntity = TeamImpl.class, mappedBy = "admins")
    protected Set<Team> adminTeams;

    @OneToMany(
            targetEntity = FeedEntryImpl.class,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "member")
    protected List<FeedEntry> feedEntries = new ArrayList<FeedEntry>();

    @OneToMany(
            targetEntity = MemberTransactionImpl.class,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "member")
    protected List<MemberTransaction> transactions = new ArrayList<MemberTransaction>();

    @ManyToMany(
            targetEntity = MemberRoleImpl.class,
            fetch = FetchType.EAGER,
            cascade = CascadeType.REFRESH)
    @JoinTable(name = "members_roles", joinColumns = { @JoinColumn(
            name = "member_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "role_id",
            referencedColumnName = "id") })
    private Set<MemberRole> roles = new HashSet<MemberRole>();

    public MemberImpl() {
    }

    public MemberImpl(String email, String status, String type)
            throws CoreException {
        this();
        setEmail(email);
        setAccountStatus(status);
        setAccountType(type);
    }

    public String getName() {
        return getEmail();
    }

    @Override
    public String getFacebookId() {
        return facebookId;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public Date getBirthDate() {
        if (birthDate == null) {
            return null;
        }
        return (Date) birthDate.clone();
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public MemberPostalAddress getPostalAddress() {
        if (address == null) {
            address = new MemberPostalAddressImpl();
        }
        return address;
    }

    @Override
    public Date getCreated() {
        if (created == null) {
            return null;
        }
        return (Date) created.clone();
    }

    @Override
    public void setCreated(Date created) {
        if (created != null) {
            this.created = (Date) created.clone();
        }
    }

    @Override
    public Date getModified() {
        if (modified == null) {
            return null;
        }
        return (Date) modified.clone();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getMiddleNames() {
        return middleName;
    }

    @Override
    public String getTwitterId() {
        return twitterId;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public void setEmail(String email) throws CoreException {
        /*
         * if (!Utils.isValidEmailAddress(email)) { throw new
         * CoreException("Email " + email +
         * " is invalid! Cannot create Member"); }
         */
        this.email = email;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    @Override
    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    @Override
    public void setBirthDate(Date birhDate) {
        if (birhDate != null) {
            this.birthDate = (Date) birhDate.clone();
        }
    }

    @Override
    public void setGender(int gender) {
        this.gender = gender;
    }

    @Override
    public void setAvatar(URL avatar) {
        if (avatar != null) {
            this.avatarUrl = avatar.toString();
        }
    }

    @Override
    public void setPostalAddress(MemberPostalAddress address) {
        this.address = address;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public int getGender() {
        return gender;
    }

    @Override
    public URL getAvatar() {
        try {
            return new URL(avatarUrl);
        } catch (MalformedURLException e) {
            return null;
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
        if (obj instanceof Member) {
            Member m = (Member) obj;
            if (m.getEmail() != null && getEmail() != null) {
                return m.getEmail().equals(getEmail());
            } else if (m.getId() != 0 && getId() != 0) {
                return m.getId() == getId();
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return String.valueOf(getEmail()).hashCode();
    }

    @PrePersist
    protected void onCreate() {
        Date now = Calendar.getInstance().getTime();
        setCreated(now);
        setModified(now);
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String getFacebookToken() {
        return facebookToken;
    }

    @Override
    public void setFacebookToken(String token) {
        this.facebookToken = token;
    }

    @Override
    public Double getTimeZone() {
        return timeZone;
    }

    @Override
    public void setTimeZone(Double tz) {
        this.timeZone = tz;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getAbout() {
        return bio;
    }

    @Override
    public void setAbout(String about) {
        this.bio = about;

    }

    @Override
    public String getMobilePhone() {
        return mobilePhone;
    }

    @Override
    public void setMobilePhone(String mobile) {
        this.mobilePhone = mobile;
    }

    @Override
    public String getNickName() {
        return nickName;
    }

    @Override
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public Set<Member> getFriends() {
        if (friends == null) {
            friends = new HashSet<Member>();
        }
        return friends;
    }

    @Override
    public void setFriends(Set<Member> friends) {
        this.friends = friends;
    }

    @Override
    public void addFriend(Member member) {
        if (member != null && !isFriend(member)) {
            getFriends().add(member);
        }
    }

    @Override
    public Boolean isFriend(Member member) {
        return getFriends().contains(member);
    }

    @Override
    public void setModified(Date date) {
        if (date != null) {
            this.modified = (Date) date.clone();
        }
    }

    @Override
    public void removeFriend(Member member) {
        if (getFriends().contains(member)) {
            getFriends().remove(member);
        }
    }

    @Override
    public Set<Team> getMemberTeams() {
        if (memberTeams == null) {
            memberTeams = new HashSet<Team>();
        }
        return memberTeams;
    }

    @Override
    public void setMemberTeams(Set<Team> teams) {
        this.memberTeams = teams;
    }

    @Override
    public void addMemberTeam(Team team) {
        if (!getMemberTeams().contains(team)) {
            memberTeams.add(team);
        }

    }

    @Override
    public Set<Team> getAdminTeams() {
        if (adminTeams == null) {
            adminTeams = new HashSet<Team>();
        }
        return adminTeams;
    }

    @Override
    public void setAdminTeams(Set<Team> teams) {
        this.adminTeams = teams;
    }

    @Override
    public void addAdminTeam(Team team) {
        if (!getAdminTeams().contains(team)) {
            adminTeams.add(team);
        }

    }

    @Override
    public Iterator<FeedEntry> getFeedEntries() {
        return feedEntries.listIterator();
    }

    @Override
    public List<FeedEntry> getBatchedFeedEntries(int offset, int size) {
        List<FeedEntry> batch = new ArrayList<FeedEntry>();
        ListIterator<FeedEntry> it = feedEntries.listIterator(offset);
        int i = 0;
        while (it.hasNext() && i < offset) {
            batch.add(it.next());
            i += 1;
        }
        return batch;
    }

    @Override
    public String getAccountStatus() {
        return status;
    }

    @Override
    public void setAccountStatus(String status) {
        if (status != null) {
            this.status = status;
        } else {
            this.status = MemberAccountStatus.CREATED;
        }
    }

    @Override
    public String getAccountType() {
        return type;
    }

    @Override
    public void setAccountType(String type) {
        if (type != null) {
            this.type = type;
        } else {
            this.type = MemberAccountType.SIMPLE;
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getBirthCountry() {
        return birthCountry;
    }

    @Override
    public void setBirthCountry(String country) {
        this.birthCountry = country;
    }

    @Override
    public String getBirthPlace() {
        return birthPlace;
    }

    @Override
    public void setBirthPlace(String place) {
        this.birthPlace = place;
    }

    @Override
    public String getBirthArea() {
        return birthArea;
    }

    @Override
    public void setBirthArea(String area) {
        this.birthArea = area;
    }

    @Override
    public MemberCredit getMemberCredit() {
        if (credit == null) {
            credit = new MemberCreditImpl();
        }
        return credit;
    }

    @Override
    public void setMemberCredit(MemberCredit credit) {
        this.credit = credit;
    }

    @Override
    public MemberIBAN getIBAN() {
        return iban;
    }

    @Override
    public void setIBAN(MemberIBAN iban) {
        this.iban = iban;
    }

    @Override
    public MemberRIB getRIB() {
        return rib;
    }

    @Override
    public void setRIB(MemberRIB rib) {
        this.rib = rib;
    }

    @Override
    public List<MemberTransaction> getTransactions() {
        if (transactions == null) {
            transactions = new ArrayList<MemberTransaction>();
        }
        return transactions;
    }

    @Override
    public void addTransaction(MemberTransaction txn) {
        if (txn != null) {
            getTransactions().add(txn);
            MemberCredit credit = getMemberCredit();
            if (txn.isDebit()
                    && getMemberCredit().getCredit() >= txn.getAmount()) {
                credit.setCredit(credit.getCredit() - txn.getAmount());
            } else if (txn.isCredit() || txn.isBonus()) {
                // Only credit if transaction gets approved.
                // The case if pending transaction will be handled
                // asynchronously
                boolean approved = true;
                if (txn.getType().equals(MemberTransactionType.CC_CREDIT)
                        && !txn.getStatus().equals(
                                MemberTransactionStatus.APPROVED)) {
                    approved = false;
                }
                if (approved) {
                    credit.setCredit(credit.getCredit() + txn.getAmount());
                }
            }
            setMemberCredit(credit);
        }
    }

    @Override
    public String getActivationKey() {
        return frenchActivationKey;
    }

    @Override
    public void setActivationKey(String activationKey) {
        this.frenchActivationKey = activationKey;
    }

    @Override
    public int getMemberSuspendedDays() {
        return memberSuspendedDays;
    }

    @Override
    public void setMemberSuspendedDay(int days) {
        this.memberSuspendedDays = days;
    }

    @Override
    public String getIDFileName() {
        return idFilename;
    }

    @Override
    public void setIDFileName(String filename) {
        this.idFilename = filename;
    }

    @Override
    public String getRIBFileName() {
        return ribFilename;
    }

    @Override
    public void setRIBFileName(String filename) {
        this.ribFilename = filename;
    }

    @Override
    public Date getMemberSuspendedDate() {
        if (memberSuspendedDate != null) {
            return (Date) memberSuspendedDate.clone();
        }
        return null;
    }

    @Override
    public void setMemberSuspendedDate(Date date) {
        if (date == null) {
            this.memberSuspendedDate = null;
        } else {
            this.memberSuspendedDate = (Date) date.clone();
        }
    }

    @Override
    public Set<MemberRole> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(Set<MemberRole> roles) {
        this.roles = roles;
    }

    @Override
    public void addRole(MemberRole role) {
        this.roles.add(role);
    }

    @Override
    public void setLastModified(Date modifield) {
        if (modified != null) {
            this.modified = (Date) modifield.clone();
        }
    }

    @Override
    public Date getPolicyAcceptanceDate() {
        if (policyAcceptanceDate == null) {
            // BBB : opening of gambling services
            return (Date) created.clone();
        } else {
            return (Date) policyAcceptanceDate.clone();
        }
    }

    @Override
    public void setPolicyAcceptanceDate(Date date) {
        if (date != null) {
            policyAcceptanceDate = (Date) date.clone();
        }
    }

    @Override
    public Date getGamblingFRAccountCreationDate() {
        if (gamblingFrAccountCreationDate == null) {
            return (Date) created.clone();
        } else {
            return (Date) gamblingFrAccountCreationDate.clone();
        }
    }

    @Override
    public void setGamblingFRAccountCreationDate(Date date) {
        if (date != null) {
            gamblingFrAccountCreationDate = (Date) date.clone();
        }

    }

    @Override
    public Date getGamblingFRAccountActivationDate() {
        return (Date) gamblingFrAccountActivationDate.clone();
    }

    @Override
    public void setGamblingFRAccountActivationDate(Date date) {
        if (date != null) {
            gamblingFrAccountActivationDate = (Date) date.clone();
        }

    }

}
