package org.cirdles.topsoil.data;

import org.json.JSONObject;
import org.json.JSONString;

import java.util.List;

public interface DataTable extends JSONString {

    DataTemplate getTemplate();

    String getTitle();

    void setTitle(String s);

    List<? extends DataColumn<?>> getColumns();

    default List<? extends DataColumn<?>> getLeafColumns() {
        return TableUtils.getLeafColumns(getColumns());
    }

    List<? extends DataRow> getRows();

    default List<? extends DataRow> getLeafRows() {
        return TableUtils.getLeafRows(getRows());
    }

    Uncertainty getUncertainty();

    void setUncertainty(Uncertainty u);

    @Override
    default String toJSONString() {
        JSONObject json = new JSONObject();

        String title = getTitle();
        if (title != null) json.put("title", title);

        List<? extends DataColumn<?>> columns = getColumns();
        if (columns != null) json.put("columns", columns);

        List<? extends DataRow> rows = getRows();
        if (rows != null) json.put("data", rows);

        return json.toString();
    }

}
