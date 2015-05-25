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

import com.johnzeringue.extendsfx.layout.CustomVBox;
import java.util.Optional;
import javafx.fxml.FXML;

/**
 *
 * @author parizotclement
 */
public class TabContents extends CustomVBox<TabContents> {

    Optional<TSVTable> table;
    Optional<InstructionsPanel> instructions;

    public TabContents(TSVTable table) {
        super(self -> {
            self.table = Optional.of(table);
            self.instructions = Optional.empty();
        });
    }

    public TabContents(InstructionsPanel instructions) {
        super(self -> {
            self.table = Optional.empty();
            self.instructions = Optional.of(instructions);
        });
    }

    @FXML
    private void initialize() {
        getTable().ifPresent(getChildren()::setAll);
        getInstructions().ifPresent(getChildren()::setAll);
    }

    public Optional<TSVTable> getTable() {
        return table;
    }

    public Optional<InstructionsPanel> getInstructions() {
        return instructions;
    }

}
