package org.cirdles.topsoil.app.data.composite;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author marottajb
 */
public class DataComposite<T extends DataComponent> extends DataComponent {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    protected ObservableList<T> children = FXCollections.observableList(
            new ArrayList<>(),
            (T item) -> new Observable[]{
                    item.labelProperty(),
                    item.selectedProperty()
            });

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataComposite() {
        this(DEFAULT_LABEL);
    }

    public DataComposite(String title) {
        super(title);
    }

    public DataComposite(String title, T[] children) {
        this(title);
        this.children.addAll(asList(children));
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public ObservableList<T> getChildren() {
        return children;
    }

    /**
     * Returns the first {@code DataComponent} within this branch with the specified title.
     *
     * @param title     the title of the component to find
     *
     * @return          DataComponent with title; else null
     */
    public DataComponent find(String title) {
        return findIn(title, this);
    }

    /**
     * Returns the first {@code DataComponent} within the provided {@code DataComposite} with the specified title.
     *
     * @param title     the title of the component to find
     * @param parent    DataComposite
     *
     * @return          DataComponent with matching title
     */
    public static DataComponent findIn(String title, DataComposite<?> parent) {
        DataComponent target = null;
        if (parent.getLabel().equals(title)) {
            return parent;
        }
        for (DataComponent node : parent.getChildren()) {
            if (node.getLabel().equals(title)) {
                target = node;
                break;
            }
            if (node instanceof DataComposite) {
                target = findIn(title, (DataComposite) node);
                if (target != null) {
                    break;
                }
            }
        }
        return target;
    }

    /**
     * Counts the {@link DataLeaf} objects within this {@code DataComposite}.
     *
     * @return  int count
     */
    public int countLeafNodes() {
        int count = 0;
        DataComposite<DataComponent> branch;
        for (DataComponent child : children) {
            if (child instanceof DataComposite) {
                branch = (DataComposite<DataComponent>) child;
                count += branch.countLeafNodes();
            } else {
                count += 1;
            }
        }
        return count;
    }

    /**
     * Returns a {@code List} of the {@code DataLeaf} objects in this composite.
     *
     * @return  List of DataLeaves
     */
    public List<? extends DataLeaf> getLeafNodes() {
        List<DataLeaf> leafNodes = new ArrayList<>();
        for (DataComponent child : children) {
            if (child instanceof DataComposite) {
                leafNodes.addAll(((DataComposite<DataComponent>) child).getLeafNodes());
            } else {
                leafNodes.add((DataLeaf) child);
            }
        }
        return leafNodes;
    }

    /**
     * Returns the node depth of this composite.
     * <p>
     * For example, a composite with no children would have a depth of 1,
     * but if it had a child, its depth would be 2. If it had a grandchild, its depth would be 3.
     *
     * @return  int depth
     */
    public int getDepth() {
        if (children.isEmpty()) {
            return 0;
        }

        int maxDepth = 0;
        for (DataComponent child : children) {
            if (child instanceof DataComposite) {
                maxDepth = Math.max(maxDepth, ((DataComposite<DataComponent>) child).getDepth());
            }
        }
        return maxDepth + 1;
    }

}
