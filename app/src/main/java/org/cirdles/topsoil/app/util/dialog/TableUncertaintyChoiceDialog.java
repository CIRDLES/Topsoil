package org.cirdles.topsoil.app.util.dialog;

import javafx.scene.control.ChoiceDialog;
import org.cirdles.topsoil.app.table.uncertainty.UncertaintyFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A custom {@code ChoiceDialog} for selecting the {@link UncertaintyFormat} of a table.
 *
 * @author Jake Marotta
 */
public class TableUncertaintyChoiceDialog extends ChoiceDialog<String> {

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code TableUncertaintyChoiceDialog}.
     */
    public TableUncertaintyChoiceDialog() {
        super();

        this.setContentText("What format are your uncertainty values in?");

        List<String> formatNames = new ArrayList<>();
        for (UncertaintyFormat format : UncertaintyFormat.ALL) {
            formatNames.add(format.getName());
        }
        this.getItems().addAll(formatNames);

        // Set default selected item
        this.setSelectedItem(UncertaintyFormat.ALL.get(0).getName());

    }

    //***********************
    // Methods
    //***********************

    /**
     * Opens a {@code ChoiceDialog} for table uncertainty format selection and returns the selected {@code
     * UncertaintyFormat}.
     *
     * @return selected UncertaintyFormat
     */
    public UncertaintyFormat selectUncertaintyFormat() {

        UncertaintyFormat selection = null;
        Optional<String> result = this.showAndWait();
        if (result.isPresent()) {
            for (UncertaintyFormat format : UncertaintyFormat.ALL) {
                if (format.getName().equals(result.get())) {
                    selection = format;
                    break;
                }
            }
        }

        return selection;
    }

}
