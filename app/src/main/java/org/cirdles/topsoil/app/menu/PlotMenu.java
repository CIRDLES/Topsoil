/*
 * Copyright 2016 CIRDLES.
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
package org.cirdles.topsoil.app.menu;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.cirdles.topsoil.app.TopsoilMainWindow;
import org.cirdles.topsoil.app.plot.PlotType;

import java.util.List;

/**
 * Created by johnzeringue on 1/17/16.
 */
public class PlotMenu extends Menu {

    private List<PlotType> plotTypes;
    private TopsoilMainWindow topsoilMainWindow;

    public PlotMenu() {
        setText("Plots");
    }

    public List<PlotType> getPlotTypes() {
        return plotTypes;
    }

    private EventHandler<ActionEvent> createPlot(PlotType plotType) {
        return event -> {
            if (topsoilMainWindow != null) {
                topsoilMainWindow.initializeAndShow(plotType);
            }
        };
    }

    private void refreshItems() {
        getItems().clear();

        getPlotTypes().forEach(plotType -> {
            MenuItem menuItem = new MenuItem();

            menuItem.setText(plotType.getName());
            menuItem.setOnAction(createPlot(plotType));

            getItems().add(menuItem);
        });
    }

    public void setPlotTypes(List<PlotType> plotTypes) {
        this.plotTypes = plotTypes;
        refreshItems();
    }

    public TopsoilMainWindow getTopsoilMainWindow() {
        return topsoilMainWindow;
    }

    public void setTopsoilMainWindow(TopsoilMainWindow topsoilMainWindow) {
        this.topsoilMainWindow = topsoilMainWindow;
    }

}
