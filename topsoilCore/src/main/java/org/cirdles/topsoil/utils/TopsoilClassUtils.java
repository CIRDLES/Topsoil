package org.cirdles.topsoil.utils;

import org.apache.commons.lang3.Validate;
import org.cirdles.topsoil.exception.MissingImplementationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class TopsoilClassUtils {

    private TopsoilClassUtils() {}

    public static <T> T instantiate(Class<T> clazz, Class[] argTypes, Object[] args) {
        for (int i = 0; i < argTypes.length; i++) {
            Validate.isInstanceOf(argTypes[i], args[i], "Invalid argument at index " + i);
        }

        try {
            Constructor<T> constructor = clazz.getConstructor(argTypes);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException|InstantiationException|IllegalAccessException| InvocationTargetException e) {
            throw new MissingImplementationException(
                    e,
                    clazz,
                    MissingImplementationException.Type.CONSTRUCTOR,
                    argTypes
            );
        }
    }

}
