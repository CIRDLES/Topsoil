package org.cirdles.topsoil.app.data;

import javafx.beans.property.*;

/**
 * @author marottajb
 */
public class ObservableDataRow extends TopsoilDataList {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String DEFAULT_ROW_ID = "";

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    /**
     * A {@code String} identifying the row.
     * <p>
     * The row may or may not have an ID, and it need not be unique. The ID is a name provided by the user to tag
     * data values.
     */
    private StringProperty rowID = new SimpleStringProperty();
    public StringProperty rowIDProperty() {
        return rowID;
    }
    public String getRowID() {
        return rowID.get();
    }
    public void setRowID(String s) {
        if (s != null) {
            rowID.set(s);
        }
    }

    /**
     * Whether or not this row should be marked as active in plots.
     */
    private BooleanProperty selected = new SimpleBooleanProperty();
    public BooleanProperty selectedProperty() {
        return selected;
    }
    public boolean isSelected() {
        return selected.get();
    }
    public void setSelected(boolean b) {
        selected.set(b);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs an empty data row.
     */
    public ObservableDataRow() {
        this(DEFAULT_ROW_ID, true);
    }

    /**
     * Constructs a new data row containing the provided {@code Double} values.
     *
     * @param values    sequence of Double values
     */
    public ObservableDataRow(Double... values) {
        this("", true, values);
    }

    /**
     * Constructs a new data row with the provided {@code String} id, selected condition, and {@code Double} values.
     *
     * @param id    String row id
     * @param selected  boolean sets selected property
     * @param values    sequence of Double values
     */
    public ObservableDataRow(String id, boolean selected, Double... values) {
        super(values);
        setRowID(id);
        setSelected(selected);
    }
}

