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
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 *
 * @author pfif
 */
public class NumberField extends TextField {
    StringConverter<Number> converter;
    Property<Number> target;
    
    
    public NumberField(StringConverter<Number> converter_arg){
        converter = converter_arg;
    }
    
    public NumberField(Property<Number> targetProperty, StringConverter<Number> converter_arg){
        converter = converter_arg;
        target = targetProperty;
        bindTargetThroughConverter();
    }
    
    private void bindTargetThroughConverter(){
        if(converter != null && target != null){
            Bindings.bindBidirectional(textProperty(), target, converter);
        }
    }
    
    private void unbindTarget(){
        if(target != null) textProperty().unbindBidirectional(target);
    }
    
    public void setConverter(StringConverter<Number> conveter_arg){
        unbindTarget();
        converter = conveter_arg;
        bindTargetThroughConverter();
    }
    
    public void setTarget(Property<Number> property){
        unbindTarget();
        target = property;
        bindTargetThroughConverter();
    }
}
