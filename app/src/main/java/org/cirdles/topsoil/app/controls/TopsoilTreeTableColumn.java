package org.cirdles.topsoil.app.controls;

import javafx.scene.control.TreeTableColumn;
import org.cirdles.topsoil.app.data.node.DataNode;

/**
 * @author marottajb
 */
public class TopsoilTreeTableColumn<S, T> extends TreeTableColumn<S, T> {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private DataNode dataNode;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilTreeTableColumn(String text, DataNode dataNode) {
        super(text);
        this.dataNode = dataNode;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public DataNode getDataNode() {
        return this.dataNode;
    }

}
