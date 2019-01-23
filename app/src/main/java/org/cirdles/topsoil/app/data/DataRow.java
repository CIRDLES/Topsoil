package org.cirdles.topsoil.app.data;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.cirdles.topsoil.app.data.node.LeafNode;

import java.util.Map;

/**
 * @author marottajb
 */
public class DataRow extends LeafNode {

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private MapProperty<DataColumn, Object> dataValueMap = new SimpleMapProperty<>(FXCollections.observableHashMap());
    public MapProperty<DataColumn, Object> dataValueMapProperty() {
        return dataValueMap;
    }
    public final ObservableMap<DataColumn, Object> getDataValueMap() {
        return dataValueMap.get();
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataRow(String label) {
        super(label);
    }

    public DataRow(String label, Map<DataColumn, Object> valMap) {
        this(label);
        valMap.forEach((col, value) -> {
            dataValueMap.putIfAbsent(col, value);
        });
        dataValueMap.setValue(FXCollections.observableMap(valMap));
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Object getValueForColumn(DataColumn column) {
        return dataValueMap.get(column);
    }

    public int size() {
        return dataValueMap.size();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

}
