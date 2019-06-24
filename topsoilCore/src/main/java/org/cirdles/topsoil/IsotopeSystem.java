package org.cirdles.topsoil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * Created by sbunce on 6/27/2016.
 */
public enum IsotopeSystem {

    //Isotope abbreviation, isotope name, array of default headers as strings
    GENERIC("Gen", "Generic"),

    UPB("UPb", "Uranium Lead"),

    UTH("UTh", "Uranium Thorium");

    /**
     * An abbreviation of the {@code IsotopeSystem}'s name.
     */
    private final String abbreviation;

    /**
     * The name of the {@code IsotopeSystem}.
     */
    private final String name;

    /**
     * A {@code List} of all {@code IsotopeSystem}s.
     */
    public static final List<IsotopeSystem> ISOTOPE_SYSTEMS;
    static {
	    ISOTOPE_SYSTEMS = Collections.unmodifiableList(Arrays.asList(
                GENERIC,
                UPB,
                UTH
	                                                                ));
    }

    //***********************
    // Constructors
    //***********************

    IsotopeSystem(String abbr, String name) {
        this.abbreviation = abbr;
        this.name = name;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Returns the appropriate {@code IsotopeSystem} for the given name, if one exists.
     *
     * @param name  String name
     * @return      associated IsotopeSystem
     */
    public static IsotopeSystem fromName( String name ) {
        for (IsotopeSystem i : values()) {
            if (name.equals(i.getName())) {
                return i;
            }
        }
        return null;
    }

    /**
     * Returns the abbreviated name of the {@code IsotopeSystem}.
     *
     * @return abbreviated String name
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Returns the name of the {@code IsotopeSystem}.
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
