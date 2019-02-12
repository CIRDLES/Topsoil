package org.cirdles.topsoil.app.model.composite;

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

    private static final long serialVersionUID = 7042768990238389822L;
    protected static String DEFAULT_LABEL = "";

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    protected transient StringProperty label = new SimpleStringProperty(DEFAULT_LABEL);
    public StringProperty labelProperty() {
        return label;
    }
    public String getLabel() {
        return labelProperty().get();
    }
    public void setLabel(String label) {
        labelProperty().set(label);
    }

    protected transient BooleanProperty selected = new SimpleBooleanProperty(true);
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

    public DataComponent() {
        this(DEFAULT_LABEL);
    }

    public DataComponent(String label) {
        setLabel(label);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public String toString() {
        return label.get();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeChars(label.get());
        out.writeBoolean(selected.get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        label.set(String.valueOf(in.readObject()));
        selected.set(in.readBoolean());
    }

}
