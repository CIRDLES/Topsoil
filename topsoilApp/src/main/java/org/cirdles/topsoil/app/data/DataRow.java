package org.cirdles.topsoil.app.data;

import javafx.beans.property.*;
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

    private MapProperty<DataColumn, ObjectProperty<Object>> dataPropertyMap =
            new SimpleMapProperty<>(FXCollections.observableHashMap());
    public MapProperty<DataColumn, ObjectProperty<Object>> dataPropertyMapProperty() {
        return dataPropertyMap;
    }
    public final ObservableMap<DataColumn, ObjectProperty<Object>> getDataPropertyMap() {
        return dataPropertyMap.get();
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataRow(String label) {
        super(label);
    }

    public DataRow(String label, Map<DataColumn, Object> valMap) {
        this(label);
        dataPropertyMap.setValue(FXCollections.observableHashMap());
        valMap.forEach((col, value) -> {
            dataPropertyMap.putIfAbsent(col, putObjectInProperty(value));
        });
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public ObjectProperty<Object> getValuePropertyForColumn(DataColumn column) {
        return dataPropertyMap.get(column);
    }

    public int size() {
        return dataPropertyMap.size();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private <T> ObjectProperty<T> putObjectInProperty(T obj) {
        return new SimpleObjectProperty<>(obj);
    }

}
