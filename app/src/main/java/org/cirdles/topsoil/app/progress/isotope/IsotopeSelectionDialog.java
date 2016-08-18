package org.cirdles.topsoil.app.progress.isotope;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by benjaminmuldrow on 6/29/16.
 */
public class IsotopeSelectionDialog extends ChoiceDialog<String> {

    public IsotopeSelectionDialog() {
        super();

        // create a list of isotope systems
        List<String> systems = new ArrayList<String>();
        for (IsotopeType isotopeType : IsotopeType.values()) {
            systems.add(isotopeType.getName());
        }

        // add list to choice dialogue
        this.getItems().addAll(FXCollections.observableList(systems));

        // Set default selected item
        this.setSelectedItem(IsotopeType.values()[0].getName());

    }

    /**
     * Opens a Dialogue for isotope system selection and returns selected isotope system enum
     * @param isotopeSelectionDialog An instance of a dialogue
     * @return selection from the user
     */
    public static IsotopeType selectIsotope(IsotopeSelectionDialog isotopeSelectionDialog) {

        // display dialogue
        IsotopeType selection = null;
        Optional<String> result = isotopeSelectionDialog.showAndWait();

        if (result.isPresent()) {

            // check selection
            for (IsotopeType isotopeType : IsotopeType.values()) {
                if (result.get().equals(isotopeType.getName())) {
                    selection = isotopeType;
                }
            }
        } else { // CANCELED
            selection = null;
        }

        return selection;
    }
}
