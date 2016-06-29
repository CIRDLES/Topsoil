package org.cirdles.topsoil.app.progress;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.cirdles.topsoil.app.util.Alerter;
import org.cirdles.topsoil.app.util.ErrorAlerter;

import java.util.List;

/**
 * Created by benjaminmuldrow on 6/20/16.
 */
public class TopsoilTable extends TableView<TopsoilDataEntry> {

    private Alerter alerter;

    private TableColumn leadUraniumCol;
    private TableColumn leadUraniumStDCol;
    private TableColumn leadUraniumCol2;
    private TableColumn leadUraniumStDCol2;
    private TableColumn corrCoefCol;

    private String [] headers;

    public TopsoilTable(List<TopsoilDataEntry> entries, String [] headers, IsotopeType isotopeType) {

        super();
        ObservableList<TopsoilDataEntry> data = FXCollections.observableList(entries);

        // enter headers
        if (headers == null) {
            this.headers = isotopeType.getHeaders();
        } else if (headers.length < 4 || headers.length > 5) {
            alerter = new ErrorAlerter();
            alerter.alert("Invalid Headers");
        } else if (headers.length == 4) {
            // populate with provided headers and add Corr Coef column
            for (int i = 0; i < headers.length; i ++) {
                this.headers[i] = headers[i];
                this.headers[5] = "Corr Coef";
            }
        } else if (headers.length == 5) {
            this.headers = headers;
        }

        // enter data
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
                new PropertyValueFactory<TopsoilDataEntry, Double>("leadUranium")
        );

        leadUraniumStDCol = new TableColumn(headers[1]);
        leadUraniumStDCol.setCellValueFactory(
                new PropertyValueFactory<TopsoilDataEntry, Double>("leadUraniumStD")
        );

        leadUraniumCol2 = new TableColumn(headers[2]);
        leadUraniumCol2.setCellValueFactory(
                new PropertyValueFactory<TopsoilDataEntry, Double>("leadUranium2")
        );

        leadUraniumStDCol2 = new TableColumn(headers[3]);
        leadUraniumStDCol2.setCellValueFactory(
                new PropertyValueFactory<TopsoilDataEntry, Double>("leadUraniumStD2")
        );

        corrCoefCol = new TableColumn(headers[4]);
        corrCoefCol.setCellValueFactory(
                new PropertyValueFactory<TopsoilDataEntry, Double>("corrCoef")
        );
    }

    public static void createNewRow(TopsoilTable table) {
        table.getItems().add(new TopsoilDataEntry(0,0,0,0,0));
    }
}
