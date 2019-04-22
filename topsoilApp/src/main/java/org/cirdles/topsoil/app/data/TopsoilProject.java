package org.cirdles.topsoil.app.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.app.ProjectManager;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.plot.PlotType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a working state of Topsoil data. Maintains a list of active {@link DataTable}s, and tracks which plots
 * belong to which tables.
 *
 * @author marottajb
 */
public class TopsoilProject {

    private ObservableMap<Lambda, Number> lambdas = FXCollections.observableHashMap();
    private ObservableList<DataTable> dataTables = FXCollections.observableArrayList();

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

    public ObservableMap<Lambda, Number> getLambdas() {
        return FXCollections.unmodifiableObservableMap(lambdas);
    }

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

    public ObservableList<DataTable> getDataTables() {
        return FXCollections.unmodifiableObservableList(dataTables);
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
