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

    private MapProperty<DataColumn, ObjectProperty<?>> dataPropertyMap =
            new SimpleMapProperty<>(FXCollections.observableHashMap());
    public MapProperty<DataColumn, ObjectProperty<?>> dataPropertyMapProperty() {
        return dataPropertyMap;
    }
    public final ObservableMap<DataColumn, ObjectProperty<?>> getDataPropertyMap() {
        return dataPropertyMap.get();
    }

    private BooleanProperty selected = new SimpleBooleanProperty(true);
    public BooleanProperty selectedProperty() {
        return selected;
    }
    public final boolean isSelected() {
        return true;
    }
    public final void setSelected(boolean b) {
        selected.set(b);
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

    public ObjectProperty<?> getValuePropertyForColumn(DataColumn column) {
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
