package org.cirdles.topsoil.constant;

/**
 * @author marottajb
 */
public interface Constant<T> {

    String getName();

    String getKeyString();

    Class<T> getType();

    T getDefaultValue();

    boolean match(Object value);

}
