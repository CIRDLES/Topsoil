package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.data.node.BranchNode;

/**
 * @author marottajb
 */
public class DataCategory extends BranchNode<DataColumn> {

    public DataCategory(String title, DataColumn... columns) {
        super(title, columns);
    }

}
