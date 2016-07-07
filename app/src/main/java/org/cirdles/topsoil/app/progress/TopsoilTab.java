package org.cirdles.topsoil.app.progress;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;

/**
 * Created by sbunce on 6/30/2016.
 */
public class TopsoilTab extends Tab {

    private String tabName = new String("Unnamed Tab");
    private TopsoilTable table;

    private final Label label = new Label(tabName);

    public TopsoilTab(TopsoilTable table) {
        this.setGraphic(label);
        this.table = table;
        this.setContent(this.table);
        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    TabNameDialog nameChange = new TabNameDialog(tabName);
                    tabName = nameChange.getName();
                    label.setText(tabName);
                }
            }
        });
    }

    public String getTabName() {
        return tabName;
    }

    public TopsoilTable getTopsoilTable() {
        return table;
    }

}
