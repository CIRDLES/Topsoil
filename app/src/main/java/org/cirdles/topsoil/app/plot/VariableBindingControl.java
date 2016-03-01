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

import com.johnzeringue.extendsfx.layout.CustomHBox;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import org.cirdles.topsoil.app.dataset.field.Field;

import java.util.List;

/**
 *
 * @author John Zeringue
 * @param <T> the variable type
 */
public class VariableBindingControl<T> extends CustomHBox<VariableBindingControl<T>> {

    private Variable variable;
    private List<Field<T>> fields;

    @FXML
    private Label variableNameLabel;
    @FXML
    private ChoiceBox<Field<T>> fieldChoiceBox;
    @FXML
    private ChoiceBox<VariableFormat<T>> variableFormatChoiceBox;

    public VariableBindingControl(Variable variable, List<Field<T>> fields) {
        super(self -> {
            self.variable = variable;
            self.fields = fields;
        });
    }

    private void initializeVariableFormatChoiceBox() {
        if (variable.getFormats().size() <= 1) {
            variableFormatChoiceBox.setVisible(false);
        }

        variableFormatChoiceBox.setConverter(new StringConverter<VariableFormat<T>>() {

            @Override
            public String toString(VariableFormat<T> variableFormat) {
                return variableFormat.getName();
            }

            @Override
            public VariableFormat<T> fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        });
        variableFormatChoiceBox.getItems().setAll(variable.getFormats());
        variableFormatChoiceBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void initialize() {
        variableNameLabel.setText(variable.getName());

        fieldChoiceBox.setConverter(new StringConverter<Field<T>>() {

            @Override
            public String toString(Field<T> field) {
                return field.getName();
            }

            @Override
            public Field<T> fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        });
        fieldChoiceBox.getItems().setAll(fields);
        fieldChoiceBox.getSelectionModel().selectFirst();

        initializeVariableFormatChoiceBox();
    }

    public Variable<T> getVariable() {
        return variable;
    }

    public Field<T> getFieldSelection() {
        return fieldChoiceBox.getSelectionModel().getSelectedItem();
    }

    public void setFieldSelection(Field<T> field) {
        fieldChoiceBox.getSelectionModel().select(field);
    }

    public VariableFormat<T> getVariableFormatSelection() {
        return variableFormatChoiceBox.getSelectionModel().getSelectedItem();
    }

    public void setVariableFormatSelection(VariableFormat<T> variableFormat) {
        variableFormatChoiceBox.getSelectionModel().select(variableFormat);
    }

}
