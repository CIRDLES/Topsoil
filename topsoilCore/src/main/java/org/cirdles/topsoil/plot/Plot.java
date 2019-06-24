package org.cirdles.topsoil.plot;

import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataTable;
import org.json.JSONString;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;

public interface Plot extends JSONString {

    PlotType getPlotType();

    DataTable getDataTable();

    Map<Variable<?>, DataColumn<?>> getVariableMap();

    List<DataEntry> getData();

    String getJSONData();

    void setData(DataTable table, Map<Variable<?>, DataColumn<?>> variableMap);

    void setData(List<DataEntry> data);

    void setVariableMap(Map<Variable<?>, DataColumn<?>> variableMap);

    Map<PlotOption<?>, Object> getOptions();

    String getJSONOptions();

    void setOptions(Map<PlotOption<?>, Object> options);

    Object call(PlotFunction function, Object... args);

    Document toSVGDocument();

}
