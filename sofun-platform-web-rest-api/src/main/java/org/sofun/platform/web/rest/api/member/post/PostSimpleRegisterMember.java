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

import java.util.Map;


/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class PostSimpleRegisterMember extends AbstractRegisterMember {

    private static final long serialVersionUID = 1036229066161902807L;
   
    public PostSimpleRegisterMember(Map<String, String> params) {
        super(params);
    }
    
    @Override
    public boolean verify() {
       // XXX implement verification here. Application level responsibility to to this right now
       return true;
    }

}
