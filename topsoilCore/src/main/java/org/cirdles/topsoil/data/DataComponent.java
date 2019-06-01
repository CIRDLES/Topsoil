package org.cirdles.topsoil.data;

import org.json.JSONString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public interface DataComponent<T extends DataComponent<T>> extends JSONString {

    String getTitle();

    void setTitle(String s);

    boolean isSelected();

    void setSelected(boolean b);

    List<? extends T> getChildren();

    default List<? extends T> getLeafChildren() {
        List<T> leaves = new ArrayList<>();
        List<? extends T> children = getChildren();
        if (children.size() == 0) {
            leaves.add((T) this);
        } else {
            for (T child : children) {
                leaves.addAll(child.getLeafChildren());
            }
        }
        return leaves;
    }

    default int countChildren() {
        return getChildren().size();
    }

    default T find(String title) {
        if (title == null) {
            return null;
        }
        if (title.equals(getTitle())) {
            return (T) this;
        }

        List<? extends T> children = getChildren();
        LinkedList<T> queue = new LinkedList<>();
        for (T child : children) {
            if (child.getTitle().equals(title)) {
                return child;
            }
            queue.add(child);
        }
        T child;
        while (! queue.isEmpty()) {
            child = queue.poll().find(title);
            if (child != null) {
                return child;
            }
        }
        return null;
    }

}
