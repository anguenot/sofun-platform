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

package org.sofun.core.api.security;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Sofun Group.
 * 
 * <p/>
 * 
 * Holds a collection of roles.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class SofunGroup implements Group, Serializable {

    private static final long serialVersionUID = -1029083190003608523L;

    protected final String name;

    protected final Set<Principal> users = new HashSet<Principal>();

    public SofunGroup(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean addMember(Principal member) {
        return users.add(member);
    }

    @Override
    public boolean isMember(Principal member) {
        return users.contains(member);
    }

    @Override
    public Enumeration<? extends Principal> members() {
        return Collections.enumeration(users);
    }

    @Override
    public boolean removeMember(Principal member) {
        return users.remove(member);
    }

}
