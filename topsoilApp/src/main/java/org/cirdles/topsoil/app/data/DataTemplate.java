package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.util.file.DataParser;
import org.cirdles.topsoil.app.util.file.DefaultDataParser;
import org.cirdles.topsoil.app.util.file.Squid3DataParser;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

/**
 * @author marottajb
 */
public enum DataTemplate {

    DEFAULT(DefaultDataParser.class),
    SQUID_3(Squid3DataParser.class);

    private Class<? extends DataParser> parserClass;

    DataTemplate(Class<? extends DataParser> clazz) {
        this.parserClass = clazz;
    }

    public DataParser getDataParser(Path path) {
        try {
            return parserClass.getConstructor(Path.class).newInstance(path);
        } catch (InstantiationException|IllegalAccessException|NoSuchMethodException|InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DataParser getDataParser(String content) {
        try {
            return parserClass.getConstructor(String.class).newInstance(content);
        } catch (InstantiationException|IllegalAccessException|NoSuchMethodException|InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
