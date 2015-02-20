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
package org.cirdles.topsoil.app;

import javafx.fxml.FXML;
import org.cirdles.javafx.CustomVBox;

/**
 *
 * @author John Zeringue
 */
public class EmptyTablePlaceholder extends CustomVBox<EmptyTablePlaceholder> {

    private TSVTable dataTable;

    public EmptyTablePlaceholder(TSVTable dataTable) {
        super(self -> self.dataTable = dataTable);
    }
    
    @FXML
    void pasteFromClipboardIntoDataTable() {
        dataTable.pasteFromClipboard();
    }

}
