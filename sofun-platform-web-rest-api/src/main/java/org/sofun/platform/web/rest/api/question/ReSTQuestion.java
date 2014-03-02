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

package org.sofun.platform.web.rest.api.question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sofun.core.api.question.Question;
import org.sofun.core.api.question.QuestionKupTiebreaker;

/**
 * Question API.
 * 
 * <p>
 * 
 * Used for both prediction' and kup's tiebreaker questions.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    protected long id;

    protected String label;

    protected List<String> choices = new ArrayList<String>();

    public ReSTQuestion() {
    }

    public ReSTQuestion(Question coreQuestion) {
        this();
        if (coreQuestion != null) {
            this.setLabel(coreQuestion.getLabel());
            this.setChoices(coreQuestion.getChoices());
            this.setId(coreQuestion.getId());
        }
    }

    public ReSTQuestion(QuestionKupTiebreaker coreQuestion) {
        this();
        if (coreQuestion != null) {
            this.setLabel(coreQuestion.getLabel());
            this.setChoices(coreQuestion.getChoices());
            this.setId(coreQuestion.getId());
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
