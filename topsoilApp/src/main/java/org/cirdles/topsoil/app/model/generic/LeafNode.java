package org.cirdles.topsoil.app.model.generic;

/**
 * @author marottajb
 */
public class LeafNode extends DataNode {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -9122019912022411715L;

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

    @Override
    public boolean equals(Object object) {
        if (object instanceof LeafNode) {
            LeafNode other = (LeafNode) object;
            if (! this.getLabel().equals(other.getLabel())) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[" + getLabel() + "]";
    }

}
