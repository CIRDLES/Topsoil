package org.cirdles.topsoil.variable;

import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.uncertainty.Uncertainty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A {@code Variable} for a {@code Boolean} data type.
 *
 * @author marottajb
 */
public enum BooleanVariable implements Variable<Boolean> {

    SELECTED("selected", "selected");

    private String name;
    private String abbr;

    BooleanVariable(String name, String abbreviation) {
        this.name = name;
        this.abbr = abbreviation;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAbbreviation() {
        return abbr;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.defaultWriteObject();
//    }
//
//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//    }

}
