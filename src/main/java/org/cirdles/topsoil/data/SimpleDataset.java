/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.topsoil.data;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author John Zeringue
 */
public class SimpleDataset implements Dataset {
 
    private final List<Field<?>> fields;
    private final List<Entry> entries;
    
    private Optional<String> name = Optional.empty();

    public SimpleDataset(List<Field<?>> fields, List<Entry> entries) {
        this.fields = fields;
        this.entries = entries;
    }

    @Override
    public Optional<String> getName() {
        return name;
    }

    @Override
    public List<Field<?>> getFields() {
        return fields;
    }

    @Override
    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = Optional.ofNullable(name);
    }

}
