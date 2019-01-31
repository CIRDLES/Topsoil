package org.cirdles.topsoil.app.data.node;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.app.data.DataColumn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author marottajb
 */
public class BranchNode<T extends DataNode> extends DataNode {

    protected ListProperty<T> children = new SimpleListProperty<>(FXCollections.observableArrayList());
    public final ObservableList<T> getChildren() {
        return children.get();
    }

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
        this.children.addAll(children);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

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

}
