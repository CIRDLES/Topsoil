package org.cirdles.topsoil.app.plot;

/**
 * Provides a format for plot uncertainty, including a name and a {@code Double} value.
 *
 * @author Jake Marotta
 */
public class UncertaintyFormat {

    /**
     * The name of the uncertainty format.
     */
    private String name;

    /**
     * The {@code Double} value of the uncertainty format.
     */
    private Double value;

    /**
     * Constructs a new {@code UncertaintyFormat} with the specified name and value.
     *
     * @param name  String name
     * @param value Double value
     */
    public UncertaintyFormat(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the name of the {@code UncertaintyFormat}.
     *
     * @return  String name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of the {@code UncertaintyFormat}.
     *
     * @return  Double value
     */
    public Double getValue() {
        return value;
    }
}
