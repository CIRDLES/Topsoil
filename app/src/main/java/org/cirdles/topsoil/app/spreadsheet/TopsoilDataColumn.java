package org.cirdles.topsoil.app.spreadsheet;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import org.cirdles.topsoil.variable.Variable;

/**
 * @author marottajb
 */
public class TopsoilDataColumn extends SimpleListProperty<DoubleProperty> {

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private StringProperty name;
    public StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty(null);

            name.addListener(c -> {
                if (hasVariable()) {
                    setColumnHeader("(" + getVariable().getAbbreviation() + ") " + getName());
                } else {
                    setColumnHeader(getName());
                }
            });
        }
        return name;
    }
    @Override public String getName() {
        return nameProperty().get();
    }
    public void setName(String name) {
        nameProperty().set(name);
    }

    private StringProperty columnHeader;
    public StringProperty columnHeaderProperty() {
        if (columnHeader == null) {
            if (hasVariable()) {
                columnHeader = new SimpleStringProperty("(" + getVariable().getAbbreviation() + ") " + getName());
            } else {
                columnHeader = new SimpleStringProperty(getName());
            }
        }
        return columnHeader;
    }
    public String getColumnHeader() {
        return columnHeaderProperty().get();
    }
    private void setColumnHeader(String s) {
        columnHeaderProperty().set(s);
    }

    /**
     * The {@code Variable} that is set to this column, if one exists.
     */
    private ObjectProperty<Variable> variable;
    public ObjectProperty<Variable> variableProperty() {
        if (variable == null) {
            variable = new SimpleObjectProperty<>(null);
            variable.addListener(c -> {
                if (variable.get() == null) {
                    hasVariableProperty().set(false);

                } else {
                    hasVariableProperty().set(true);
                }
            });
        }

        return variable;
    }
    public Variable getVariable() {
        return variableProperty().get();
    }
    public void setVariable(Variable v) {
        variableProperty().set(v);
    }

    private BooleanProperty hasVariable;
    public BooleanProperty hasVariableProperty() {
        if (hasVariable == null) {
            hasVariable = new SimpleBooleanProperty(variableProperty().get() != null);
            hasVariableProperty().addListener(c -> {
                if (hasVariable()) {
                    setColumnHeader("(" + getVariable().getAbbreviation() + ") " + getName());
                } else {
                    setColumnHeader(getName());
                }
            });
        }
        return hasVariable;
    }
    public Boolean hasVariable() {
        return hasVariableProperty().get();
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilDataColumn() {
        this("Untitled Column");
    }

    public TopsoilDataColumn(DoubleProperty... properties) {
        this("Untitled Column", properties);
    }

    public TopsoilDataColumn(String name, DoubleProperty... properties) {
        super(FXCollections.observableArrayList());
        setName(name);
        this.addAll(properties);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Double[] getData() {
        Double[] data = new Double[size()];
        for (int i = 0; i < size(); i++) {
            data[i] = this.get(i).get();
        }
        return data;
    }
}
