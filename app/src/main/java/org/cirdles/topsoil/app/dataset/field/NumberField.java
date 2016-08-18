/*
 * Copyright 2014 zeringuej.
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

package org.cirdles.topsoil.app.dataset.field;

import static java.lang.Double.NaN;
import javafx.util.StringConverter;

/**
 *
 * @author zeringuej
 */
public class NumberField extends BaseField implements Field {

    public NumberField(String name) {
        super(name);
    }

    @Override
    public StringConverter<Double> getStringConverter() {
        return new StringConverter<Double>() {

            @Override
            public String toString(Double number) {
                if (number == null) {
                    return "---";
                }

                return number.toString();
            }

            @Override
            public Double fromString(String string) {

                //Check for valid numbers
                try {
                    return Double.valueOf(string);
                } catch (NumberFormatException e) {
                    return NaN;
                }
            }

        };
    }

}
