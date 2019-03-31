package org.cirdles.topsoil.variable;

import java.util.Date;

/**
 * A {@code Variable} for a {@code Date} data type.
 *
 * @author marottajb
 */
public enum DateVariable implements Variable<Date> {

    TIMESTAMP("Timestamp", "Time");

    private String name;
    private String abbr;

    DateVariable(String name, String abbreviation) {
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

}
