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
package org.cirdles.topsoil.app.plot;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.dataset.field.NumberField;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 *
 * @author John Zeringue
 */
public class VariableBindingViewTest extends ApplicationTest {

    private static final List<Variable> oneVariable = Arrays.asList(
            new IndependentVariable("Test Variable")
    );

    private static final List<Field> oneField = Arrays.asList(
            new NumberField("Test Field")
    );

    private static final List<Variable> twoVariables = Arrays.asList(
            new IndependentVariable("Test Variable 1"),
            new IndependentVariable("Test Variable 2")
    );

    private static final List<NumberField> twoFields = Arrays.asList(
            new NumberField("Test Variable 1"),
            new NumberField("Test Variable 2")
    );

    private Pane parent;

    @Override
    public void start(Stage stage) throws Exception {
        parent = new Pane();
        Scene scene = new Scene(parent);

        stage.setScene(scene);
        stage.show();
    }

    private void testVariableBindingView(
            List<? extends Variable> variables,
            List<? extends Field> fields) {

        RunnableFuture<Void> test = new FutureTask<Void>(() -> {
            parent.getChildren().add(new VariableBindingView(variables, fields));
        }, null);

        Platform.runLater(test);

        try {
            test.get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void testInitialization() {
        testVariableBindingView(oneVariable, oneField);
    }

    @Test
    public void testMoreFieldsThanVariables() {
        testVariableBindingView(oneVariable, twoFields);
    }

    @Test
    public void testMoreVariablesThanFields() {
        testVariableBindingView(twoVariables, oneField);
    }

}
