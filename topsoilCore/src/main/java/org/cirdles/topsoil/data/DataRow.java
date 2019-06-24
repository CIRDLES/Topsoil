package org.cirdles.topsoil.data;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface DataRow extends DataComponent<DataRow> {

    Map<? extends DataColumn<?>, Object> getColumnValueMap();

    <T> T getValueForColumn(DataColumn<T> column);

    <T> void setValueForColumn(DataColumn<T> column, T value);

    List<? extends DataRow> getChildren();

    @Override
    default String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("title", getTitle());
        json.put("selected", isSelected());

        DataColumn<?> column;
        Map<? extends DataColumn, Object> map = getColumnValueMap();
        if (map != null) {
            for (Map.Entry<? extends DataColumn, Object> entry : map.entrySet()) {
                column = entry.getKey();
                json.put(column.getTitle(), column.getJSONCompatibleValue(entry.getValue()));
            }
        }

        List<? extends DataRow> childRows = getChildren();
        if (childRows != null && childRows.size() > 0) {
            json.put("_children", childRows);
        }

        return json.toString();
    }
}
