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

package org.sofun.core;

/**
 * Core Constants.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class CoreConstants {

    public static final String VERSION = "1.0";

    public static final String POWERED_BY = "Sofun Platform";

    public static final String COPYRIGHT = "(c) 2011-2012 Sofun Gaming SAS ";

    public static final String COPYRIGHT_URL = "http://www.sofungaming.com";

    public static final String SOFUN_EMAIL_CONTACT = "contact@sofungaming.com";

    public static final String SOFUN_MAIL_FROM = "noreply@sofungaming.com";

    public static final String PERSISTENCE_UNIT = "SofunCore";

    public static final String STATIC_RESOURCES_PROTOCOL = "http://";

    public static final String STATIC_RESOURCES_DOMAIN = "static.sofungaming.com";

    public static final String API_DOMAIN = "api.sofungaming.com";

    public static final String API_PROTOCOL = "https://";

    public static final int TIME_TO_BET_BEFORE_EVENT_STARTS = 300;

    public static final int BETKUP_FR_COMMUNITY_ID = 1;

    public static final int SIMPLE_ACCOUNT_MIN_AGE = 18;

    public static final int GAMBLING_FR_ACCOUNT_MIN_AGE = 18;

    /*
     * This is the *platform* oauth token. Not the user session (see external
     * client such as betkup.fr for session's management)
     */
    public static final int SESSION_TIMEOUT = 86400;

    public static final int TOKEN_TIMEOUT = 3600;

    public static final int PASSWORD_RESET_EXPIRATION = 1800;

}
