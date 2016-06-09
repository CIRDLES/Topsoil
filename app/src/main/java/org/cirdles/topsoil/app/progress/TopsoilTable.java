import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Created by benjaminmuldrow on 5/25/16.
 */
public class TopsoilTable extends TableView<DataEntry> {

    public TopsoilTable(ObservableList<DataEntry> data) {
        super();
        super.setItems(data);
    }

    /**
     * Clear data from table
     */
    public void clear() {
        this.getItems().clear();
        this.getColumns().clear();
    }

}