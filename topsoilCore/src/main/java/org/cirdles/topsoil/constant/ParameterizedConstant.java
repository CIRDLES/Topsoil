package org.cirdles.topsoil.constant;

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

    protected ParameterizedConstant(String name, String key, T defaultValue) {
        this.name = name;
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = (Class<T>) defaultValue.getClass();
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public String getName() {
        return name;
    }

    public String getKeyString() {
        return key;
    }

    public Class<T> getType() {
        return type;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean match(Object value) {
        return type.isInstance(value);
    }

}
