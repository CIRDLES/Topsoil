package org.cirdles.topsoil.app.progress.table;

import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.util.Alerter;
import org.cirdles.topsoil.app.util.ErrorAlerter;

/**
 * Created by benjaminmuldrow on 7/27/16.
 */
public class TopsoilTableCell extends TableCell<TopsoilDataEntry, Double> {

    private TextField textField;
    private Alerter alerter;

    public TopsoilTableCell() {
        super();

        this.alerter = new ErrorAlerter();

        // Handle key press events
        this.setOnKeyPressed(keyEvent -> {

            // confirm change
            if (keyEvent.getCode() == KeyCode.ENTER ||
                    keyEvent.getCode() == KeyCode.TAB) {

                // Make sure entry is valid
                Double newVal = getNumber(textField);
                if (newVal != null) {
                    // Only create undoable command if value was changed.
                    if (Double.compare(this.getItem(), newVal) != 0) {
                        TopsoilTableCellEditCommand cellEditCommand =
                                new TopsoilTableCellEditCommand(this,
                                        this.getItem(), newVal);
                        ((TopsoilTabPane) this.getScene()
                                .lookup("#TopsoilTabPane")).getSelectedTab()
                                .addUndo(cellEditCommand);
                    }

                    commitEdit(newVal);
                    updateItem(newVal, textField.getText().isEmpty());
                } else {
                    cancelEdit();
                    alerter.alert("Entry must be a number");
                }

            // cancel change
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
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
        this.setText(getItem().toString());
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
                    textField.setText(getItem().toString());
                    setText(null);
                    setGraphic(textField);
                }
            } else {
                setText(getItem().toString());
                setGraphic(null);
            }
        }
    }

    /**
     * create a text field to be used to edit cell value
     */
    private void generateTextField() {
        this.textField = new TextField();
        this.textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        this.textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // if new value contains non-numerics or is empty
            if (!newValue) {
                commitEdit(getNumber(this.textField));
            }
        });
    }

    /**
     * get the number contained in a textfield
     * @param textField textfield to be searched
     * @return Double value of contents or null if invalid entry
     */
    private Double getNumber(TextField textField) {
        Double result = null;
        try {
            result = new Double(textField.getText());
        } catch (NumberFormatException e) {
            //
        } finally {
            return result;
        }
    }

    public TopsoilDataEntry getDataEntry() {
        return this.getTableView().getItems().get(this.getIndex());
    }

    public int getColumnIndex() {
        return Integer.parseInt(this.getTableColumn().getId());
    }

}
