/*
 * Copyright 2014 pfif.
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

package org.cirdles.topsoil;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import org.controlsfx.dialog.Dialogs;

/**
 * A toolbar, containing buttons to call actions on a <code>TopsoilTable</code>.
 */
public class TopsoilTableToolbar extends ToolBar {
    /**
     * The table that is linked to the Menubar
     */
    TopsoilTable dataTable;

    public TopsoilTableToolbar(TopsoilTable dataTable_arg) {
        dataTable = dataTable_arg;
        
        Button generateErrorEllipseChart = new Button("Error Ellipse Chart");
        generateErrorEllipseChart.setOnAction((ActionEvent event) -> {
            //Show an error if there is not enough column
            if (dataTable.getColumns().size() < 4) {

                Dialogs.create().message(Topsoil.NOT_ENOUGH_COLUMNS_MESSAGE).showWarning();
            } else {
                ColumnSelectorDialog csd = new ColumnSelectorDialog(dataTable, dataTable);
                csd.show();
            }
        });
        this.getItems().add(generateErrorEllipseChart);
    }      
}
