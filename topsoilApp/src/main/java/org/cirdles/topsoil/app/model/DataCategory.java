package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.model.node.BranchNode;
import org.cirdles.topsoil.app.model.node.DataNode;

/**
 * @author marottajb
 */
public class DataCategory extends BranchNode<DataNode> {

    public DataCategory(String label, DataNode... children) {
        super(label, children);
    }

}
