package org.cirdles.topsoil.app.data.composite;

/**
 * @author marottajb
 */
public class DataLeaf extends DataComponent {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = 8156642754912935606L;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataLeaf() {
        this(DEFAULT_LABEL);
    }

    public DataLeaf(String title) {
        super();
        setLabel(title);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DataLeaf) {
            DataLeaf other = (DataLeaf) object;
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
