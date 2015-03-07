/*
 * Copyright 2014 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.topsoil.app.table;

import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.util.StringConverter;

/**
 *
 * @author CIRDLES
 * @param <T>
 */
public abstract class Field<T> {

    private static int numberOfFields = 0;

    private final int id;
    private final StringProperty name = new StringPropertyBase() {

        @Override
        public Object getBean() {
            return Field.this;
        }

        @Override
        public String getName() {
            return "name";
        }
    };

    public Field() {
        id = numberOfFields;
        incrementNumberOfFields();
    }

    public Field(String name) {
        this();
        this.name.setValue(name);
    }

    /**
     * @return the id
     */
    int getID() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name.get();
    }
    
    public abstract StringConverter<T> getStringConverter();

    public StringProperty nameProperty() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name.set(name);
    }

    @Override
    public boolean equals(Object that) {
        if (that == null || !(that instanceof Field)) {
            return false;
        }

        return this.getID() == ((Field) that).getID();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + this.id;
        return hash;
    }
    
    // makes FindBugs happy
    private static void incrementNumberOfFields() {
        ++numberOfFields;
    }
}
