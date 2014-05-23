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
package org.cirdles.topsoil.builder;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

/**
 *
 * @author zeringuej
 */
public class TopsoilBuilderFactory implements BuilderFactory {

    private final String packageName = this.getClass().getPackage().getName();
    private final BuilderFactory defaultBuilderFactory = new JavaFXBuilderFactory();

    @Override
    public Builder<?> getBuilder(Class<?> type) {
        try {
            return (Builder) Class.forName(builderNameForClass(type)).newInstance();
        } catch (ClassNotFoundException ex) {
            return defaultBuilderFactory.getBuilder(type);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(TopsoilBuilderFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private String builderNameForClass(Class type) {
        return String.format("%s.%sBuilder", packageName, type.getSimpleName());
    }
}
