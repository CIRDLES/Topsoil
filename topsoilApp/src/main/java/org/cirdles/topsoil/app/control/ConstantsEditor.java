package org.cirdles.topsoil.app.control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.Lambda;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.util.Map;

/**
 * A custom control for editing constant values.
 *
 * @author marottajb
 */
public class ConstantsEditor extends Accordion {

    private static String CONTROLLER_FXML = "constants-editor.fxml";

    @FXML private VBox lambdaBox;

    private TopsoilProject project;

    public ConstantsEditor(TopsoilProject project) {
        this.project = project;
        try {
            FXMLUtils.loadController(CONTROLLER_FXML, ConstantsEditor.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
        for (Map.Entry<Lambda, Number> entry : project.getLambdas().entrySet()) {
            lambdaBox.getChildren().add(new LambdaRow(entry.getKey(), project));
        }
    }

    /**
     * A simple set of controls for editing the value of a numeric {@code Constant}.
     *
     * @author marottajb
     */
    public static class LambdaRow extends HBox {

        private static String CONTROLLER_FXML = "constant-row.fxml";

        //**********************************************//
        //                  ATTRIBUTES                  //
        //**********************************************//

        private TopsoilProject project;
        private Lambda lambda;

        //**********************************************//
        //                   CONTROLS                   //
        //**********************************************//

        @FXML private Label constantLabel;
        @FXML private TextField textField;
        @FXML private Button moreOptionsButton;

        //**********************************************//
        //                 CONSTRUCTORS                 //
        //**********************************************//

        LambdaRow(Lambda lambda, TopsoilProject project) {
            this.lambda = lambda;
            this.project = project;
            try {
                final ResourceExtractor re = new ResourceExtractor(LambdaRow.class);
                final FXMLLoader loader = new FXMLLoader(re.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
                loader.setRoot(this);
                loader.setController(this);
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
            }
        }

        @FXML
        protected void initialize() {
            Number lambdaValue = project.getLambdaValue(lambda);

            constantLabel.setText(lambda.getTitle());
            textField.setText(lambdaValue.toString());
            textField.setPrefWidth(100.0);

            Button setButton = new Button("Set Value");
            setButton.setOnAction(event -> {
                try {
                    double value = Double.parseDouble(textField.getText());
                    project.setLambdaValue(lambda, value);
                } catch (NumberFormatException e) {
                    textField.setText(getLambdaValue());
                }
            });

            Button resetButton = new Button("Reset to Default");
            resetButton.setOnAction(event -> {
                project.setLambdaValue(lambda, lambda.getDefaultValue());
                textField.setText(getLambdaValue());
            });

            VBox vBox = new VBox(setButton, resetButton);
            vBox.setSpacing(10.0);
            vBox.setPadding(new Insets(10.0));

            PopOver popOver = new PopOver(vBox);
            moreOptionsButton.setOnAction(event -> popOver.show(moreOptionsButton));
        }

        private String getLambdaValue() {
            return project.getLambdaValue(lambda).toString();
        }

    }
}
