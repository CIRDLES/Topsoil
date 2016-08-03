package org.cirdles.topsoil.app.progress;

import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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

        this.setOnKeyPressed(keyEvent -> {

            if (keyEvent.getCode() == KeyCode.ENTER ||
                    keyEvent.getCode() == KeyCode.TAB) {
                Double newVal = getNumber(textField);
                if (newVal != null) {
                    commitEdit(newVal);
                    updateItem(newVal, textField.getText().isEmpty());
                } else {
                    cancelEdit();
                    alerter.alert("Entry must be a number");
                }
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
            keyEvent.consume();
        });

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

}
