package org.cirdles.topsoil.data;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDataTable<C extends DataColumn<?>, R extends DataRow> implements DataTable<C, R> {

    protected static final Uncertainty DEFAULT_UNCERTAINTY = Uncertainty.ONE_SIGMA_ABSOLUTE;

    protected final DataTemplate template;
    protected String title;
    protected Uncertainty uncertainty;
    protected List<C> columns;
    protected List<R> rows;

    public AbstractDataTable(DataTemplate template, String title) {
        this(template, title, null, null, null);
    }

    public AbstractDataTable(DataTemplate template, String title, Uncertainty uncertainty) {
        this(template, title, uncertainty, null, null);
    }

    public AbstractDataTable(DataTemplate template, String title, Uncertainty uncertainty, List<C> columns, List<R> rows) {
        Validate.notNull(template, "\"template\" cannot be null.");

        this.template = template;
        this.title = (title != null) ? title : "";
        this.uncertainty = (uncertainty != null) ? uncertainty : DEFAULT_UNCERTAINTY;
        this.columns = (columns != null) ? columns : new ArrayList<>();
        this.rows = (rows != null) ? rows : new ArrayList<>();
    }

    @Override
    public DataTemplate getTemplate() {
        return template;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public List<C> getColumns() {
        return columns;
    }

    @Override
    public List<R> getRows() {
        return rows;
    }

    @Override
    public Uncertainty getUncertainty() {
        return uncertainty;
    }

    @Override
    public void setUncertainty(Uncertainty uncertainty) {
        this.uncertainty = uncertainty;
    }

}
