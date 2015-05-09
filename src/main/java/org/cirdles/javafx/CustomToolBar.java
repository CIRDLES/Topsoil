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
package org.cirdles.javafx;

import java.util.function.Consumer;
import javafx.scene.control.ToolBar;

/**
 * This class allows subclasses to easily inherit the initialization behavior
 * necessary to create a custom JavaFX component, in this case a {@link ToolBar}.
 *
 * @author John Zeringue
 * @param <T> the type of {@link CustomToolBar} to be instantiated
 */
public abstract class CustomToolBar<T extends CustomToolBar> extends ToolBar {

    private static final CustomComponentInitializer initializer
            = new CustomComponentInitializer();

    /**
     * Creates a new {@link CustomToolBar} that has been initialized by a
     * {@link CustomComponentInitializer}.
     */
    public CustomToolBar() {
        initializer.initialize(this);
    }

    /**
     * Creates a new {@link CustomToolBar} that has been initialized by a
     * {@link CustomComponentInitializer} after having run the given
     * preinitialization step. The preinitialization {@link Consumer} is passed
     * the new object as its argument.
     *
     * This is useful for allowing subclasses to prepare instance variables
     * before their initialize method is called by the
     * {@link CustomComponentInitializer}'s {@link FXMLLoader}.
     *
     * @param preinitialization the preinitialization to be performed on the new
     * object
     */
    public CustomToolBar(Consumer<T> preinitialization) {
        preinitialization.accept((T) this);
        initializer.initialize(this);
    }

}
