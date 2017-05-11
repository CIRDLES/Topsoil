package org.cirdles.topsoil.app.plot;

import javafx.scene.control.ChoiceDialog;
import org.cirdles.topsoil.app.isotope.IsotopeType;

import java.util.ArrayList;

/**
 * A simple {@code ChoiceDialog} that offers a user a choice of plot types based on the current {@link IsotopeType}.
 *
 * @author Benjamin Muldrow
 */
public class PlotChoiceDialog extends ChoiceDialog<String> {

    //***********************
    // Attributes
    //***********************

    /**
     * The current {@code IsotopeType}.
     */
    private final IsotopeType type;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code PlotChoiceDialog} for the specified {@code IsotopeType}.
     *
     * @param type  IsotopeType
     */
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

    //***********************
    // Methods
    //***********************

    /**
     * show dialog and wait for response
     *
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
