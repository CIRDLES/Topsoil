/*
 * Copyright 2014 pfif.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.topsoil.chart.concordia.panels;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.cirdles.jfxutils.NodeToSVGConverter;
import org.cirdles.jfxutils.NumberField;
import org.cirdles.topsoil.LabelUsePrefSize;
import org.cirdles.topsoil.chart.concordia.ErrorEllipseChart;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.control.action.Action;

/**
 *
 * @author pfif
 */
public class SVGExportDialog extends Dialog {

    private final ResourceBundle resources = ResourceBundle.getBundle("org.cirdles.topsoil.Resources");

    private ErrorEllipseChart chart;

    public SVGExportDialog(Object owner, ErrorEllipseChart chart) {
        super(owner, "Export SVG");
        this.chart = chart;

        setTitle(resources.getString("exportToSVG"));
        setContent(new SVGExportDialogView());

        getActions().add(new ConvertToSVGAction());
        getActions().add(Dialog.ACTION_CANCEL);
    }

    private static class SVGExportDialogView extends VBox {

        @FXML private NumberField svgWidthField;
        @FXML private NumberField svgHeightField;

        @FXML private ResourceBundle resources;

        public SVGExportDialogView() {
            FXMLLoader loader = new FXMLLoader(SVGExportDialog.class.getResource("svgexportpanel.fxml"),
                                               ResourceBundle.getBundle("org.cirdles.topsoil.Resources"));
            loader.setRoot(this);
            loader.setController(this);
            try {
                loader.load();
            } catch (IOException ex) {
                this.getChildren().add(new LabelUsePrefSize("There was an error loading this module."));
            }

        }

        @FXML
        private void initialize() {
            svgWidthField.setConverted(15);
            svgHeightField.setConverted(10);
        }

        public NumberField getSvgWidthField() {
            return svgWidthField;
        }

        public NumberField getSvgHeightField() {
            return svgHeightField;
        }

    }

    private class ConvertToSVGAction extends Action {

        public ConvertToSVGAction() {
            super(resources.getString("exportToSVG"), event -> {
                NodeToSVGConverter converter = new NodeToSVGConverter();

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Export to SVG");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SVG Image", "*.svg"));
                File file = fileChooser.showSaveDialog(getContent().getScene().getWindow());

                if (file != null) {
                    converter.convert(chart,
                                      file,
                                      ((SVGExportDialogView) getContent()).getSvgWidthField().getConverted().doubleValue(),
                                      ((SVGExportDialogView) getContent()).getSvgHeightField().getConverted().doubleValue());
                }

                hide();
            });
        }

    }
}
