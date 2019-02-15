package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.util.file.DataParser;
import org.cirdles.topsoil.app.util.file.DefaultDataParser;
import org.cirdles.topsoil.app.util.file.Squid3DataParser;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

/**
 * Each template represents a possible format for imported data, and has a {@code DataParserBase} associated with it.
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
     * Returns a new instance of the {@code DataParserBase} for the template.
     *
     * @return          DataParser
     */
    public DataParser getDataParser() {
        try {
            return parserClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
