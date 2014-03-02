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

import org.sofun.core.api.member.bank.MemberIBAN;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members_iban")
public class MemberIBANImpl implements MemberIBAN {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "iban")
    protected String iban;

    @Column(name = "swift")
    protected String swift;

    public MemberIBANImpl() {
        super();
    }

    public MemberIBANImpl(String iban, String swift) {
        this();
        this.iban = iban;
        this.swift = swift;
    }

    @Override
    public String getIBAN() {
        return iban;
    }

    @Override
    public void setIBAN(String iban) {
        this.iban = iban;
    }

    @Override
    public String getSwift() {
        return swift;
    }

    @Override
    public void setSwift(String swift) {
        this.swift = swift;
    }

}
