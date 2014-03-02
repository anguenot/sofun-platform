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

/**
 * Sofun Principal.
 * 
 * <p/>
 * 
 * This class represents either a user or a role.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class SofunPrincipal implements Principal, Serializable {

    private static final long serialVersionUID = -6505266622036912843L;
    
    private String name;

    public SofunPrincipal(String name) {
        if (name == null)
            throw new NullPointerException("illegal null input");

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return ("SecurityPrincipal:  " + name);
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this == o)
            return true;

        if (!(o instanceof SofunPrincipal))
            return false;
        SofunPrincipal that = (SofunPrincipal) o;

        if (this.getName().equals(that.getName()))
            return true;
        return false;
    }
    
    public int hashCode() {
        return name.hashCode();
    }

}
