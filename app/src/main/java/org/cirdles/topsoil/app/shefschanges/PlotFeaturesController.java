package org.cirdles.topsoil.app.shefschanges;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import org.cirdles.commons.util.ResourceExtractor;

import java.io.IOException;

public class PlotFeaturesController extends AnchorPane {

    @FXML
    CheckBox mcLeanRegressionCheckBox;

    @FXML
    CheckBox mcLeanUncertaintyCheckBox;

    @FXML
    CheckBox wetherillCheckBox;

    @FXML
    CheckBox evolutionCheckBox;

    @FXML
    public void initialize() {

    }

    public PlotFeaturesController() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    new ResourceExtractor(PlotFeaturesController.class).extractResourceAsPath("plot-features.fxml").toUri().toURL()
            );
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}