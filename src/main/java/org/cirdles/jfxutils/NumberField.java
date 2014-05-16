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
package org.cirdles.jfxutils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import static java.lang.Math.*;

/**
 * A useful type of TextField where the field is associated with a DoubleProperty at instantiation and the necessary
 * listeners are put in place to keep the text and the value of the DoubleProperty in sync. That is, if either the text
 * in the field or the value of the DoubleProperty change, the other is updated to match it.
 *
 * Note that this class should not be used in conjunction with values that would be better represented by an
 * IntegerProperty or a LongProperty. Examples of these include indices and counts, since such values are more precisely
 * represented by binary integers. In such a case, a modification of this class should be used or made.
 *
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class NumberField extends TextField {

    /**
     * The DoubleProperty linked with this field
     */
    private final DoubleProperty doubleProperty;

    /**
     * Creates a new NumberField that is linked to the given DoubleProperty.
     *
     * @param displayValue the DoubleProperty to sync with this field
     * @param relatedValue
     */
    public NumberField(DoubleProperty displayValue, ObservableValue<Number> relatedValue) {
        super(displayValue.getValue().toString());
        this.doubleProperty = displayValue;

        /*
         * Add a ChangeListener to this field's textProperty so that the associated DoubleProperty is changed whenever
         * this field's text changes.
         */
        textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    try {
                        displayValue.set(Double.valueOf(newValue));
                    } catch (NumberFormatException ex) {
                        /*
                         * Do nothing. newValue must not be able to be parsed as a double.
                         *
                         * There's a "better" way to check if newValue is parsable by Double.valueOf that is outlined at
                         * http://download.java.net/jdk8/docs/api/java/lang/Double.html#valueOf-java.lang.String- but
                         * it's simpler to catch the exception unless serious drawbacks to this approach are found.
                         */
                    }
                });

        /*
         * Add a ChangeListener to this field's associated DoubleProperty to change this field's text whenever the
         * associated DoubleProperty is changed.
         */
        displayValue.addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    // calculates the optimal number of decimal places to display to the user based on the magnitude of
                    // the related value
                    int decimalPlaces = (int) abs(min(0, log10(relatedValue.getValue().doubleValue()) - 3));
                    
                    setText(String.format("%." + decimalPlaces + "f", newValue));
                });
    }

    /**
     * Returns the DoubleProperty that is linked with this field.
     *
     * @return this field's DoubleProperty
     */
    public DoubleProperty doubleProperty() {
        return doubleProperty;
    }
}
