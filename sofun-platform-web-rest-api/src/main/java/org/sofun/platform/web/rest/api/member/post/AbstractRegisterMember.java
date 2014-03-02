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

package org.sofun.platform.web.rest.api.member.post;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 *
 */
public abstract class AbstractRegisterMember implements PostRegisterMember {

    private static final long serialVersionUID = -8546957823964096829L;
    
    protected Map<String, String> properties = new HashMap<String, String>();

    public AbstractRegisterMember(Map<String, String> params) {
        this.properties = params;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
    
}
