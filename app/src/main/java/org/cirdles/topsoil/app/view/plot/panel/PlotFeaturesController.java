package org.cirdles.topsoil.app.view.plot.panel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.isotope.IsotopeSystem;

import java.io.IOException;

public class PlotFeaturesController extends AnchorPane {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "plot-features-menu.fxml";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private VBox container;

    @FXML private VBox mcLeanRegressionControls;
    @FXML CheckBox mcLeanRegressionCheckBox;
    @FXML CheckBox mcLeanEnvelopeCheckBox;

    @FXML private VBox wetherillControls;
    @FXML CheckBox wetherillCheckBox;
    @FXML ColorPicker wetherillLineFillColorPicker;
    @FXML ColorPicker wetherillEnvelopeFillColorPicker;

    @FXML private VBox wasserburgControls;
    @FXML CheckBox wasserburgCheckBox;
    @FXML ColorPicker wasserburgLineFillColorPicker;
    @FXML ColorPicker wasserburgEnvelopeFillColorPicker;

    @FXML private VBox evolutionControls;
    @FXML CheckBox evolutionCheckBox;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private ObjectProperty<IsotopeSystem> isotopeSystem;
    public ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
        if (isotopeSystem == null) {
            isotopeSystem = new SimpleObjectProperty<>(IsotopeSystem.GENERIC);
            isotopeSystem.addListener(c -> {
            	if (isotopeSystem.get() != null) {
		            switch ( isotopeSystem.get() ) {
			            case UPB:
				            container.getChildren().setAll(mcLeanRegressionControls, wetherillControls, wasserburgControls);
				            break;
			            case UTH:
				            container.getChildren().setAll(mcLeanRegressionControls, evolutionControls);
				            break;
			            default:
				            container.getChildren().setAll(mcLeanRegressionControls);
				            break;
		            }
	            }
            });
        }
        return isotopeSystem;
    }
    public final IsotopeSystem getIsotopeSystem() {
        return isotopeSystemProperty().get();
    }
    public final void setIsotopeSystem(IsotopeSystem i) {
        if (i != null) {
            isotopeSystemProperty().set(i);
        }
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public PlotFeaturesController() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    new ResourceExtractor(PlotFeaturesController.class).extractResourceAsPath(CONTROLLER_FXML).toUri().toURL()
            );
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML protected void initialize() {
        container.getChildren().setAll(mcLeanRegressionControls);
        isotopeSystemProperty();
    }

}