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

import javafx.beans.property.Property;
import javafx.scene.control.ChoiceBox;

/**
 * A ChoiceBox that binds zero or more properties in its constructor to itself,
 * so that whenever its selection changes, all properties bound to it also
 * change.
 *
 * @param <T> the type of this ChoiceBox's items and the type of the properties
 * bound to it
 */
public class BoundChoiceBox<T> extends ChoiceBox<T> {

    public BoundChoiceBox(Property<T>... properties) {
        for (Property<T> property : properties) {
            property.bind(getSelectionModel().selectedItemProperty());
        }
    }
}
