package org.cirdles.topsoil.app.data.composite;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author marottajb
 */
public class DataComposite<T extends DataComponent> extends DataComponent {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -7225522174882434258L;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    protected ArrayList<T> children = new ArrayList<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataComposite() {
        this(DEFAULT_LABEL);
    }

    public DataComposite(String title) {
        super(title);
    }

    public DataComposite(String title, T... children) {
        this(title);
        this.children.addAll(asList(children));
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public ArrayList<T> getChildren() {
        return children;
    }

    /**
     * Returns the first {@code DataComponent} within this branch with the specified title.
     *
     * @param   title
     *          the title of the node to find
     * @return  DataComponent with title; else null
     */
    public DataComponent find(String title) {
        return findIn(title, this);
    }

    public static DataComponent findIn(String title, DataComposite<?> parent) {
        DataComponent target = null;
        for (DataComponent node : parent.getChildren()) {
            if (node.getLabel().equals(title)) {
                target = node;
                break;
            }
        }
        return target;
    }

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

    public List<? extends DataLeaf> getLeafNodes() {
        List<DataLeaf> leafNodes = new ArrayList<>();
        DataComposite<DataComponent> branch;
        for (DataComponent child : children) {
            if (child instanceof DataComposite) {
                branch = (DataComposite<DataComponent>) child;
                leafNodes.addAll(branch.getLeafNodes());
            } else {
                leafNodes.add((DataLeaf) child);
            }
        }
        return leafNodes;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DataComposite<?>) {
            DataComposite<?> other = (DataComposite<?>) object;
            if (! super.equals(other)) {
                return false;
            }
            if (this.getChildren().size() != other.getChildren().size()) {
                return false;
            }
            for (int i = 0; i < this.getChildren().size(); i++) {
                if (! this.getChildren().get(i).equals(other.getChildren().get(i))) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
