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

 * @version $Id: UserAddress.java 45 2010-11-11 06:51
 */

package org.sofun.core.api.member;

import java.io.Serializable;

/**
 * org.sofun.core.api.userf="mailto:julien@anguenot.org">Julien
 * Anguenot</a>
 * 
 */
public interface MemberPostalAddress extends Serializable {

    String getStreet();

    void setStreet(String street);

    String getCity();

    void setCity(String city);

    String getState();

    void setState(String state);

    String getZipCode();

    void setZip(String zip);

    String getCountry();

    void setCountry(String country);

}
