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
package org.cirdles.topsoil.app;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.table.Record;
import org.controlsfx.dialog.Dialog;

public class ColumnSelectorDialog extends Dialog {

    private static final String MASTHEAD_TEXT = "Select the column for each variable.";

    public ColumnSelectorDialog(TableView<Record> tableToReadArg) {
        super(null, null);

        setContent(new ColumnSelectorView(tableToReadArg, this));
        getActions().addAll(new ColumnSelectorAction(tableToReadArg, this), Dialog.ACTION_CANCEL);

        setResizable(false);
        setMasthead(MASTHEAD_TEXT);
    }

}
