/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.topsoil.data;

import java.util.List;

/**
 *
 * @author John Zeringue
 */
public class SimpleDataset implements Dataset {
 
    private final List<Field> fields;
    private final List<Entry> entries;
    
    private String name;

    public SimpleDataset(List<Field> fields, List<Entry> entries) {
        this.fields = fields;
        this.entries = entries;
        
        name = "";
    }

    @Override
    public List<Field> getFields() {
        return fields;
    }

    @Override
    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
