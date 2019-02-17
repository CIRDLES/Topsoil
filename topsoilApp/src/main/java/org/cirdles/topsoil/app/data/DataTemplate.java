package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.util.file.parser.DataParser;
import org.cirdles.topsoil.app.util.file.parser.DefaultDataParser;
import org.cirdles.topsoil.app.util.file.parser.Squid3DataParser;
import org.cirdles.topsoil.app.util.file.writer.DefaultDataWriter;
import org.cirdles.topsoil.app.util.file.writer.DataWriter;
import org.cirdles.topsoil.app.util.file.writer.Squid3DataWriter;

/**
 * Each template represents a possible format for imported data, and has a {@code DataParserBase} associated with it.
 *
 * @author marottajb
 */
public enum DataTemplate {

    DEFAULT(DefaultDataParser.class, DefaultDataWriter.class),
    SQUID_3(Squid3DataParser.class, Squid3DataWriter.class);

    private Class<? extends DataParser> parserClass;
    private Class<? extends DataWriter> writerClass;

    DataTemplate(Class<? extends DataParser> parserClass, Class<? extends DataWriter> writerClass) {
        this.parserClass = parserClass;
        this.writerClass = writerClass;
    }

    /**
     * Returns a new instance of the {@code DataParser} for the template.
     *
     * @return          DataParser
     */
    public DataParser getParser() {
        try {
            return parserClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DataWriter getWriter() {
        try {
            return writerClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
