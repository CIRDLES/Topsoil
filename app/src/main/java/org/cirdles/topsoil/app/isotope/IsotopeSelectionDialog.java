package org.cirdles.topsoil.app.isotope;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A simple dialog for selecting the {@code IsotopeType} of a {@code TopsoilDataTable}.
 *
 * @author Benjamin Muldrow
 */
public class IsotopeSelectionDialog extends ChoiceDialog<String> {

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code IsotopeSelectionDialog}.
     */
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

    //***********************
    // Methods
    //***********************

    /**
     * Opens a Dialog for isotope system selection and returns the selected {@code IsotopeType}.
     *
     * @param isotopeSelectionDialog an instance of IsotopeSelectionDialog
     * @return user-selected IsotopeType
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
