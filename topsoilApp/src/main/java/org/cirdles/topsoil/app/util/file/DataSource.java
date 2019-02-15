package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.model.DataTemplate;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.isotope.IsotopeSystem;

import java.nio.file.Path;

/**
 * @author marottajb
 */
public class DataSource {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private Path path;
    private DataTemplate template;
    private String delimiter;
    private IsotopeSystem isotopeSystem;
    private Uncertainty unctFormat;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataSource(Path path, DataTemplate template, String delimiter) {
        this(path, template, delimiter, null, null);
    }

    public DataSource(Path path, DataTemplate template, String delimiter, IsotopeSystem isotopeSystem,
                      Uncertainty unctFormat) {
        this.path = path;
        this.template = template;
        this.delimiter = delimiter;
        this.isotopeSystem = isotopeSystem;
        this.unctFormat = unctFormat;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Path getPath() {
        return path;
    }

    public DataTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DataTemplate template) {
        this.template = template;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public IsotopeSystem getIsotopeSystem() {
        return isotopeSystem;
    }

    public void setIsotopeSystem(IsotopeSystem isotopeSystem) {
        this.isotopeSystem = isotopeSystem;
    }

    public Uncertainty getUnctFormat() {
        return unctFormat;
    }

    public void setUnctFormat(Uncertainty unctFormat) {
        this.unctFormat = unctFormat;
    }

    @Override
    public String toString() {
        return path.getFileName().toString();
    }

}
