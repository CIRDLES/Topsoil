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
package org.cirdles.topsoil.chart;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.table.Record;
import org.controlsfx.dialog.Dialog;

public class ChartInitializationDialog extends Dialog {

    private static final String MASTHEAD_TEXT = "Select the column for each variable.";

    public ChartInitializationDialog(TableView<Record> tableToRead, JavaScriptChart chart) {
        super(null, null);

        setContent(new ChartInitializationView(tableToRead));
        getActions().addAll(new ChartInitializationAction(tableToRead, chart, this), Dialog.ACTION_CANCEL);

        setResizable(false);
        setMasthead(MASTHEAD_TEXT);
    }

}
