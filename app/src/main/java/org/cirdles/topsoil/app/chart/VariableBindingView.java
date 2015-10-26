/*
 * Copyright 2014 zeringue.
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
package org.cirdles.topsoil.app.chart;

import com.johnzeringue.extendsfx.layout.CustomVBox;
import javafx.fxml.FXML;
import org.cirdles.commons.string.LevenshteinDistance;
import org.cirdles.topsoil.chart.Variable;
import org.cirdles.topsoil.dataset.field.Field;

import java.util.List;

import static java.lang.Math.min;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * This UI element is used by the user to choose which column in the main table
 * determine which value of an ellipse.
 *
 * @param <T>
 */
public class VariableBindingView<T> extends CustomVBox<VariableBindingView<T>> {

    private List<Variable<T>> variables;
    private List<Field<T>> fields;

    public VariableBindingView(List<Variable<T>> variables, List<Field<T>> fields) {
        super(self -> {
            self.variables = variables;
            self.fields = fields;
        });
    }

    private int controlIndex() {
        return getChildren().size();
    }

    private void initializeControl(Variable<T> variable) {
        VariableBindingControl control
                = new VariableBindingControl(variable, fields);

        // set field by index

        // select the corresponding field index unless there aren't enough
        // fields
        int defaultFieldIndex = min(controlIndex(), fields.size() - 1);

        Field field = fields.get(defaultFieldIndex);
        control.setFieldSelection(field);

        // set variable format by Levenshtein distance from field name
        variable.getFormats().stream()
                .min(comparing(variableFormat -> {
                    return new LevenshteinDistance(
                            variableFormat.getName(),
                            field.getName()
                    ).compute();
                }))
                .ifPresent(control::setVariableFormatSelection);

        getChildren().add(control);
    }

    @FXML
    private void initialize() {
        variables.forEach(this::initializeControl);
    }

    public List<VariableBindingControl<T>> getControls() {
        return getChildren().stream()
                .map(child -> (VariableBindingControl<T>) child)
                .collect(toList());
    }

}
