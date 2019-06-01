package org.cirdles.topsoil.app.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.cirdles.topsoil.data.DataComponent;

public abstract class FXDataComponent<C extends DataComponent<C>> implements DataComponent<C> {

    private StringProperty title = new SimpleStringProperty();
    public final StringProperty titleProperty() {
        return title;
    }
    @Override
    public final String getTitle() {
        return title.get();
    }
    @Override
    public final void setTitle(String title) {
        this.title.set(title);
    }

    private BooleanProperty selected = new SimpleBooleanProperty();
    public final BooleanProperty selectedProperty() {
        return selected;
    }
    @Override
    public boolean isSelected() {
        return selected.get();
    }
    @Override
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    protected FXDataComponent(String title) {
        this(title, true);
    }

    protected FXDataComponent(String title, boolean selected) {
        this.title.set(title);
        this.selected.set(selected);
    }

    @Override
    public String toString() {
        return title.get();
    }
}
