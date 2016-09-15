package org.cirdles.topsoil.app.progress.plot;

import javafx.scene.control.ChoiceDialog;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;

import java.util.ArrayList;

/**
 * Created by benjaminmuldrow on 8/9/16.
 */
public class PlotChoiceDialog extends ChoiceDialog<String> {

    private final IsotopeType type;

    public PlotChoiceDialog(IsotopeType type) {

        this.type = type;

        // get names from enum
        ArrayList<String> names = new ArrayList<>();
        for (TopsoilPlotType plotType : type.getPlots()) {
            names.add(plotType.getName());
        }

        // add names
        this.getItems().addAll(names);
        this.setSelectedItem(getItems().get(0));
    }

    /**
     * show dialog and wait for response
     * @return TopsoilPlotType user's choice of plotType
     */
    public TopsoilPlotType select() {

        final TopsoilPlotType[] result = {null};

        // check response
        this.showAndWait().ifPresent(consumer -> {
            for (TopsoilPlotType plot : TopsoilPlotType.values()) {
                if (consumer.equals(plot.getName())) {
                    result[0] = plot;
                }
            }
        });

        return result[0];
    }

}
