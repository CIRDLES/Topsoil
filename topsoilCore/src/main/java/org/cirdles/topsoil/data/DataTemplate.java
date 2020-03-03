package org.cirdles.topsoil.data;

import org.cirdles.topsoil.file.parser.DataParser;
import org.cirdles.topsoil.file.parser.DefaultDataParser;
import org.cirdles.topsoil.file.parser.Squid3DataParser;
import org.cirdles.topsoil.file.writer.DataWriter;
import org.cirdles.topsoil.file.writer.DefaultDataWriter;

/**
 * Each template represents a possible format for imported data.
 *
 * @author marottajb
 */
public enum DataTemplate {

    DEFAULT("Default", DefaultDataParser.class, DefaultDataWriter.class),
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
    public <T extends DataTable, C extends DataColumn<?>, R extends DataRow> DataParser getParser(Class<T> tableClass, Class<C> columnClass, Class<R> rowClass) {
        return getInstanceOf(parserClass, tableClass, columnClass, rowClass);
    }

    /**
     * Returns a new instance of the {@code DataWriter} for the template.
     *
     * @return          DataWriter
     */
    public DataWriter getWriter() {
        return getInstanceOf(writerClass, null, null, null);
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

    private <I, T extends DataTable, C extends DataColumn<?>, R extends DataRow> T getInstanceOf(Class<I> clazz, Class<? extends DataTable> tableClass) {
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
