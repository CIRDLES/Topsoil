package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.file.parser.ClassicDataParser;
import org.cirdles.topsoil.app.file.parser.DataParser;
import org.cirdles.topsoil.app.file.parser.DefaultDataParser;
import org.cirdles.topsoil.app.file.parser.Squid3DataParser;
import org.cirdles.topsoil.app.file.writer.DefaultDataWriter;
import org.cirdles.topsoil.app.file.writer.DataWriter;

/**
 * Each template represents a possible format for imported data.
 *
 * @author marottajb
 */
public enum DataTemplate {

    DEFAULT("Default", DefaultDataParser.class, DefaultDataWriter.class),
    CLASSIC("Classic (x, σx, y, σy, rho)", ClassicDataParser.class, DefaultDataWriter.class),
    SQUID_3("Squid 3", Squid3DataParser.class, null);

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private String name;
    private Class<? extends DataParser> parserClass;
    private Class<? extends DataWriter> writerClass;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    DataTemplate(String name, Class<? extends DataParser> parserClass, Class<? extends DataWriter> writerClass) {
        this.name = name;
        this.parserClass = parserClass;
        this.writerClass = writerClass;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Returns a new instance of the {@code DataParser} for the template.
     *
     * @return          DataParser
     */
    public DataParser getParser() {
        return getInstanceOf(parserClass);
    }

    /**
     * Returns a new instance of the {@code DataWriter} for the template.
     *
     * @return          DataWriter
     */
    public DataWriter getWriter() {
        return getInstanceOf(writerClass);
    }

    public boolean isParsingSupported() {
        return parserClass != null;
    }

    public boolean isWritingSupported() {
        return writerClass != null;
    }

    @Override
    public String toString() {
        return name;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private <T> T getInstanceOf(Class<T> clazz) {
        try {
            if (clazz != null) {
                return clazz.newInstance();
            }
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
