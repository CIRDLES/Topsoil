package org.cirdles.topsoil.app.data.row;

import org.cirdles.topsoil.app.data.composite.DataComposite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;

public class DataSegment extends DataComposite<DataRow> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataSegment(String label) {
        super(label);
    }

    public DataSegment(String label, DataRow... rows) {
        this(label);
        children.addAll(asList(rows));
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DataSegment) {
            DataSegment other = (DataSegment) object;
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
