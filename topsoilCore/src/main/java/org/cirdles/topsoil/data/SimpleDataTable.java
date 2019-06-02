package org.cirdles.topsoil.data;

import java.util.ArrayList;
import java.util.List;

public class SimpleDataTable implements DataTable {

    private final DataTemplate template;
    private String title;
    private Uncertainty uncertainty = Uncertainty.ONE_SIGMA_ABSOLUTE;
    private List<DataColumn<?>> columns = new ArrayList<>();
    private List<DataRow> rows = new ArrayList<>();

    public SimpleDataTable(DataTemplate template, String title) {
        this(template, title, null, null);
    }

    public SimpleDataTable(DataTemplate template, String title, List<DataColumn<?>> columns, List<DataRow> rows) {
        this.template = template;
        this.title = title;
        if (columns != null) {
            this.columns.addAll(columns);
        }
        if (rows != null) {
            this.rows.addAll(rows);
        }
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
    public List<DataColumn<?>> getColumns() {
        return columns;
    }

    @Override
    public List<DataRow> getRows() {
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
