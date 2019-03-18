package org.cirdles.topsoil.app.data.column;

import org.cirdles.topsoil.app.data.composite.DataComposite;
import org.cirdles.topsoil.app.data.composite.DataComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a category of data columns. Can be composed of {@link DataColumn}s as well as other {@code DataCategory}s.
 *
 * @author marottajb
 */
public class DataCategory extends DataComposite<DataComponent> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataCategory(String label, DataComponent... children) {
        super(label, children);
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DataCategory) {
            DataCategory other = (DataCategory) object;
            if (! other.getLabel().equals(this.getLabel())) {
                return false;
            }
            if (other.isSelected() != this.isSelected()) {
                return false;
            }
            if (other.getChildren().size() != children.size()) {
                return false;
            }
            for (int i = 0; i < children.size(); i++) {
                if (! children.get(i).equals(other.getChildren().get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        List<Object> objects = new ArrayList<>();
        objects.add(getLabel());
        objects.add(isSelected());
        Collections.addAll(objects, children);
        return Objects.hash(objects.toArray());
    }

}
