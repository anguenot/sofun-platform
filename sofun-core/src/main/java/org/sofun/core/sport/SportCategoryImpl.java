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

package org.sofun.core.sport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.sofun.core.api.sport.SportCategory;

/**
 * Sport Category Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_categories")
public class SportCategoryImpl implements SportCategory {

    private static final long serialVersionUID = 7194426624376114241L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @Column(name = "uuid", unique = true, nullable = true)
    protected long uuid;

    @Column(name = "name", unique = false, nullable = false)
    protected String name;

    public SportCategoryImpl() {

    }

    public SportCategoryImpl(String name) {
        this();
        this.name = name;
    }

    public SportCategoryImpl(long uuid) {
        this();
        this.uuid = uuid;
    }

    public SportCategoryImpl(String name, long uuid) {
        this(name);
        this.uuid = uuid;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(long uuid) {
        this.uuid = uuid;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof SportCategory) {
            SportCategory c = (SportCategory) obj;
            if (c.getUUID() != 0 && getUUID() != 0) {
                return c.getUUID() == getUUID() ? true : false;
            } else if (c.getId() != 0 && getId() != 0) {
                return c.getId() == getId() ? true : false;
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getId()).hashCode();
    }

}
