package org.cirdles.topsoil.app.data.node;

/**
 * @author marottajb
 */
public class LeafNode extends DataNode {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public LeafNode() {
        this(DEFAULT_LABEL);
    }

    public LeafNode(String title) {
        super();
        setLabel(title);
    }

}
