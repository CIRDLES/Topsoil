/*
 * Copyright 2014 pfif.
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

package org.cirdles.jfxutils;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 *
 * @author pfif
 * @param <T>
 */
public abstract class ConverterField<T> extends TextField {
    
    private final Property<T> converted;
    
    public Property<T> convertedProperty() {
        return converted;
    }
    
    public T getConverted() {
        return converted.getValue();
    }
    
    public void setConverted(T value) {
        converted.setValue(value);
    }

    /**
     * Create a new number field that uses the given converter to equate its text contents to a decimal number.
     * 
     * @param converted
     * @param converter 
     */
    public ConverterField(Property<T> converted, StringConverter<T> converter) {
        this.converted = converted;
        Bindings.bindBidirectional(textProperty(), this.converted, converter);
        setAlignment(Pos.CENTER_RIGHT);
    }
}
