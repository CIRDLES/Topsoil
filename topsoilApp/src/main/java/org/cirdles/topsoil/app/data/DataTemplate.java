package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.file.parser.ClassicDataParser;
import org.cirdles.topsoil.app.file.parser.DataParser;
import org.cirdles.topsoil.app.file.parser.DefaultDataParser;
import org.cirdles.topsoil.app.file.parser.Squid3DataParser;
import org.cirdles.topsoil.app.file.writer.DefaultDataWriter;
import org.cirdles.topsoil.app.file.writer.DataWriter;
import org.cirdles.topsoil.app.file.writer.Squid3DataWriter;

/**
 * Each template represents a possible format for imported data.
 *
 * @author marottajb
 */
public enum DataTemplate {

    DEFAULT("Default", DefaultDataParser.class, DefaultDataWriter.class),
    CLASSIC("Classic (x, σx, y, σy, rho)", ClassicDataParser.class, DefaultDataWriter.class),
    SQUID_3("Squid 3", Squid3DataParser.class, Squid3DataWriter.class);

    private String name;
    private Class<? extends DataParser> parserClass;
    private Class<? extends DataWriter> writerClass;

    DataTemplate(String name, Class<? extends DataParser> parserClass, Class<? extends DataWriter> writerClass) {
        this.name = name;
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

    /**
     * Returns a new instance of the {@code DataWriter} for the template.
     *
     * @return          DataWriter
     */
    public DataWriter getWriter() {
        try {
            return writerClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

}
