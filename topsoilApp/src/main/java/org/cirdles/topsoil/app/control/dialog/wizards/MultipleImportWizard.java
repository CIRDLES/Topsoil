package org.cirdles.topsoil.app.control.dialog.wizards;

import javafx.scene.control.ButtonType;
import org.controlsfx.dialog.Wizard;

import java.util.Map;

/**
 * @author marottajb
 */
public class MultipleImportWizard extends Wizard {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    public static final double INIT_WIDTH = 600.0;
    public static final double INIT_HEIGHT = 550.0;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final MultipleImportSourcesView sourcesView = new MultipleImportSourcesView();
    private final MultipleImportPreview preView = new MultipleImportPreview();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private MultipleImportWizard() {
        LinearFlow pageOrder = new Wizard.LinearFlow(
                sourcesView,
                preView
        );
        this.setFlow(pageOrder);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static Map<String, Object> startWizard() {
        MultipleImportWizard wizard = new MultipleImportWizard();
        ButtonType response = wizard.showAndWait().orElse(null);

        wizard.preView.onExitingPage(wizard);       // This is necessary because of a bug where this is not called
                                                    // when the wizard is finished. Should be removed when fixed.

        if (response == null || response.equals(ButtonType.CANCEL)) {
            return null;
        } else {
            if (wizard.getSettings() != null) {
                return wizard.getSettings();
            }
        }
        return null;
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    public static class Key {
        public static final String TABLES = "TABLES";
    }

}
