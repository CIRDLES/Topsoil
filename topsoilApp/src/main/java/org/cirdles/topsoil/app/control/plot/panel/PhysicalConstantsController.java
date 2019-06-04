package org.cirdles.topsoil.app.control.plot.panel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.plot.PlotOption;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PhysicalConstantsController extends AnchorPane {

    private Map<Lambda, LambdaRow> lambdaRowMap = new HashMap<>();

    public PhysicalConstantsController() {
        super();

        VBox lambdaVBox = new VBox();
        lambdaVBox.setPadding(new Insets(10.0));
        lambdaVBox.setSpacing(10.0);
        LambdaRow lambdaRow;
        for (Lambda lambda : Lambda.values()) {
            lambdaRow = new LambdaRow(lambda);
            lambdaRowMap.put(lambda, lambdaRow);
            lambdaVBox.getChildren().add(lambdaRow);
        }

        AnchorPane innerAnchor = new AnchorPane(lambdaVBox);
        FXMLUtils.setAnchorPaneConstraints(lambdaVBox, 0.0, 0.0, 0.0, 0.0);

        ScrollPane scrollPane = new ScrollPane(innerAnchor);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        this.getChildren().add(scrollPane);
        FXMLUtils.setAnchorPaneConstraints(scrollPane, 0.0, 0.0, 0.0, 0.0);
    }

    void setLambda(Lambda lambda, double value) {
        if (lambdaRowMap.get(lambda) != null) {
            lambdaRowMap.get(lambda).textField.setText(Double.toString(value));
        }
    }

    /**
     * A simple set of controls for editing the value of a numeric {@code Constant}.
     *
     * @author marottajb
     */
    public static class LambdaRow extends HBox {

        private static String CONTROLLER_FXML = "lambda-row.fxml";

        private Lambda lambda;

        private DoubleProperty valueProperty = new SimpleDoubleProperty();

        //**********************************************//
        //                   CONTROLS                   //
        //**********************************************//

        @FXML private Label constantLabel;
        @FXML private TextField textField;
        @FXML private Button moreOptionsButton;

        //**********************************************//
        //                 CONSTRUCTORS                 //
        //**********************************************//

        LambdaRow(Lambda lambda) {
            this.lambda = lambda;
            try {
                FXMLUtils.loadController(CONTROLLER_FXML, LambdaRow.class, this);
            } catch (IOException e) {
                throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
            }
        }

        @FXML
        protected void initialize() {
            constantLabel.setText(lambda.getTitle() + ":");

            textField.setText(lambda.getDefaultValue().toString());
            textField.setPrefWidth(100.0);

            PlotOptionsPanel.fireEventOnChanged(
                    valueProperty,
                    this,
                    PlotOption.forLambda(lambda)
            );

            Button setButton = new Button("Set Value");
            setButton.setOnAction(event -> {
                try {
                    double value = Double.parseDouble(textField.getText());
                    this.valueProperty.set(value);
                } catch (NumberFormatException e) {
                    textField.setText(Double.toString(valueProperty.get()));
                }
            });

            Button resetButton = new Button("Reset to Default");
            resetButton.setOnAction(event -> {
                valueProperty.set((double) lambda.getDefaultValue());
                textField.setText(Double.toString(valueProperty.get()));
            });

            VBox vBox = new VBox(setButton, resetButton);
            vBox.setSpacing(10.0);
            vBox.setPadding(new Insets(10.0));

            PopOver popOver = new PopOver(vBox);
            moreOptionsButton.setOnAction(event -> popOver.show(moreOptionsButton));
        }

    }

}
