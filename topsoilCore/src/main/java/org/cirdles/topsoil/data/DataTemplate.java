package org.cirdles.topsoil.data;

import org.apache.commons.lang3.Validate;
import org.cirdles.topsoil.exception.MissingImplementationException;
import org.cirdles.topsoil.file.parser.DataParser;
import org.cirdles.topsoil.file.parser.DefaultDataParser;
import org.cirdles.topsoil.file.parser.Squid3DataParser;
import org.cirdles.topsoil.file.writer.DataWriter;
import org.cirdles.topsoil.file.writer.DefaultDataWriter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

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
     * @return DataParser
     */
    @SuppressWarnings("unchecked")  // this is okay because DataParser's C and R are based off of the T class provided
    public <T extends DataTable<C, R>, C extends DataColumn<?>, R extends DataRow> DataParser<T, C, R> getParser(Class<T> tableClass) {
        Validate.notNull(tableClass, "The Class of a concrete implementor of DataTable must be provided.");
        if (!isParsingSupported()) {
            throw new UnsupportedOperationException("Data parsing is not supported for DataTemplate." + this.name());
        }
        try {
            Constructor<? extends DataParser> constructor = parserClass.getConstructor(Class.class);
            return constructor.newInstance(tableClass);
        } catch (InstantiationException|IllegalAccessException|NoSuchMethodException|InvocationTargetException e) {
            throw new MissingImplementationException(
                    e,
                    parserClass,
                    MissingImplementationException.Type.CONSTRUCTOR,
                    Class.class
            );
        }
    }


    /**
     * Returns a new instance of the {@code DataWriter} for the template.
     *
     * @return DataWriter
     */
    public DataWriter getWriter() {
        if (!isWritingSupported()) {
            throw new UnsupportedOperationException("Data writing is not supported for DataTemplate." + this.name());
        }
        try {
            return writerClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
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
}
