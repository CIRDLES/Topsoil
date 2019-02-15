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

    private static final long serialVersionUID = 4826726036796946067L;

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
            if (! this.getLabel().equals(other.getLabel())) {
                return false;
            }
            if (this.getChildren().size() != other.getChildren().size()) {
                return false;
            }
            for (int i = 0; i < this.getChildren().size(); i++) {
                Object thisObject = this.getChildren().get(i);
                Object thatObject = other.getChildren().get(i);
                if (! thisObject.equals(thatObject)) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void writeObject(ObjectOutputStream out) throws IOException {
        ObjectOutputStream.PutField fields = out.putFields();
        fields.put("children", children);
        out.writeFields();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = in.readFields();
        children = (ArrayList<T>) fields.get("children", null);
    }

}
