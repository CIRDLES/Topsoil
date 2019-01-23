package org.cirdles.topsoil.app.data.node;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedHashMap;

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

    public int countTotalLeafNodes() {
        int count = 0;
        for (DataNode child : children) {
            if (child instanceof BranchNode) {
                count += ((BranchNode) child).countTotalLeafNodes();
            } else {
                count += children.getSize();
            }
        }
        return count;
    }

    public int countTotalNodes() {
        int count = 0;
        for (DataNode child : children) {
            if (child instanceof BranchNode) {
                count += (((BranchNode) child).countTotalNodes() + 1);
            } else {
                count += (children.getSize() + 1);
            }
        }
        return count;
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

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//



}
