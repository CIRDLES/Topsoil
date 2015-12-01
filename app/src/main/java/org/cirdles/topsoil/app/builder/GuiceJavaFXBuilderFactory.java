/*
 * Copyright 2015 CIRDLES.
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
package org.cirdles.topsoil.app.builder;

import com.google.inject.Injector;
import com.google.inject.Key;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by johnzeringue on 11/30/15.
 */
public class GuiceJavaFXBuilderFactory implements BuilderFactory {

    private final Injector injector;
    private final BuilderFactory javaFXBuilderFactory;

    @Inject
    public GuiceJavaFXBuilderFactory(
            Injector injector,
            @Named("JavaFX") BuilderFactory javaFXBuilderFactory) {
        this.injector = injector;
        this.javaFXBuilderFactory = javaFXBuilderFactory;
    }

    @Override
    public Builder<?> getBuilder(Class<?> type) {
        Builder<?> result;

        if (injector.getExistingBinding(Key.get(type)) == null) {
            result = javaFXBuilderFactory.getBuilder(type);
        } else {
            result = injector.getProvider(type)::get;
        }

        return result;
    }

}
