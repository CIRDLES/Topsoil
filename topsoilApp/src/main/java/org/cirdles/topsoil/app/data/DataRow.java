package org.cirdles.topsoil.app.data;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.cirdles.topsoil.app.data.node.LeafNode;

import java.util.Map;
import java.util.StringJoiner;

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

    @Override
    public boolean equals(Object object) {
        if (object instanceof DataRow) {
            DataRow other = (DataRow) object;
            if (! this.getLabel().equals(other.getLabel())) {
                System.out.println("label");
                return false;
            }
            if (this.isSelected() != other.isSelected()) {
                return false;
            }
            if (this.size() != other.size()) {
                System.out.println("size");
                return false;
            }
            // TODO Figure out how to test equivalence for each value
//            for (Map.Entry<DataColumn, ObjectProperty<Object>> entry : this.getDataPropertyMap().entrySet()) {
//                Object thisValue = entry.getValue().get();
//                Object thatValue = other.getValuePropertyForColumn(entry.getKey()).get();
//                if (thisValue.equals(thatValue)) {
//                    System.out.println(thisValue);
//                    System.out.println(thatValue);
//                    return false;
//                }
//            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<DataColumn, ObjectProperty<Object>> entry : this.dataPropertyMap.get().entrySet()) {
            joiner.add("\"" + entry.getKey().getLabel() + "\" => " + entry.getValue().get().toString());
        }
        return "DataRow(\"" + this.label.get() + "\"){ " + joiner.toString() + " }";
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private <T> ObjectProperty<T> putObjectInProperty(T obj) {
        return new SimpleObjectProperty<>(obj);
    }

}
