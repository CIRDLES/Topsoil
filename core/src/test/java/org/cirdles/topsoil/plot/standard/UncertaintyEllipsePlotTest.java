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
package org.cirdles.topsoil.plot.standard;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.plot.Plot;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.testfx.framework.junit.ApplicationTest;

/**
 * Created by johnzeringue on 11/11/15.
 */
public class UncertaintyEllipsePlotTest extends ApplicationTest {

    @Rule
    public Timeout timeout = Timeout.seconds(5);

    private Plot plot;

    @Override
    public void start(Stage stage) throws Exception {
        plot = new UncertaintyEllipsePlot();

        Scene scene = new Scene((Parent) plot.displayAsNode());
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testInitialize() {
        // no-op
    }

}
