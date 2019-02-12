package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.util.file.DataParser;
import org.cirdles.topsoil.app.util.file.DefaultDataParser;
import org.cirdles.topsoil.app.util.file.Squid3DataParser;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

/**
 * Each template represents a possible format for imported data, and has a {@code DataParser} associated with it.
 *
 * @author marottajb
 */
public enum DataTemplate {

    DEFAULT(DefaultDataParser.class),
    SQUID_3(Squid3DataParser.class);

    private Class<? extends DataParser> parserClass;

    DataTemplate(Class<? extends DataParser> clazz) {
        this.parserClass = clazz;
    }

    /**
     * Returns a new instance of the {@code DataParser} for the template, given the specified path.
     *
     * @param path  Path to data
     * @return      DataParser
     */
    public DataParser getDataParser(Path path) {
        try {
            return parserClass.getConstructor(Path.class).newInstance(path);
        } catch (InstantiationException|IllegalAccessException|NoSuchMethodException|InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a new instance of the {@code DataParser} for the template, given the specified {@code String} content.
     *
     * @param content   String content
     * @return          DataParser
     */
    public DataParser getDataParser(String content) {
        try {
            return parserClass.getConstructor(String.class).newInstance(content);
        } catch (InstantiationException|IllegalAccessException|NoSuchMethodException|InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
