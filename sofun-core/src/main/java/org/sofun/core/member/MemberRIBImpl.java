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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.sofun.core.api.banking.RIB;
import org.sofun.core.api.member.bank.MemberRIB;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members_rib")
public class MemberRIBImpl implements MemberRIB {

    private static final long serialVersionUID = -2067395602320166709L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "bank")
    protected String bank;

    @Column(name = "branch")
    protected String branch;

    @Column(name = "number")
    protected String number;

    @Column(name = "key")
    protected String key;

    public MemberRIBImpl() {
        super();
    }

    public MemberRIBImpl(String bank, String branch, String number, String key) {
        this();
        this.bank = bank;
        this.branch = branch;
        this.number = number;
        this.key = key;
    }

    @Override
    public String getBank() {
        return bank;
    }

    @Override
    public void setBank(String bank) {
        this.bank = bank;
    }

    @Override
    public String getBranch() {
        return branch;
    }

    @Override
    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toIban() {
        return RIB.toIBAN(bank, branch, number, key);
    }

}
