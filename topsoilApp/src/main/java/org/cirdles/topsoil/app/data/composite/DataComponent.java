package org.cirdles.topsoil.app.data.composite;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A {@code DataComponent} is the basic building block for reading in structured model. When parsing delimited model
 * with moderately complex nested columns, the header structure is represented with a tree of {@code DataComponent}s
 * . Each node should have single header, and references a list of objects.
 *
 * @author marottajb
 */
public abstract class DataComponent implements Serializable {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -5314772239757317389L;
    protected static String DEFAULT_LABEL = "";

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
        setLabel(label != null ? label : "");
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public boolean equals(Object object) {
        if (object instanceof DataComponent) {
            DataComponent other = (DataComponent) object;
            if (! this.getLabel().equals(other.getLabel())) {
                return false;
            }
            if (this.isSelected() && !other.isSelected()
                || !this.isSelected() && other.isSelected()) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return label.get();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(getLabel());
        out.writeBoolean(isSelected());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setLabel(in.readUTF());
        setSelected(in.readBoolean());
    }

}
