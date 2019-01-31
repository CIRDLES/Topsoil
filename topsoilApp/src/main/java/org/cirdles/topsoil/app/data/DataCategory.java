package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.data.node.BranchNode;
import org.cirdles.topsoil.app.data.node.DataNode;

/**
 * @author marottajb
 */
public class DataCategory extends BranchNode<DataNode> {

    public DataCategory(String title, DataNode... children) {
        super(title, children);
    }

}
