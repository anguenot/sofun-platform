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

package org.sofun.platform.facebook;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.sofun.core.api.member.Member;
import org.sofun.core.member.MemberImpl;
import org.sofun.platform.facebook.api.FacebookMemberInfo;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "facebook_member_info")
public class FacebookMemberInfoImpl implements FacebookMemberInfo {

    private static final long serialVersionUID = -2643265418332621507L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @OneToOne(targetEntity = MemberImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true, nullable = false)
    protected Member member;

    @Column(name = "firstname")
    protected String firstName;

    @Column(name = "lastname")
    protected String lastName;

    public FacebookMemberInfoImpl() {

    }

    public FacebookMemberInfoImpl(Member member) {
        this();
        this.member = member;
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public void setMember(Member member) {
        this.member = member;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getFullName() {
        String fullname = "";
        if (getFirstName() != null && !"".equals(getFirstName())) {
            fullname += getFirstName();
            fullname += " ";
        }
        if (getLastName() != null && !"".equals(getLastName())) {
            fullname += getLastName();
        }
        return fullname;
    }

}
