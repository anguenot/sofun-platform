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

 * @version $Id: Tournament.java 3877 2014-03-02 07:24:54Z anguenot $
 */

package org.sofun.core.api.sport.tournament;

import java.io.Serializable;
import java.util.List;

import org.sofun.core.api.sport.Sport;

/**
 * Tournament.
 * 
 * <p/>
 * This interface defines a tournament. Example of tournaments: UEFA Word Cup, France League 1, etc...
 * <p/>
 * 
 * @see {@link TournamentSeason} for and instance of a tournament. For instance, UEFA World Cup season 2010.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface Tournament extends Serializable {

    /**
     * Returns the internal DB identifier.
     * 
     * @return {@link Long}
     */
    long getId();

    /**
     * Sets the internal DB identifier.
     * 
     * @param id: {@link Long}
     */
    void setId(long id);

    /**
     * Returns the tournament UUID
     * 
     * @return {@link Long}
     */
    long getUUID();

    /**
     * Sets the tournament UUID.
     * 
     * @param uuid: {@link Long}
     */
    void setUUID(long uuid);

    /**
     * Returns the tournament name.
     * 
     * @return {@link String} not null
     */
    String getName();

    /**
     * Sets the tournament name.
     * 
     * @param name: a {@link String} not null
     */
    void setName(String name);

    /**
     * Returns the tournament code.
     * 
     * @return a {@link String} or null
     */
    String getCode();

    /**
     * Sets the tournament code.
     * 
     * @param code: a {@link String}
     */
    void setCode(String code);

    /**
     * Returns the list of sports this tournament is related to.
     * 
     * @return a {@link List} of {@link Sport}
     */
    List<Sport> getSports();

    /**
     * Sets the list of sports this tournament is related to.
     * 
     * @param sports: a {@link List} of {@link Sport}
     */
    void setSports(List<Sport> sports);

    /**
     * Adds a sport if it does not exist.
     * 
     * @param sport: a {@link Sport}
     */
    void addSport(Sport sport);

    /**
     * Returns the tournament seasons.
     * <p />
     * For instance, 2010/2011, 2011/2012. A tournament can actually occur several times. (Per year, per month, etc...)
     * 
     * @return a {@link List} of {@link TournamentSeason}
     */
    List<TournamentSeason> getSeasons();

    /**
     * Sets the tournament seasons.
     * <p />
     * For instance, 2010/2011, 2011/2012. A tournament can actually occur several times. (Per year, per month, etc...)
     * 
     * @return a {@link List} of {@link TournamentSeason}
     */
    void setSeasons(List<TournamentSeason> seasons);

    /**
     * Adds a tournament season if it does not exist already.
     * 
     * @param season: a {@link TournamentSeason}
     */
    void addSeason(TournamentSeason season);

}
