package org.cirdles.topsoil.app.data;

import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.app.ProjectManager;

/**
 * Represents a working state of Topsoil data. Maintains a list of active {@link DataTable}s, and tracks which plots
 * belong to which tables.
 *
 * @author marottajb
 */
public class TopsoilProject {

    private MapProperty<Lambda, Number> lambdas = new SimpleMapProperty<>(FXCollections.observableHashMap());
    public MapProperty<Lambda, Number> lambdasProperty() {
        return lambdas;
    }
    public final ObservableMap<Lambda, Number> getLambdas() {
        return FXCollections.unmodifiableObservableMap(lambdas);
    }

    private ListProperty<DataTable> dataTables = new SimpleListProperty<>(FXCollections.observableArrayList());
    public ListProperty<DataTable> dataTablesProperty() {
        return dataTables;
    }
    public final ObservableList<DataTable> getDataTables() {
        return FXCollections.unmodifiableObservableList(dataTables);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilProject(DataTable... tables) {
        addDataTables(tables);
        resetAllLambdas();
        lambdas.addListener((MapChangeListener<? super Lambda, ? super Number>) c -> {
            ProjectManager.updatePlots();
        });
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Number getLambdaValue(Lambda lambda) {
        return lambdas.get(lambda);
    }

    public void setLambdaValue(Lambda lambda, Number value) {
        lambdas.put(lambda, value);
    }

    public void resetLambdaValue(Lambda lambda) {
        lambdas.put(lambda, lambda.getDefaultValue());
    }

    public void resetAllLambdas() {
        for (Lambda lambda : Lambda.values()) {
            resetLambdaValue(lambda);
        }
    }

    public void addDataTable(DataTable table) {
        dataTables.add(table);
    }

    public void addDataTables(DataTable... tables) {
        for (DataTable table : tables) {
            addDataTable(table);
        }
    }

    public void removeDataTable(DataTable table) {
        dataTables.remove(table);
    }

}
