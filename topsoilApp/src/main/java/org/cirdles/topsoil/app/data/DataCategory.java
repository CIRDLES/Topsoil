package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.data.node.BranchNode;
import org.cirdles.topsoil.app.data.node.DataNode;

import java.util.StringJoiner;

/**
 * @author marottajb
 */
public class DataCategory extends BranchNode<DataNode> {

    public DataCategory(String label, DataNode... children) {
        super(label, children);
    }

}
