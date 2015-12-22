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

import java.util.Arrays;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.plot.IndependentVariable;
import org.cirdles.topsoil.plot.Variable;
import org.cirdles.topsoil.dataset.field.Field;
import org.cirdles.topsoil.dataset.field.NumberField;
import org.junit.Test;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 *
 * @author John Zeringue
 */
public class VariableBindingControlTest extends ApplicationTest {

    private final Variable variable = new IndependentVariable("Test Variable");

    private final List<Field> fields
            = Arrays.asList(new NumberField("Test Field"));

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new VariableBindingControl(variable, fields));
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testVariableNameVisibleAsLabelText() {
        verifyThat("#variableNameLabel", hasText(variable.getName()));
    }

    @Test
    public void testFieldChoiceBoxSelectsDefault() {
        verifyThat("#fieldChoiceBox", (ChoiceBox choiceBox) -> {
            return !choiceBox.getSelectionModel().isEmpty();
        });
    }

    @Test
    public void testHidesVariableFormatsIfOnlyOne() {
        verifyThat("#variableFormatChoiceBox", isInvisible());
    }

}
