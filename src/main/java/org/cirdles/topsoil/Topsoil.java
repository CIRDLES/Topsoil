/*
 * Copyright (C) 2014 John Zeringue
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cirdles.topsoil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.cirdles.topsoil.chart.concordia.ErrorChartToolBar;
import org.cirdles.topsoil.chart.DataConverter;
import org.cirdles.topsoil.chart.concordia.ConcordiaChart;
import org.cirdles.topsoil.chart.concordia.ErrorEllipse;
import org.cirdles.topsoil.utils.TSVTableReader;
import org.cirdles.topsoil.utils.TSVTableWriter;
import org.cirdles.topsoil.utils.TableReader;
import org.cirdles.topsoil.utils.TableWriter;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author John Zeringue
 */
public class Topsoil extends Application implements ColumnSelectorDialog.ColumnSelectorDialogListener {
    
    private static final String APP_NAME = "Topsoil";
    
    private static final Path USER_HOME = Paths.get(System.getProperty("user.home"));

    private static final Path TOPSOIL_PATH = Paths.get(USER_HOME.toString(), APP_NAME);
    private static final Path LAST_TABLE_PATH = Paths.get(TOPSOIL_PATH.toString(), "last_table.tsv");

    /**
     * Text of the error shown if there aren't enough columns to fill all the
     * charts' fields
     */
    private final String NOT_ENOUGH_COLUMNS_MESSAGE = "Careful, you don't have enough columns to create an ErrorEllipse Chart";
    /**
     * The table that contain data
     */
    TableView<Map> dataTable;

    // x, y, sigma x, sigma y, rho
    private static final double[][] ellipses = new double[][]{
        {0.0722539075, 0.0110295656, 0.0002049758, 0.0000063126, 0.5365532874},
        {0.0721971452, 0.0110309854, 0.0001783027, 0.0000056173, 0.5325448483},
        {0.0721480905, 0.0110333887, 0.0001262722, 0.0000053814, 0.5693849155},
        {0.0720208987, 0.0110278685, 0.0001041118, 0.0000051695, 0.6034598793},
        {0.0722006985, 0.0110287224, 0.0001150679, 0.0000053550, 0.6488140173},
        {0.0721043666, 0.0110269651, 0.0001536438, 0.0000055438, 0.4514464090},
        {0.0721563039, 0.0110282194, 0.0001241486, 0.0000054189, 0.5407720667},
        {0.0721973299, 0.0110274879, 0.0001224165, 0.0000055660, 0.5557499444},
        {0.0721451656, 0.0110281849, 0.0001461117, 0.0000054048, 0.5309378161},
        {0.0720654237, 0.0110247729, 0.0001547497, 0.0000053235, 0.2337854029},
        {0.0721799174, 0.0110318201, 0.0001485404, 0.0000056511, 0.5177944463},
        {0.0721826355, 0.0110283902, 0.0001377158, 0.0000056126, 0.5953348385},
        {0.0720275042, 0.0110278402, 0.0001875497, 0.0000058909, 0.5274591815},
        {0.0721360819, 0.0110276418, 0.0001252055, 0.0000054561, 0.5760966585}
    };

    @Override
    public void start(Stage primaryStage) {
        if (!Files.exists(TOPSOIL_PATH)) {
            try {
                Files.createDirectory(TOPSOIL_PATH);
            } catch (IOException ex) {
                Logger.getLogger(Topsoil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        ToolBar toolBar = new ToolBar();

        dataTable = new TableView<>();
        VBox.setVgrow(dataTable, Priority.ALWAYS);

        if (Files.exists(LAST_TABLE_PATH)) {
            TableReader tableReader = new TSVTableReader(true);
            try {
                tableReader.read(LAST_TABLE_PATH, dataTable);
            } catch (IOException ex) {
                Logger.getLogger(Topsoil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Menu fileMenu = new Menu("File");
        MenuItem importFromFile = new MenuItem("Import from File");
        fileMenu.getItems().add(importFromFile);
        importFromFile.setOnAction(event -> {
            FileChooser tsvChooser = new FileChooser();
            tsvChooser.setInitialDirectory(USER_HOME.toFile());
            tsvChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Table Files", "TSV"));
            Path filePath = tsvChooser.showOpenDialog(primaryStage).toPath();

            yesNoPrompt("Does the selected file contain headers?", response -> {
                TableReader tableReader = new TSVTableReader(response);

                try {
                    tableReader.read(filePath, dataTable);
                } catch (IOException ex) {
                    Logger.getLogger(Topsoil.class.getName()).log(Level.SEVERE, null, ex);
                }

                TableWriter<Map> tableWriter = new TSVTableWriter(true);
                tableWriter.write(dataTable, LAST_TABLE_PATH);
            });
        });

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);

        dataTable.setOnKeyPressed((KeyEvent event) -> {
            if (event.isShortcutDown() && event.getCode().equals(KeyCode.V)) {
                yesNoPrompt("Does the pasted data contain headers?", response -> {
                    TableReader tableReader = new TSVTableReader(response);
                    tableReader.read(Clipboard.getSystemClipboard().getString(), dataTable);

                    TableWriter<Map> tableWriter = new TSVTableWriter(true);
                    tableWriter.write(dataTable, LAST_TABLE_PATH);
                });
            }
        });

        Button generateErrorEllipseChart = new Button("Error Ellipse Chart");
        generateErrorEllipseChart.setOnAction((ActionEvent event) -> {
            //Show an error if there is not enough column
            if (dataTable.getColumns().size() < 4) {

                Dialogs.create().message(NOT_ENOUGH_COLUMNS_MESSAGE).showWarning();
            } else {
                ColumnSelectorDialog csd = new ColumnSelectorDialog(dataTable, this);
                csd.show();
            }
        });
        toolBar.getItems().add(generateErrorEllipseChart);

        VBox root = new VBox(menuBar, toolBar, dataTable);

        primaryStage.setScene(new Scene(root, 1200, 800, true, SceneAntialiasing.DISABLED));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private static void yesNoPrompt(String message, Consumer<Boolean> callback) {
        Action response = Dialogs.create()
                .title(APP_NAME)
                .message(message)
                .showConfirm();

        if (response != Dialog.Actions.CANCEL) {
            callback.accept(response == Dialog.Actions.YES);
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     * <p>
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Receive a converter from a <code>ColumnSelectorDialog</code> and create a
     * chart from it.
     *
     * @param converter
     */
    @Override
    public void receiveConverter(DataConverter<ErrorEllipse> converter) {
        //Creating a serie with all the data

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (Map<Object, Integer> row_ellipse : dataTable.getItems()) {
            XYChart.Data<Number, Number> data = new XYChart.Data<>(1138, 1138, row_ellipse);
            series.getData().add(data);
        }

        ConcordiaChart chart = new ConcordiaChart(converter);
        chart.getData().add(series);
        VBox.setVgrow(chart, Priority.ALWAYS);
        
        ToolBar toolBar = new ErrorChartToolBar(chart);

        Scene scene = new Scene(new VBox(toolBar, chart), 1200, 800, true, SceneAntialiasing.DISABLED);
        Stage chartStage = new Stage();
        chartStage.setScene(scene);
        chartStage.show();
    }
}
