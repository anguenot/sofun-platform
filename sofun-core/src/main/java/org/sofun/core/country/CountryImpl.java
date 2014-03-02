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

package org.sofun.core.country;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.sofun.core.api.country.Country;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.sport.SportContestantImpl;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "countries")
public class CountryImpl implements Country {

    private static final long serialVersionUID = 1300776137563602319L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "name", unique = false, nullable = false)
    protected String name;

    @Column(name = "ioc", unique = false, nullable = false)
    protected String ioc;

    @Column(name = "iso", unique = false, nullable = false)
    protected String iso;

    @OneToMany(
            targetEntity = SportContestantImpl.class,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "country")
    protected List<SportContestant> contestants;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(name = "countries_properties", joinColumns = @JoinColumn(
            name = "id"))
    protected Map<String, String> properties = new HashMap<String, String>();

    public CountryImpl() {
    }

    public CountryImpl(String name, String ioc, String iso) {
        this.name = name;
        this.ioc = ioc;
        this.iso = iso;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getIOC() {
        return ioc;
    }

    @Override
    public void setIOC(String ioc) {
        this.ioc = ioc;
    }

    @Override
    public String getISO() {
        return iso;
    }

    @Override
    public void setISO(String iso) {
        this.iso = iso;
    }

    @Override
    public List<SportContestant> getContestants() {
        return contestants;
    }

    @Override
    public void setContestants(List<SportContestant> contestants) {
        this.contestants = contestants;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public void clear() {
        getProperties().clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return getProperties().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getProperties().containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        return getProperties().entrySet();
    }

    @Override
    public String get(Object key) {
        return getProperties().get(key);
    }

    @Override
    public boolean isEmpty() {
        return getProperties().isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return getProperties().keySet();
    }

    @Override
    public String put(String key, String value) {
        return getProperties().put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        getProperties().putAll(m);

    }

    @Override
    public String remove(Object key) {
        return getProperties().remove(key);
    }

    @Override
    public int size() {
        return getProperties().size();
    }

    @Override
    public Collection<String> values() {
        return getProperties().values();
    }

}
