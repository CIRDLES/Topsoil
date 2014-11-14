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

import org.cirdles.topsoil.app.TopsoilMainWindow;
import javafx.scene.Parent;
import org.junit.Test;
import static org.loadui.testfx.Assertions.*;
import org.loadui.testfx.GuiTest;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author John Zeringue
 */
public class TopsoilMainWindowTest extends GuiTest {

    @Override
    protected Parent getRootNode() {
        return new TopsoilMainWindow();
    }
    
    @Test
    public void createErrorChartButton_should_openColumnSelectorDialog() {
        int initialNumberOfWindows = getWindows().size();
        
        click("#createErrorChartButton");
        
        // if the button works then there should now be one more window
        verifyThat(getWindows().size(), is(initialNumberOfWindows + 1));
    }
    
}
