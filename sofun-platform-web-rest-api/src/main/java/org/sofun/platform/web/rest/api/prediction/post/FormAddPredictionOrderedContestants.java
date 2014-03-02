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

package org.sofun.platform.web.rest.api.prediction.post;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class FormAddPredictionOrderedContestants implements Serializable {

    private static final long serialVersionUID = 654451724029489108L;

    protected String memberEmail;

    protected List<Long> contestants = new ArrayList<Long>();

    public FormAddPredictionOrderedContestants() {
        super();
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public List<Long> getContestants() {
        return contestants;
    }

    public void setContestants(List<Long> contestants) {
        this.contestants = contestants;
    }

}
