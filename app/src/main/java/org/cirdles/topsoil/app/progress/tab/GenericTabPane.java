package org.cirdles.topsoil.app.progress.tab;

import javafx.css.Styleable;
import javafx.event.EventTarget;
import javafx.scene.control.Skinnable;
import org.cirdles.topsoil.app.progress.table.GenericTable;

/**
 * Created by benjaminmuldrow on 8/3/16.
 */
public interface GenericTabPane extends EventTarget, Styleable, Skinnable {
    //Adds a new tab to the MainWindow tab pane
    void add (GenericTable table);

    TopsoilTab getSelectedTab();
}
