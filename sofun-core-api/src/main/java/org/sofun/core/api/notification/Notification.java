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

package org.sofun.core.api.notification;

import java.util.HashSet;
import java.util.Set;

/**
 * Sofun Platform list of notifications.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class Notification {

    //
    // MANDATORY NOTIFICATIONS
    //

    /**
     * Confirmation upon simple account creation
     */
    public static final String ACCOUNT_CONFIRM_SIMPLE_CREATE = "ACCOUNT_CONFIRM_SIMPLE_CREATE";

    /**
     * Confirmation upon French gambling account creation
     */
    public static final String ACCOUNT_CONFIRM_GAMBLING_FR_CREATE = "ACCOUNT_CONFIRM_GAMBLING_FR_CREATE";

    /**
     * Confirmation that mandatory papers for the Frenc gambling account have been recieved
     */
    public static final String ACCOUNT_CONFIRM_GAMBLING_FR_FILES_RECIEVED = "ACCOUNT_CONFIRM_GAMBLING_FR_FILES_RECIEVED";

    /**
     * Confirmation upon French gambling account activation
     */
    public static final String ACCOUNT_CONFIRM_GAMBLING_FR_ACTIVATION_KEY = "ACCOUNT_CONFIRM_GAMBLING_FR_ACTIVATION_KEY";

    /**
     * Confirmation upon member credit transaction
     */
    public static final String ACCOUNT_CONFIRM_MEMBER_CREDIT = "ACCOUNT_CONFIRM_MEMBER_CREDIT";

    /**
     * Confirmation upon member wire transaction
     */
    public static final String ACCOUNT_CONFIRM_MEMBER_WIRE = "ACCOUNT_CONFIRM_MEMBER_WIRE";

    /**
     * Confirmation upon team creation or deletion
     */
    public static final String TEAM_CREATE_DELETE = "TEAM_CREATE_DELETE";

    /**
     * Confirmation that member joined or leaved TEAM
     */
    public static final String TEAM_JOIN_LEAVE = "TEAM_JOIN_LEAVE";

    /**
     * Confirm the registration of a Team to a Kup
     */
    public static final String TEAM_KUP_CONFIRM_REGISTRATION = "TEAM_KUP_CONFIRM_REGISTRATION";

    /**
     * Confirm a Member he is a participant in a Kup
     */
    public static final String KUP_CONFIRM_PARTICIPATE = "KUP_CONFIRM_PARTICIPATE";

    /**
     * Confirm the end of a Kup
     */
    public static final String KUP_CONFIRM_END = "KUP_CONFIRM_END";

    /**
     * Confirm Member winnings in Kup
     */
    public static final String KUP_CONFIRM_WINNINGS = "KUP_CONFIRM_WINNINGS";

    //
    // OPTIONAL NOTIFICATIONS
    //

    /**
     * A friend just started participating to a Kup I am a participating to.
     */
    public static final String KUP_FRIEND_PARTICIPATE = "KUP_FRIEND_PARTICIPATE";

    /**
     * New results are available in one my Kup.
     */
    public static final String KUP_NEW_RESULTS = "KUP_NEW_RESULTS";

    /**
     * Remind me to predict in Kup.
     */
    public static final String KUP_PREDICTION_REMINDER = "KUP_PREDICTION_REMINDER";

    /**
     * Kup ranking has been updated.
     */
    public static final String KUP_RANKING_UPDATED = "KUP_RANKING_UPDATED";

    /**
     * An invite has been accepted in one of my teams.
     */
    public static final String TEAM_ADMIN_INVITE_ACCEPTED = "TEAM_ADMIN_INVITE_ACCEPTED";

    /**
     * A new member joined one of my teams.
     */
    public static final String TEAM_ADMIN_MEMBER_JOINED = "TEAM_ADMIN_MEMBER_JOINED";

    /**
     * A new comment has been posted in one of my teams.
     */
    public static final String TEAM_ADMIN_NEW_COMMENT = "TEAM_ADMIN_NEW_COMMENT";

    /**
     * New Kup is available in my team.
     */
    public static final String TEAM_MEMBER_NEW_KUP = "TEAM_MEMBER_NEW_KUP";

    /**
     * A new Kup is available at Betkup level.
     */
    public static final String BETKUP_NEW_KUP = "BETKUP_NEW_KUP";

    /**
     * A new Team is available at Betkup level.
     */
    public static final String BETKUP_NEW_TEAM = "BETKUP_NEW_TEAM";

    /**
     * Betkup newsletter.
     */
    public static final String BETKUP_NEWSLETTER = "BETKUP_NEWSLETTER";

    /**
     * Betkup partner newsletter.
     */
    public static final String BETKUP_PARTNERS_NEWSLETTER = "BETKUP_PARTNERS_NEWSLETTER";

    /**
     * Returns all mandatory notifications.
     * 
     * 
     * @see {@link Notification} for constants.
     * @return a list of {@link String}.
     */
    public static final Set<String> getMandatoryNotifications() {

        Set<String> mandatory_notifications = new HashSet<String>();

        mandatory_notifications.add(Notification.ACCOUNT_CONFIRM_SIMPLE_CREATE);
        mandatory_notifications.add(Notification.ACCOUNT_CONFIRM_GAMBLING_FR_CREATE);
        mandatory_notifications.add(Notification.ACCOUNT_CONFIRM_GAMBLING_FR_FILES_RECIEVED);
        mandatory_notifications.add(Notification.ACCOUNT_CONFIRM_GAMBLING_FR_ACTIVATION_KEY);
        mandatory_notifications.add(Notification.ACCOUNT_CONFIRM_MEMBER_CREDIT);
        mandatory_notifications.add(Notification.ACCOUNT_CONFIRM_MEMBER_WIRE);

        mandatory_notifications.add(Notification.TEAM_CREATE_DELETE);
        mandatory_notifications.add(Notification.TEAM_JOIN_LEAVE);
        mandatory_notifications.add(Notification.TEAM_KUP_CONFIRM_REGISTRATION);

        mandatory_notifications.add(Notification.KUP_CONFIRM_END);
        mandatory_notifications.add(Notification.KUP_CONFIRM_PARTICIPATE);
        mandatory_notifications.add(Notification.KUP_CONFIRM_WINNINGS);

        return mandatory_notifications;

    }

    /**
     * Returns the list of optional notifications.
     * 
     * @see {@link Notification} for constants.
     * @return a list of {@link String}
     */
    public static final Set<String> getOptionalNotifications() {

        Set<String> optional_notifications = new HashSet<String>();

        optional_notifications.add(Notification.KUP_FRIEND_PARTICIPATE);
        optional_notifications.add(Notification.KUP_NEW_RESULTS);
        optional_notifications.add(Notification.KUP_PREDICTION_REMINDER);
        optional_notifications.add(Notification.KUP_RANKING_UPDATED);

        optional_notifications.add(Notification.TEAM_ADMIN_INVITE_ACCEPTED);
        optional_notifications.add(Notification.TEAM_ADMIN_MEMBER_JOINED);
        optional_notifications.add(Notification.TEAM_ADMIN_NEW_COMMENT);

        optional_notifications.add(Notification.TEAM_MEMBER_NEW_KUP);

        optional_notifications.add(Notification.BETKUP_NEW_KUP);
        optional_notifications.add(Notification.BETKUP_NEW_TEAM);
        optional_notifications.add(Notification.BETKUP_NEWSLETTER);
        optional_notifications.add(Notification.BETKUP_PARTNERS_NEWSLETTER);

        return optional_notifications;
    }

}
