package org.cirdles.topsoil.app.progress;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Created by benjaminmuldrow on 6/20/16.
 */
public class UPbTable extends TableView<UPbDataEntry> {

    private TableColumn leadUraniumCol;
    private TableColumn leadUraniumStDCol;
    private TableColumn leadUraniumCol2;
    private TableColumn leadUraniumStDCol2;
    private TableColumn corrCoefCol;

    String [] headers;

    public UPbTable(List<UPbDataEntry> entries) {

        super();
        ObservableList<UPbDataEntry> data = FXCollections.observableList(entries);

        // use default column headers
        // TODO put default headers in enum
        String header1 = "207Pb*/235U";
        String header2 = "±2σ (%)";
        String header3 = "206Pb*/238U";
        String header4 = "±2σ (%)";
        String header5 = "Corr Coef";

        this.headers = new String[5];
        this.headers[0] = header1;
        this.headers[1] = header2;
        this.headers[2] = header3;
        this.headers[3] = header4;
        this.headers[4] = header5;

        this.setItems(data);
        this.setColumns();
        this.getColumns().addAll(
                leadUraniumCol,
                leadUraniumStDCol,
                leadUraniumCol2,
                leadUraniumStDCol2,
                corrCoefCol
        );
    }

    private void setColumns() {
        leadUraniumCol = new TableColumn(headers[0]);
        leadUraniumCol.setCellValueFactory(
                new PropertyValueFactory<UPbDataEntry, Double>("leadUranium")
        );

        leadUraniumStDCol = new TableColumn(headers[1]);
        leadUraniumStDCol.setCellValueFactory(
                new PropertyValueFactory<UPbDataEntry, Double>("leadUraniumStD")
        );

        leadUraniumCol2 = new TableColumn(headers[2]);
        leadUraniumCol2.setCellValueFactory(
                new PropertyValueFactory<UPbDataEntry, Double>("leadUranium2")
        );

        leadUraniumStDCol2 = new TableColumn(headers[3]);
        leadUraniumStDCol2.setCellValueFactory(
                new PropertyValueFactory<UPbDataEntry, Double>("leadUraniumStD2")
        );

        corrCoefCol = new TableColumn(headers[4]);
        corrCoefCol.setCellValueFactory(
                new PropertyValueFactory<UPbDataEntry, Double>("corrCoef")
        );
    }
}
