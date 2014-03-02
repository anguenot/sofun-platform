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

package org.sofun.core.api.question;

import java.io.Serializable;
import java.util.List;

/**
 * Question
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 *
 */
public interface Question extends Serializable {
    
    long getId();
    
    String getLabel();
    
    void setLabel(String label);
    
    List<String> getChoices();
    
    void setChoices(List<String> choices);

}
