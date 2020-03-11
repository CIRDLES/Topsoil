package org.cirdles.topsoil.data;

import org.cirdles.topsoil.utils.TopsoilTableUtils;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.List;

public interface DataTable<C extends DataColumn<?>, R extends DataRow> extends JSONString {

    DataTemplate getTemplate();

    String getTitle();

    void setTitle(String s);

    List<C> getColumns();

    default List<C> getLeafColumns() {
        return TopsoilTableUtils.getLeafColumns(getColumns());
    }

    List<R> getRows();

    default List<R> getLeafRows() {
        return TopsoilTableUtils.getLeafRows(getRows());
    }

    Uncertainty getUncertainty();

    void setUncertainty(Uncertainty u);

    @Override
    default String toJSONString() {
        JSONObject json = new JSONObject();

        String title = getTitle();
        if (title != null) json.put("title", title);

        List<C> columns = getColumns();
        if (columns != null) json.put("columns", columns);

        List<R> rows = getRows();
        if (rows != null) json.put("data", rows);

        return json.toString();
    }

}
