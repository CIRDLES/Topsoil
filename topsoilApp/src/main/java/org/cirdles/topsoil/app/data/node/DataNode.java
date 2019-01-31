package org.cirdles.topsoil.app.data.node;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A {@code DataNode} is the basic building block for reading in structured data. When parsing delimited data
 * with moderately complex nested columns, the header structure is represented with a tree of {@code DataNode}s
 * . Each node should have single header, and references a list of objects.
 *
 * @author marottajb
 */
public abstract class DataNode {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    protected static String DEFAULT_LABEL = "";

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    protected StringProperty label = new SimpleStringProperty(DEFAULT_LABEL);
    public StringProperty labelProperty() {
        return label;
    }
    public String getLabel() {
        return labelProperty().get();
    }
    public void setLabel(String label) {
        labelProperty().set(label);
    }

    protected BooleanProperty selected = new SimpleBooleanProperty(true);
    public BooleanProperty selectedProperty() {
        return selected;
    }
    public final boolean isSelected() {
        return selected.get();
    }
    public final void setSelected(boolean val) {
        selected.set(val);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataNode() {
        this(DEFAULT_LABEL);
    }

    public DataNode(String label) {
        setLabel(label);
    }

    @Override
    public String toString() {
        return label.get();
    }

}
