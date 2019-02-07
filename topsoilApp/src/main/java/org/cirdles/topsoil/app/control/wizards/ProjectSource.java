package org.cirdles.topsoil.app.control.wizards;

import org.cirdles.topsoil.app.model.DataTemplate;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.isotope.IsotopeSystem;

import java.nio.file.Path;

/**
 * @author marottajb
 */
public class ProjectSource {
    private Path path;
    private DataTemplate template;
    private IsotopeSystem isotopeSystem;
    private Uncertainty unctFormat;

    public ProjectSource(Path path, DataTemplate template, IsotopeSystem isotopeSystem, Uncertainty unctFormat) {
        this.path = path;
        this.template = template;
        this.isotopeSystem = isotopeSystem;
        this.unctFormat = unctFormat;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public DataTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DataTemplate template) {
        this.template = template;
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
        return path.toString();
    }
}
