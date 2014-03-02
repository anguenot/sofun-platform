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

package org.sofun.platform.facebook.api;

import java.io.Serializable;

import org.sofun.core.api.member.Member;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface FacebookService extends Serializable {

    Member updateMemberInfoFromFacebook(Member member) throws Exception;

    void addMemberLike(Member member, String like) throws Exception;

    FacebookMemberLike getMemberLike(Member member) throws Exception;
    
    void addMemberFriend(Member member, String friend) throws Exception;
    
    FacebookMemberFriend getMemberFriends(Member member) throws Exception;

    boolean doesMemberLike(Member member, String pageId) throws Exception;
    
    FacebookMemberInfo getFacebookMemberInfo(Member member);

}
