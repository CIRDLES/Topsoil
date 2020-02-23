package org.cirdles.topsoil.data;

import org.cirdles.topsoil.symbols.SymbolKey;
import org.json.JSONObject;

import java.util.List;

/**
 *
 *
 * @param <T>   the type of values associated with this column
 */
public interface DataColumn<T> extends DataComponent<DataColumn<?>>, SymbolKey<T> {

    public void getDependentColumn(DataColumn<?> column);

    public void setDependentColumn(DataColumn<?> column);

    @Override
    default String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("title", getTitle());
        json.put("selected", isSelected());

        List<? extends DataColumn<?>> childColumns = getChildren();
        if (childColumns != null && childColumns.size() > 0) {
            json.put("columns", childColumns);
        } else {
            json.put("field", getTitle());
        }

        return json.toString();
    }

}
