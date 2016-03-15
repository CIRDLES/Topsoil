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
package org.cirdles.topsoil.app.plot;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.cirdles.topsoil.app.dataset.Dataset;

import java.util.List;
import java.util.Map;

import static javafx.scene.control.ButtonType.OK;

public class VariableBindingDialog extends Dialog<List<Map<String, Object>>> {

    private final List<Variable> variables;
    private final Dataset dataset;

    public VariableBindingDialog(List<Variable> variables, Dataset dataset) {
        this.variables = variables;
        this.dataset = dataset;

        configureThis();
    }

    private void configureThis() {
        setDialogPane(new VariableBindingDialogPane(variables, dataset));
        setResizable(false);
        setResultConverter(this::convertResult);
    }

    private List<Map<String, Object>> extractVariableContextFromDialogPane() {
        return ((VariableBindingDialogPane) getDialogPane()).getData();
    }

    private List<Map<String, Object>> convertResult(ButtonType type) {
        if (type == OK) {
            return extractVariableContextFromDialogPane();
        } else {
            return null;
        }
    }

}
