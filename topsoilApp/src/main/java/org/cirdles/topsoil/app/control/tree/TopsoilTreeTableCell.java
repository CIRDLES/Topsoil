package org.cirdles.topsoil.app.control.tree;

import javafx.collections.MapChangeListener;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;

import java.net.MalformedURLException;

public class TopsoilTreeTableCell<T> extends TextFieldTreeTableCell<DataComponent, T> {

    private static Image INFO_IMAGE;
    static {
        ResourceExtractor re = new ResourceExtractor(TopsoilTreeTableCell.class);
        try {
            INFO_IMAGE = new Image(re.extractResourceAsPath("error-bubble.png").toUri().toURL().toString(),
                    16.0, 16.0, false, false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private DataColumn<T> dataColumn;
    private DataTable table;
    private Tooltip tooltip;
    private ImageView infoGraphic;

    public TopsoilTreeTableCell(DataColumn<T> dataColumn, DataTable table) {
        super(dataColumn.getStringConverter());
        this.dataColumn = dataColumn;
        this.table = table;
        this.tooltip = new Tooltip();
        tooltip.setFont(Font.font("Arimo-Regular", 12.0));

        if (INFO_IMAGE != null) {
            infoGraphic = new ImageView(INFO_IMAGE);
            infoGraphic.setCache(true);
            infoGraphic.setCacheHint(CacheHint.SPEED);
        }

        table.variableColumnMapProperty().addListener((MapChangeListener<? super Variable<?>, ? super DataColumn<?>>) c -> {
            updateValidity();
        });

        if (Number.class.equals(dataColumn.getType())) {
            this.setAlignment(Pos.CENTER_RIGHT);
        } else {
            this.setAlignment(Pos.CENTER_LEFT);
        }

        updateValidity();
    }

    @Override
    public void startEdit() {
        if (! isEditable()
                || ! getTreeTableView().isEditable()
                || ! getTableColumn().isEditable()) {
            return;
        }
        super.startEdit();

        if (!isEmpty()) {
            super.startEdit();
            TextField textField = (TextField) this.getGraphic();
            textField.setText(getItem().toString());
        }
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        updateValidity();
    }

    @Override
    public void commitEdit(T newValue) {
        if (newValue == null || newValue.equals(Double.NaN)) {
            super.cancelEdit();
            updateValidity();
            return;
        }
        Event.fireEvent(this, new CellEditEvent(
                ((DataRow) getTreeTableRow().getTreeItem().getValue()).getValueForColumn(dataColumn),
                getItem(),
                newValue
        ));
        super.commitEdit(newValue);
    }

    private void updateValidity() {
        if (! isEmpty()) {
            Variable<?> variable = table.getVariableForColumn(dataColumn);
            if (variable == Variables.RHO) {
                Number n = (Number) getItem();
                if (n != null) {
                    double value = n.doubleValue();
                    if (value < -1 || value > 1) {
                        setInvalid(true, "Rho values must be in the range [-1,1].\nThis value will be 0.0 in plots.");
                    } else {
                        setInvalid(false, null);
                    }
                }
            } else {
                setInvalid(false, null);
            }
        }
    }

    private void setInvalid(boolean invalid, String message) {
        if (invalid) {
            if (message != null) {
                tooltip.setText(message);
                setTooltip(tooltip);
            }
            this.setGraphic(infoGraphic);
            this.setStyle("-fx-background-color: lightcoral");
        } else {
            setTooltip(null);
            this.setGraphic(null);
            this.setStyle("");
        }
    }

}
