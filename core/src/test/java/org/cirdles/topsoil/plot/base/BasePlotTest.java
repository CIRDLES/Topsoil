package org.cirdles.topsoil.plot.base;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.plot.Plot;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.testfx.framework.junit.ApplicationTest;

/**
 * Created by Jake on 6/7/2017.
 */
public class BasePlotTest extends ApplicationTest {

    @Rule
    public Timeout timeout = Timeout.seconds(5);

    private Plot plot;

    @Override
    public void start(Stage stage) throws Exception {
        plot = new BasePlot();

        Scene scene = new Scene((Parent) plot.displayAsNode());
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testInitialize() {
        // no-op
    }

}
