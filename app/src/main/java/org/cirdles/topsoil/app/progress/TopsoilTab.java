package org.cirdles.topsoil.app.progress;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;

/**
 * Created by sbunce on 6/30/2016.
 */
public class TopsoilTab extends Tab {

    private TopsoilTable table;

    private final Label label;

    public TopsoilTab(TopsoilTable table) {
        label = new Label(table.getTitle());
        this.setGraphic(label);
        this.table = table;
        this.setContent(this.table.getTable());
        label.setText(table.getTitle());
        label.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TabNameDialog nameChange = new TabNameDialog(label.getText());
                label.setText(nameChange.getName());
            }
        });
    }

    public TableView getTable() {
        return table.getTable();
    }

    public TopsoilTable getTopsoilTable() {
        return table;
    }

    public IsotopeType getIsotopeType() {
        return table.getIsotopeType();
    }

}
