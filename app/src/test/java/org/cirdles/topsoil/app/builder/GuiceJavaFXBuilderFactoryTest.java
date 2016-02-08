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

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Provider;
import javafx.util.BuilderFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by johnzeringue on 12/1/15.
 */
public class GuiceJavaFXBuilderFactoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Injector injector;

    @Mock
    private Binding<Object> binding;

    @Mock
    private Provider<Object> provider;

    @Mock
    private BuilderFactory javaFXBuilderFactory;

    private BuilderFactory builderFactory;

    @Before
    public void setUp() {
        builderFactory = new GuiceJavaFXBuilderFactory(
                injector,
                javaFXBuilderFactory);
    }

    @Test
    public void testGetBuilderForGuiceClass() {
        when(injector.getExistingBinding(any())).thenReturn(binding);
        when(injector.getProvider(Object.class)).thenReturn(provider);

        builderFactory.getBuilder(Object.class);

        verify(injector).getExistingBinding(any());
        verify(injector).getProvider(Object.class);
        verifyNoMoreInteractions(injector, javaFXBuilderFactory);
    }

    @Test
    public void testGetBuilderForJavaFXClass() {
        builderFactory.getBuilder(Object.class);

        verify(injector).getExistingBinding(any());
        verify(javaFXBuilderFactory).getBuilder(Object.class);
        verifyNoMoreInteractions(injector, javaFXBuilderFactory);
    }

}
