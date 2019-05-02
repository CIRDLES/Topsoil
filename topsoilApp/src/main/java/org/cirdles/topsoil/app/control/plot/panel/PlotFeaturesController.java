package org.cirdles.topsoil.app.control.plot.panel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.plot.PlotProperties;
import org.cirdles.topsoil.plot.feature.Concordia;

import java.io.IOException;

import static org.cirdles.topsoil.app.control.plot.panel.PlotPropertiesPanel.fireEventOnChanged;

/**
 * A controller with controls for plot features.
 */
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

    @FXML private VBox concordiaControls;
    @FXML CheckBox concordiaLineCheckBox;
    @FXML CheckBox concordiaEnvelopeCheckBox;
    @FXML RadioButton wetherillRadioButton;
    @FXML RadioButton wasserburgRadioButton;
    ToggleGroup concordiaToggleGroup = new ToggleGroup();
    @FXML ColorPicker concordiaLineColorPicker;
    @FXML ColorPicker concordiaEnvelopeColorPicker;
    @FXML Button snapToCornersButton;

    @FXML private VBox evolutionControls;
    @FXML CheckBox evolutionCheckBox;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private final ObjectProperty<IsotopeSystem> isotopeSystem = new SimpleObjectProperty<>(IsotopeSystem.GENERIC);
    public ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
        return isotopeSystem;
    }

    private final ObjectProperty<Concordia> concordiaType = new SimpleObjectProperty<>();
    public ObjectProperty<Concordia> concordiaTypeProperty() {
        return concordiaType;
    }
    public void setConcordiaType(Concordia type) {
        switch (type) {
            case WETHERILL:
                concordiaToggleGroup.selectToggle(wetherillRadioButton);
                break;
            case TERA_WASSERBURG:
                concordiaToggleGroup.selectToggle(wasserburgRadioButton);
                break;
            default:
                concordiaToggleGroup.selectToggle(null);
                break;
        }
    }

    private StringProperty concordiaLineFillValue = new SimpleStringProperty();
    private DoubleProperty concordiaLineOpacityValue = new SimpleDoubleProperty();
    private StringProperty concordiaEnvelopeFillValue = new SimpleStringProperty();
    private DoubleProperty concordiaEnvelopeOpacityValue = new SimpleDoubleProperty();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public PlotFeaturesController() {
        try {
            FXMLUtils.loadController(CONTROLLER_FXML, PlotFeaturesController.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML protected void initialize() {
        container.getChildren().setAll(mcLeanRegressionControls);

        // Listen for isotope system changes; replace system-specific plot controls
        isotopeSystem.addListener(c -> {
            if (isotopeSystem.get() != null) {
                switch (isotopeSystem.get()) {
                    case UPB:
                        container.getChildren().setAll(mcLeanRegressionControls, concordiaControls);
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

        // Configure plot controls
        mcLeanEnvelopeCheckBox.disableProperty().bind(Bindings.not(mcLeanRegressionCheckBox.selectedProperty()));

        // Configure UPb controls
        concordiaToggleGroup.getToggles().addAll(wetherillRadioButton, wasserburgRadioButton);
        wetherillRadioButton.setSelected(true);
        concordiaType.bind(Bindings.createObjectBinding(() -> {
            Toggle toggle = concordiaToggleGroup.getSelectedToggle();
            if (toggle == wasserburgRadioButton) {
                return Concordia.TERA_WASSERBURG;
            } else {
                return Concordia.WETHERILL;
            }
        }, concordiaToggleGroup.selectedToggleProperty()));
        concordiaEnvelopeCheckBox.disableProperty().bind(Bindings.not(concordiaLineCheckBox.selectedProperty()));
        snapToCornersButton.disableProperty().bind(Bindings.not(wetherillRadioButton.selectedProperty()));

        // Configure properties that need to have values converted
        concordiaLineFillValue.bind(Bindings.createStringBinding(
                () -> PlotPropertiesPanel.convertColor(concordiaLineColorPicker.getValue()),
                concordiaLineColorPicker.valueProperty())
        );
        concordiaLineOpacityValue.bind(Bindings.createDoubleBinding(
                () -> PlotPropertiesPanel.convertOpacity(concordiaLineColorPicker.getValue()),
                concordiaLineColorPicker.valueProperty()
        ));
        concordiaEnvelopeFillValue.bind(Bindings.createStringBinding(
                () -> PlotPropertiesPanel.convertColor(concordiaEnvelopeColorPicker.getValue()),
                concordiaEnvelopeColorPicker.valueProperty()
        ));
        concordiaEnvelopeOpacityValue.bind(Bindings.createDoubleBinding(
                () -> PlotPropertiesPanel.convertOpacity(concordiaEnvelopeColorPicker.getValue()),
                concordiaEnvelopeColorPicker.valueProperty()
        ));

        // Fire property changed events
        fireEventOnChanged(mcLeanRegressionCheckBox.selectedProperty(), mcLeanRegressionCheckBox, PlotProperties.MCLEAN_REGRESSION);
        fireEventOnChanged(mcLeanEnvelopeCheckBox.selectedProperty(), mcLeanEnvelopeCheckBox, PlotProperties.MCLEAN_REGRESSION_ENVELOPE);

        fireEventOnChanged(concordiaType, this, PlotProperties.CONCORDIA_TYPE);
        fireEventOnChanged(concordiaLineCheckBox.selectedProperty(), concordiaLineCheckBox, PlotProperties.CONCORDIA_LINE);
        fireEventOnChanged(concordiaLineFillValue, concordiaLineColorPicker, PlotProperties.CONCORDIA_LINE_FILL);
        fireEventOnChanged(concordiaLineOpacityValue, concordiaLineColorPicker, PlotProperties.CONCORDIA_LINE_OPACITY);
        fireEventOnChanged(concordiaEnvelopeCheckBox.selectedProperty(), concordiaEnvelopeCheckBox, PlotProperties.CONCORDIA_ENVELOPE);
        fireEventOnChanged(concordiaEnvelopeFillValue, concordiaEnvelopeColorPicker, PlotProperties.CONCORDIA_ENVELOPE_FILL);
        fireEventOnChanged(concordiaEnvelopeOpacityValue, concordiaEnvelopeColorPicker, PlotProperties.CONCORDIA_ENVELOPE_OPACITY);

        fireEventOnChanged(evolutionCheckBox.selectedProperty(), evolutionCheckBox, PlotProperties.EVOLUTION);
    }

}