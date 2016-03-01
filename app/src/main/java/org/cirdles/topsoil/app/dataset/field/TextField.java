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

import javafx.util.StringConverter;

/**
 *
 * @author zeringuej
 */
public class TextField extends BaseField<String> {

    public TextField(String name) {
        super(name);
    }

    @Override
    public StringConverter<String> getStringConverter() {
        return new StringConverter<String>() {

            @Override
            public String toString(String string) {
                return string;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        };
    }

}
