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

package org.sofun.core.question;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.sofun.core.api.question.QuestionGame;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.sport.tournament.TournamentGameImpl;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "question_games")
@Inheritance(strategy = InheritanceType.JOINED)
public class QuestionGameImpl extends QuestionImpl implements QuestionGame {

    private static final long serialVersionUID = -449873296025499915L;

    @ManyToMany(targetEntity = TournamentGameImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "questions_games", joinColumns = { @JoinColumn(name = "question_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "game_id", referencedColumnName = "id") })
    protected List<TournamentGame> games;

    @Override
    public List<TournamentGame> getTournamentGames() {
        if (games == null) {
            games = new ArrayList<TournamentGame>();
        }
        return games;
    }

    @Override
    public void setTournamentGames(List<TournamentGame> games) {
        this.games = games;
    }

}
