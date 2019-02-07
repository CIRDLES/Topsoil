package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.model.node.BranchNode;
import org.cirdles.topsoil.app.model.node.DataNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author marottajb
 */
public class ColumnTree extends BranchNode<DataNode> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ColumnTree(BranchNode<DataNode> root) {
        this(root.getChildren());
    }

    public ColumnTree(List<? extends DataNode> topLevel) {
        this.getChildren().addAll(topLevel);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public List<DataColumn> getLeafNodes() {
        return leafHelper(super.getLeafNodes());
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private <T> List<DataColumn> leafHelper(List<T> leaves) {
        List<DataColumn> columns = new ArrayList<>();
        for (T leaf : leaves) {
            if (leaf instanceof DataColumn) {
                columns.add((DataColumn) leaf);
            } else {
                // @TODO Probably better to throw an exception here
                return null;
            }
        }
        return columns;
    }


}
