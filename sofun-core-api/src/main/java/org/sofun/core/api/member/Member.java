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

package org.sofun.core.api.member;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.feed.FeedEntry;
import org.sofun.core.api.member.bank.MemberIBAN;
import org.sofun.core.api.member.bank.MemberRIB;
import org.sofun.core.api.team.Team;

/**
 * Member Principal
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface Member extends Serializable {

    void addAdminTeam(Team team);

    void addFriend(Member member);

    void addMemberTeam(Team team);

    String getAbout();

    String getAccountStatus();

    String getAccountType();

    Set<Team> getAdminTeams();

    URL getAvatar();

    List<FeedEntry> getBatchedFeedEntries(int offset, int size);

    Date getBirthDate();

    Date getCreated();

    String getEmail();

    String getFacebookId();

    String getFacebookToken();

    Iterator<FeedEntry> getFeedEntries();

    String getFirstName();

    Set<Member> getFriends();

    int getGender();

    long getId();

    String getLastName();

    String getLocale();

    String getLocation();

    Set<Team> getMemberTeams();

    String getMiddleNames();

    String getMobilePhone();

    Date getModified();

    String getNickName();

    String getPassword();

    MemberPostalAddress getPostalAddress();

    Double getTimeZone();

    String getTwitterId();

    Boolean isFriend(Member member);

    void removeFriend(Member member);

    void setAbout(String about);

    void setAccountStatus(String status);

    void setAccountType(String type);

    void setAdminTeams(Set<Team> teams);

    void setAvatar(URL avatar);

    void setBirthDate(Date birhDate);

    void setCreated(Date created);

    void setEmail(String email) throws CoreException;

    void setFacebookId(String facebookId);

    void setFacebookToken(String token);

    void setFirstName(String firstName);

    void setFriends(Set<Member> friends);

    void setGender(int gender);

    void setId(long id);

    void setLastName(String lastName);

    void setLocale(String locale);

    void setLocation(String location);

    void setMemberTeams(Set<Team> teams);

    void setMiddleName(String middleName);

    void setMobilePhone(String mobile);

    void setModified(Date date);

    void setNickName(String nickName);

    void setPassword(String password);

    void setPostalAddress(MemberPostalAddress address);

    void setTimeZone(Double tz);

    void setTwitterId(String twitterId);

    String getTitle();

    void setTitle(String title);

    String getBirthCountry();

    void setBirthCountry(String country);

    String getBirthPlace();

    void setBirthPlace(String place);

    String getBirthArea();

    void setBirthArea(String area);

    MemberCredit getMemberCredit();

    void setMemberCredit(MemberCredit credit);

    MemberIBAN getIBAN();

    void setIBAN(MemberIBAN iban);

    MemberRIB getRIB();

    void setRIB(MemberRIB rib);

    List<MemberTransaction> getTransactions();

    void addTransaction(MemberTransaction txn);

    String getActivationKey();

    void setActivationKey(String activationKey);

    int getMemberSuspendedDays();

    void setMemberSuspendedDay(int days);

    Date getMemberSuspendedDate();

    void setMemberSuspendedDate(Date date);

    String getIDFileName();

    void setIDFileName(String filename);

    String getRIBFileName();

    void setRIBFileName(String filename);

    Set<MemberRole> getRoles();

    void setRoles(Set<MemberRole> roles);

    void addRole(MemberRole role);

    void setLastModified(Date modifield);

    Date getPolicyAcceptanceDate();

    void setPolicyAcceptanceDate(Date date);

    Date getGamblingFRAccountCreationDate();

    void setGamblingFRAccountCreationDate(Date date);

    Date getGamblingFRAccountActivationDate();

    void setGamblingFRAccountActivationDate(Date date);

}
