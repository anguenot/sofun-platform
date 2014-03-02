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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.SportCategory;

/**
 * Sport implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports")
public class SportImpl implements Sport {

    private static final long serialVersionUID = -2551940328814141262L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    protected long id;

    @Column(name = "uuid", unique = true, nullable = true)
    protected long uuid;

    @Column(name = "name", unique = true, nullable = false)
    protected String name;

    @ManyToMany(
            targetEntity = SportCategoryImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "sports_categories_sports", joinColumns = { @JoinColumn(
            name = "sport_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "sport_category_id",
            referencedColumnName = "id") })
    protected List<SportCategory> categories;

    public SportImpl() {
        super();
    }

    public SportImpl(String name) {
        this.name = name;
    }

    public SportImpl(long uuid) {
        this.uuid = uuid;
    }

    public SportImpl(String name, long uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<SportCategory> getCategories() {
        return categories;
    }

    @Override
    public long getUUID() {
        return uuid;
    }

    @Override
    public void setId(long id) {
        this.id = id;
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
    public void setCategories(List<SportCategory> categories) {
        this.categories = categories;
    }

    @Override
    public void addCategory(SportCategory category) {
        if (category != null) {
            if (getCategories() == null) {
                List<SportCategory> cats = new ArrayList<SportCategory>();
                cats.add(category);
                setCategories(cats);
            } else {
                if (!getCategories().contains(category)) {
                    getCategories().add(category);
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Sport) {
            Sport s = (Sport) obj;
            if (s.getUUID() != 0 && getUUID() != 0) {
                return s.getUUID() == getUUID() ? true : false;
            } else if (s.getId() != 0 && getId() != 0) {
                return s.getId() == getId() ? true : false;
            } else if (s.getName() != null && getName() != null) {
                return s.getName().equals(getName()) ? true : false;
            }
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getId()).hashCode();
    }

}
