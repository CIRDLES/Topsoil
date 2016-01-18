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
import org.cirdles.topsoil.app.plot.PlotType;
import org.cirdles.topsoil.app.system.IsotopeSystem;

import java.util.List;

import static java.util.Arrays.asList;
import static org.cirdles.topsoil.app.system.IsotopeSystem.URANIUM_LEAD;

/**
 * Created by johnzeringue on 1/17/16.
 */
public class IsotopeSystemMenu extends Menu {

    private static final IsotopeSystem DEFAULT_SYSTEM = URANIUM_LEAD;

    private PlotMenu plotMenu;

    public IsotopeSystemMenu() {
        setText("Isotope Systems");
        createItems();
    }

    private EventHandler<ActionEvent> usePlotTypes(List<PlotType> plotTypes) {
        return event -> {
            if (plotMenu != null) {
                plotMenu.setPlotTypes(plotTypes);
                getItems().forEach(menuItem -> menuItem.setDisable(false));
                ((MenuItem) event.getTarget()).setDisable(true);
            }
        };
    }

    private void createItems() {
        asList(IsotopeSystem.values()).forEach(isotopeSystem -> {
            MenuItem menuItem = new MenuItem();

            menuItem.setText(isotopeSystem.getName());
            menuItem.setOnAction(usePlotTypes(isotopeSystem.getPlotTypes()));

            if (isotopeSystem.equals(DEFAULT_SYSTEM)) {
                menuItem.setDisable(true);
            }

            getItems().add(menuItem);
        });
    }

    public PlotMenu getPlotMenu() {
        return plotMenu;
    }

    private void setDefaultIsotopeSystem(PlotMenu plotMenu) {
        plotMenu.setPlotTypes(DEFAULT_SYSTEM.getPlotTypes());
    }

    public void setPlotMenu(PlotMenu plotMenu) {
        this.plotMenu = plotMenu;
        setDefaultIsotopeSystem(plotMenu);
    }

}
