package org.cirdles.topsoil.app.data.composite;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A {@code DataComponent} is the basic building block for reading in structured model. When parsing delimited model
 * with moderately complex nested columns, the header structure is represented with a tree of {@code DataComponent}s
 * . Each node should have single header, and references a list of objects.
 *
 * @author marottajb
 */
public class DataComponent {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    protected static final String DEFAULT_LABEL = "";

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    protected transient StringProperty label;
    public StringProperty labelProperty() {
        if (label == null) {
            label = new SimpleStringProperty(DEFAULT_LABEL);
        }
        return label;
    }
    public String getLabel() { return labelProperty().get(); }
    public void setLabel(String label) { labelProperty().set(label); }

    protected transient BooleanProperty selected;
    public BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new SimpleBooleanProperty(true);
        }
        return selected;
    }
    public final boolean isSelected() {
        return selectedProperty().get();
    }
    public final void setSelected(boolean val) {
        selectedProperty().set(val);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataComponent() {
        this(DEFAULT_LABEL);
    }

    public DataComponent(String label) {
        if (label != null) {
            setLabel(label);
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public String toString() {
        return getLabel();
    }
}
