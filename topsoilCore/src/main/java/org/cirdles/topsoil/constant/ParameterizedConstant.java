package org.cirdles.topsoil.constant;

/**
 * A base class for defining objects which represent constant values that may be mapped to variable values of type
 * {@code T}.
 *
 * For example, plot properties are pre-defined as static
 * {@link org.cirdles.topsoil.plot.PlotProperties.Property} objects that can be mapped to associated values. A
 * {@code Property} of type {@code String}, such as {@link org.cirdles.topsoil.plot.PlotProperties#TITLE},
 *
 * @param <T>   the type of the values associated with the ParameterizedConstant
 */
public abstract class ParameterizedConstant<T> implements Constant<T> {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    protected String name;
    protected String key;
    protected T defaultValue;
    protected Class<T> type;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a new {@code ParameterizedConstant} object with the specified name, string key, and default value.
     * The type of the constant will be the type of the provided default value.
     *
     * @param name          String name
     * @param key           String key used for JS maps
     * @param defaultValue  default value of type T
     */
    protected ParameterizedConstant(String name, String key, T defaultValue) {
        this.name = name;
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = (Class<T>) defaultValue.getClass();
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Returns the readable name of this constant.
     *
     * @return  String name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@code String} key used for JS maps.
     *
     * @return  String key
     */
    public String getKeyString() {
        return key;
    }

    /**
     * Returns the {@code Class} of the parameterized type of this constant value.
     *
     * @return  Class of type T
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Returns the default value of this constant value.
     *
     * @return  default value of type T
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns true if the provided {@code Object} is of the same type as this constant value.
     *
     * @param value     Object to test
     *
     * @return          true if Object is of type T
     */
    public boolean match(Object value) {
        return type.isInstance(value);
    }

}
