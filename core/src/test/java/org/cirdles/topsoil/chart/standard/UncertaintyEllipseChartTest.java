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
package org.cirdles.topsoil.chart.standard;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.chart.Chart;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.testfx.framework.junit.ApplicationTest;

import static org.cirdles.topsoil.chart.standard.UncertaintyEllipseChartProperties.TITLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by johnzeringue on 11/11/15.
 */
public class UncertaintyEllipseChartTest extends ApplicationTest {

    @Rule
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    public Timeout timeout = Timeout.seconds(5);

    private Chart chart;

    @Override
    public void start(Stage stage) throws Exception {
        chart = new UncertaintyEllipseChart();

        Scene scene = new Scene((Parent) chart.displayAsNode());
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testGetProperty() {
        assertThat(chart.getProperty(TITLE), is("Uncertainty Ellipse Chart"));
    }

    @Test
    public void testSetProperty() {
        chart.setProperty(TITLE, "New Title");
        assertThat(chart.getProperty(TITLE), is("New Title"));
    }

}
