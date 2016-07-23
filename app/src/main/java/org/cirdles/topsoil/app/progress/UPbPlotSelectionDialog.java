package org.cirdles.topsoil.app.progress;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceDialog;
import org.cirdles.topsoil.app.plot.UraniumLeadPlotType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by sbunce on 7/20/2016.
 */
public class UPbPlotSelectionDialog extends ChoiceDialog<String> {

    public UPbPlotSelectionDialog() {
        super();

        // creates a list of possible plots
        List<String> plots = new ArrayList<String>();
        for (UraniumLeadPlotType plotType : UraniumLeadPlotType.values()) {
            plots.add(plotType.getName());
        }

        // add list to choice dialogue
        this.getItems().addAll(FXCollections.observableList(plots));

        // Set default selected item
        this.setSelectedItem(UraniumLeadPlotType.values()[0].getName());
    }

    /**
     * Opens a Dialogue for plot selection and generates the selected plot type
     * @param plotSelectionDialog An instance of a dialogue
     * @return selection from the user
     */
    public static UraniumLeadPlotType selectPlot(UPbPlotSelectionDialog plotSelectionDialog) {
        // display dialogue
        UraniumLeadPlotType selection = null;
        Optional<String> result = plotSelectionDialog.showAndWait();

        // check selection
        for (UraniumLeadPlotType plotType : UraniumLeadPlotType.values()) {
            if (result.get().equals(plotType.getName())) {
                selection = plotType;
            }
        }

        return selection;
    }
}
