package org.cirdles.topsoil.app.progress.table;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.util.Alerter;
import org.cirdles.topsoil.app.util.ErrorAlerter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author benjaminmuldrow
 *
 */
public class TopsoilTableCell extends TableCell<TopsoilDataEntry, Double> {

    private TextField textField;
    private Alerter alerter;
    private NumberFormat df;

    TopsoilTableCell() {
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
            if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.TAB) {

                // Make sure entry is valid
                Double newVal = getNumber(this.textField);
                if (newVal == null) {
                    alerter.alert("Entry must be a number.");
                    cancelEdit();
                } else if (Double.compare(this.getItem(), newVal) != 0) {
                    commitEdit(newVal);
                    addUndo(this.getItem(), newVal);
                } else {
                    cancelEdit();
                }
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

    @Override
    public void startEdit() {
        super.startEdit();
        generateTextField();
        this.setText(null);
        this.textField.setText(getItem().toString());
        this.setGraphic(this.textField);
        this.textField.selectAll();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        this.setText(df.format(this.getItem()));
        this.setGraphic(null);
    }

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

    private void addUndo(Double oldVal, Double newVal) {
        TableCellEditCommand cellEditCommand = new TableCellEditCommand(this, oldVal, newVal);
        ((TopsoilTabPane) this.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(cellEditCommand);
    }

    /**
     * create a text field to be used to edit cell value
     */
    private void generateTextField() {
        this.textField = new TextField();
        this.textField.setFont(Font.font("Monospaced"));
        this.textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
    }

    /**
     * get the number contained in a textfield
     * @param textField textfield to be searched
     * @return Double value of contents or null if invalid entry
     */
    private Double getNumber(TextField textField) {
        try {
            return Double.valueOf(textField.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    TopsoilDataEntry getDataEntry() {
        return this.getTableView().getItems().get(this.getIndex());
    }

    int getColumnIndex() {
        return Integer.parseInt(this.getTableColumn().getId());
    }

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
