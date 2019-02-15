package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.util.file.parser.DefaultFileParser;
import org.cirdles.topsoil.app.util.file.parser.FileParser;
import org.cirdles.topsoil.app.util.file.parser.Squid3FileParser;

/**
 * Each template represents a possible format for imported data, and has a {@code DataParserBase} associated with it.
 *
 * @author marottajb
 */
public enum DataTemplate {

    DEFAULT(DefaultFileParser.class),
    SQUID_3(Squid3FileParser.class);

    private Class<? extends FileParser> parserClass;

    DataTemplate(Class<? extends FileParser> clazz) {
        this.parserClass = clazz;
    }

    /**
     * Returns a new instance of the {@code DataParserBase} for the template.
     *
     * @return          FileParser
     */
    public FileParser getDataParser() {
        try {
            return parserClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
