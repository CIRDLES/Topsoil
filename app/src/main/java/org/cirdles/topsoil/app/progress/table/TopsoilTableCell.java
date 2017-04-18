package org.cirdles.topsoil.app.progress.table;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.table.command.TableCellEditCommand;
import org.cirdles.topsoil.app.util.Alerter;
import org.cirdles.topsoil.app.util.ErrorAlerter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A customized <tt>TableCell</tt> used to display data. The String representation of the data is formatted, and all
 * events pertaining to the editing of a cell are handled here.
 *
 * @author benjaminmuldrow
 *
 * @see TableCell
 * @see TopsoilTable
 */
public class TopsoilTableCell extends TableCell<TopsoilDataEntry, Double> {

    private TextField textField;
    private Alerter alerter;
    private NumberFormat df;

    /**
     * Constructs a new TopsoilTableCell. The NumberFormat is introduced, as well as specific KeyEvents handled and a
     * context menu supplied.
     */
    public TopsoilTableCell() {
        super();

        this.setFont(Font.font("Monospaced"));
        this.setAlignment(Pos.TOP_RIGHT);

        this.df = DecimalFormat.getNumberInstance();
        this.df.setMinimumFractionDigits(9);
        this.df.setMaximumFractionDigits(9);

        this.alerter = new ErrorAlerter();

        //Handle key press events
        this.setOnKeyPressed(keyEvent -> {
            // confirm change
            if (keyEvent.getCode() == KeyCode.ENTER) {

                attemptToCommitEdit();
                selectNextCell();

                // cancel change
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
                selectNextCell();
            }

            keyEvent.consume();
        });

        // Context Menu
        this.setOnContextMenuRequested(menuEvent -> {
            this.setContextMenu(new TopsoilTableCellContextMenu(this));
            menuEvent.consume();
        });
    }

    /** {@inheritDoc} */
    @Override
    public void startEdit() {
        super.startEdit();
        generateTextField();
        this.setText(null);                             // Sets the cell's text to null
        this.textField.setText(getItem().toString());   // Puts the data value into the TextField
        this.setGraphic(this.textField);                // Sets the TextField as the cell's graphic
        this.textField.selectAll();
    }

    /** {@inheritDoc} */
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        this.setText(df.format(this.getItem()));
        this.setGraphic(null);
    }

    /** {@inheritDoc} */
    @Override
    public void updateItem(Double item, boolean isEmpty) {
        super.updateItem(item, isEmpty);

        if (isEmpty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(this.getItem().toString());
                }
                setText(null);
                setGraphic(this.textField);
            } else {
                setText(this.df.format(getItem()));
                setGraphic(null);
            }
        }
    }

    /**
     * Creates an undoable <tt>Command</tt> for a value change, then adds it to the <tt>UndoManager</tt>.
     *
     * @param oldVal    the old Double data value
     * @param newVal    the new Double data value
     */
    private void addUndo(Double oldVal, Double newVal) {
        TableCellEditCommand cellEditCommand = new TableCellEditCommand(this, oldVal, newVal);
        ((TopsoilTabPane) this.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(cellEditCommand);
    }

    /**
     * Create a text field to be used to edit cell value.
     */
    private void generateTextField() {
        this.textField = new TextField();
        this.textField.setFont(Font.font("Monospaced"));
        this.textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
    }

    /**
     * Attempts to commit an edit. If the input is invalid, or if the value is the same as it was previously, the
     * edit is cancelled.
     */
    private void attemptToCommitEdit() {
        try {
            Double newVal = Double.valueOf(textField.getText());
            if (Double.compare(this.getItem(), newVal) != 0) {
                addUndo(this.getItem(), newVal);
                commitEdit(newVal);
            } else {
                cancelEdit();
            }
            selectNextCell();
        } catch (NumberFormatException e) {
            alerter.alert("Entry must be a number.");
            cancelEdit();
        }
    }

    /**
     * Returns the <tt>TopsoilDataEntry</tt> that the data in the cell belongs to.
     *
     * @return  TopsoilDataEntry
     */
    public TopsoilDataEntry getDataEntry() {
        return this.getTableView().getItems().get(this.getIndex());
    }

    /**
     * Returns the index of the column this cell belongs to.
     *
     * @return  int column index
     */
    public int getColumnIndex() {
        return Integer.parseInt(this.getTableColumn().getId());
    }

    /**
     * Selects the next logical cell, depending on the button pressed. If the user presses Tab, the next cell to the
     * right and down is selected. If the user presses Shift+Tab, the next cell to the left and up is selected.
     */
    private void selectNextCell() {
        if (this.getColumnIndex() == this.getTableView().getColumns().size() - 1) {
            if (this.getIndex() != this.getTableView().getItems().size() - 1)
                this.getTableView().getSelectionModel().select(this.getIndex() + 1,
                                                               this.getTableView().getColumns().get(0));
        } else {
            this.getTableView().getSelectionModel().select(this.getIndex(), this.getTableView().getColumns()
                                                                                .get(this.getColumnIndex() + 1));
        }
    }

}
