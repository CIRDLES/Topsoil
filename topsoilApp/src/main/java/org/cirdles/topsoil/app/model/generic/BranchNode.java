package org.cirdles.topsoil.app.model.generic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author marottajb
 */
public class BranchNode<T extends DataNode> extends DataNode {

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

    public BranchNode() {
        this(DEFAULT_LABEL);
    }

    public BranchNode(String title) {
        super(title);
    }

    public BranchNode(String title, T... children) {
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
     * Returns the first {@code DataNode} within this branch with the specified title.
     *
     * @param   title
     *          the title of the node to find
     * @return  DataNode with title; else null
     */
    public DataNode find(String title) {
        return findIn(title, this);
    }

    public static DataNode findIn(String title, BranchNode<?> parent) {
        DataNode target = null;
        for (DataNode node : parent.getChildren()) {
            if (node.getLabel().equals(title)) {
                target = node;
                break;
            }
        }
        return target;
    }

    public List<? extends LeafNode> getLeafNodes() {
        List<LeafNode> leafNodes = new ArrayList<>();
        BranchNode<DataNode> branch;
        for (DataNode child : children) {
            if (child instanceof BranchNode) {
                branch = (BranchNode<DataNode>) child;
                leafNodes.addAll(branch.getLeafNodes());
            } else {
                leafNodes.add((LeafNode) child);
            }
        }
        return leafNodes;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof BranchNode<?>) {
            BranchNode<?> other = (BranchNode<?>) object;
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
        out.writeFields();
        out.writeObject(children);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.readFields();
        children.addAll((ArrayList<T>) in.readObject());
    }

}
