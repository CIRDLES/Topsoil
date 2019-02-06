package org.cirdles.topsoil.app.view.wizards;

import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.isotope.IsotopeSystem;

import java.nio.file.Path;

/**
 * @author marottajb
 */
public class ProjectSource {
    private Path path;
    private DataTemplate template;
    private IsotopeSystem isotopeSystem;
    private UncertaintyFormat unctFormat;

    public ProjectSource(Path path, DataTemplate template, IsotopeSystem isotopeSystem, UncertaintyFormat unctFormat) {
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

    public UncertaintyFormat getUnctFormat() {
        return unctFormat;
    }

    public void setUnctFormat(UncertaintyFormat unctFormat) {
        this.unctFormat = unctFormat;
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
