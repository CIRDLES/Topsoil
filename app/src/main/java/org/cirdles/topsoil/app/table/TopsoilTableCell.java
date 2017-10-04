package org.cirdles.topsoil.app.table;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.command.TableCellEditCommand;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification.NotificationType;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A customized {@code TableCell} used to display data. The {@code String} representation of the data is formatted, and
 * all events pertaining to the editing of a cell are handled here.
 *
 * @author Benjamin Muldrow
 *
 * @see TableCell
 * @see TopsoilDataTable
 */
public class TopsoilTableCell extends TableCell<TopsoilDataEntry, Double> {

    //***********************
    // Attributes
    //***********************

    /**
     * A {@code TextField} used for editing the value of the cell.
     */
    private TextField textField;

    /**
     * A {@code NumberFormat} for standardizing the displayed value in the cell.
     */
    private NumberFormat df;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new TopsoilTableCell. The NumberFormat is introduced, as well as specific KeyEvents handled and a
     * context menu supplied.
     */
    public TopsoilTableCell() {
        super();

        this.setFont(Font.font("Lucida Console"));
        this.setStyle("-fx-font-size:12");
        this.setAlignment(Pos.CENTER_RIGHT);

        this.df = DecimalFormat.getNumberInstance();
        //this.df.setMinimumFractionDigits(9);
        this.df.setMaximumFractionDigits(9);

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

    //***********************
    // Methods
    //***********************

    /**
     * Create a {@code TextField} to be used to edit cell value.
     */
    private void generateTextField() {
        this.textField = new TextField();
        this.textField.setFont(Font.font("Lucida Console"));
        this.textField.setStyle("-fx-font-size: 12");
        this.textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
    }

    /** {@inheritDoc} */
    @Override
    public void startEdit() {
        super.startEdit();
        generateTextField();
        this.setText(null);                                         // Sets the cell's text to null
        this.textField.setText(getItem().toString());    // Puts the data value into the TextField
        this.setGraphic(this.textField);                            // Sets the TextField as the cell's graphic
        this.textField.selectAll();
    }

    /** {@inheritDoc} */
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        this.setText(alignText(getItem().toString()));
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
                setText(alignText(getItem().toString()));
                setGraphic(null);
            }
        }
    }

    /**
     * Attempts to commit an edit. If the input is invalid, or if the value is the same as it was previously, the
     * edit is cancelled.
     */
    private void attemptToCommitEdit() {
        try {
            Double newVal = Double.valueOf(textField.getText());
            if (Double.compare(this.getItem(), newVal) == 0) {
                cancelEdit();
            } else {
                // TODO Test if this is a rho column by getting the variable property of this.getTableColumn()
                if (this.getColumnIndex() == 4 && (newVal > 1.0 || newVal < -1.0)) {
                    TopsoilNotification.showNotification(
                            NotificationType.ERROR,
                            "Invalid Correlation Coefficient",
                            "Rho values must be between -1.0 and 1.0."
                    );
                    cancelEdit();
                } else {
                    addUndo(this.getItem(), newVal);
                    commitEdit(newVal);
                }
            }
            selectNextCell();
        } catch (NumberFormatException e) {
            TopsoilNotification.showNotification(
                    NotificationType.ERROR,
                    "Invalid Value",
                    "Entry must be a number."
            );
            cancelEdit();
        }
    }

    /**
     * Remove trailing zeroes before setting the cell text value
     *
     * @param cellValue The text value to be checked and set in the cell
     */
    private String alignText(String cellValue) {
        if (cellValue.contains(".")) {
            String[] decimalPart = cellValue.split("\\.");
            if (decimalPart[1].length() <= 9) {
                StringBuilder builder = new StringBuilder();
                builder.append(cellValue);
                for (int i = decimalPart[1].length(); i < this.df.getMaximumFractionDigits(); i++) {
                    builder.append("  "); //padding with spaces to align decimals
                }
                cellValue = builder.toString();
            } else {
                cellValue = decimalPart[0] + "." + decimalPart[1].substring(0, 9);
            }
        }
        return cellValue;
    }

    /**
     * Creates an undoable {@code Command} for a value change, then adds it to the {@code UndoManager}.
     *
     * @param oldVal    the old Double data value
     * @param newVal    the new Double data value
     */
    private void addUndo(Double oldVal, Double newVal) {
        TableCellEditCommand cellEditCommand = new TableCellEditCommand(this, oldVal, newVal);
        ((TopsoilTabPane) this.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(cellEditCommand);
    }

    /**
     * Returns the {@code TopsoilDataEntry} that the data in the cell belongs to.
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
        return ((TopsoilTabPane) this.getScene().lookup("#TopsoilTabPane")).getSelectedTab().getTableController()
                                                                           .getColumnIndex(this.getTableColumn());
    }

    /**
     * Selects the next logical cell, depending on the button pressed. If the user presses 'Tab', the next cell to the
     * right and down is selected. If the user presses 'Shift+Tab', the next cell to the left and up is selected.
     */
    private void selectNextCell() {
        if (this.getColumnIndex() == this.getTableView().getColumns().size() - 1) {
            if (this.getIndex() != this.getTableView().getItems().size() - 1) {
                this.getTableView().getSelectionModel().select(this.getIndex() + 1,
                                                               this.getTableView().getColumns().get(0));
            }
        } else {
            this.getTableView().getSelectionModel().select(this.getIndex(), this.getTableView().getColumns()
                                                                                .get(this.getColumnIndex() + 1));
        }
    }

}
