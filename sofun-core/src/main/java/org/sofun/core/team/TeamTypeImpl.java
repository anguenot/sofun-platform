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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.sofun.core.api.team.TeamType;

/**
 * Team Type Implementation
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "teams_types")
public class TeamTypeImpl implements TeamType {

    private static final long serialVersionUID = -4860988798380664208L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "name", nullable = false)
    protected String name;

    @Column(name = "msgid")
    protected String msgid;

    public TeamTypeImpl() {
       
    }
    
    public TeamTypeImpl(String name) {
        this();
        this.name = name;
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
    public String getMsgid() {
        return msgid;
    }

    @Override
    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    @Override
    public int hashCode() {
        return String.valueOf(getName()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof TeamType) {
            TeamType other = (TeamType) obj;
            return getName().equals(other.getName());
        }
        return super.equals(obj);
    }

}
