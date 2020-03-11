package org.cirdles.topsoil.app.file.serialization;

import org.cirdles.topsoil.app.data.FXDataColumn;
import org.cirdles.topsoil.app.data.FXDataRow;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.control.plot.PlotGenerator;
import org.cirdles.topsoil.data.Uncertainty;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataRow;
import org.cirdles.topsoil.data.DataTemplate;
import org.cirdles.topsoil.data.SimpleDataColumn;
import org.cirdles.topsoil.data.SimpleDataRow;
import org.cirdles.topsoil.utils.TopsoilTableUtils;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotOption;
import org.cirdles.topsoil.plot.PlotOptions;
import org.cirdles.topsoil.plot.PlotType;
import org.cirdles.topsoil.Variable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.cirdles.topsoil.app.file.serialization.SerializableProject.SerializationKey.*;

/**
 * A serializable class for storing data from a {@link TopsoilProject}.
 */
public class SerializableProject implements Serializable {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -4335988535761575359L;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private HashMap<SerializationKey, Serializable> data = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SerializableProject(TopsoilProject project) {
        ArrayList<HashMap<SerializationKey, Serializable>> tables = new ArrayList<>();
        for (FXDataTable table : project.getDataTables()) {
            tables.add(extractTableData(project, table));
        }
        data.put(PROJECT_TABLES, tables);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Recreates a {@code TopsoilProject} from the stored data.
     *
     * @return  TopsoilProject
     */
    public TopsoilProject reconstruct() {
        List<Map<SerializationKey, Object>> tableDataList = (List<Map<SerializationKey, Object>>) data.get(PROJECT_TABLES);
        TopsoilProject project = new TopsoilProject();

        FXDataTable table;
        for (Map<SerializationKey, Object> tableData : tableDataList) {
            table = createDataTable(tableData);
            project.addDataTable(table);

            List<Map<SerializationKey, Serializable>> plotDataList =
                    (List<Map<SerializationKey, Serializable>>) tableData.get(TABLE_PLOTS);
            for (Map<SerializationKey, Serializable> plotData : plotDataList) {
                PlotType plotType = (PlotType) plotData.get(PLOT_TYPE);

                Map<String, Object> optionsMap = (Map<String, Object>) plotData.get(PLOT_OPTIONS);
                PlotOptions options = new PlotOptions();
                for (Map.Entry<String, Object> entry : optionsMap.entrySet()) {
                    options.put(PlotOption.forKey(entry.getKey()), entry.getValue());
                }

                List<? extends DataColumn<?>> columns = table.getLeafColumns();
                Map<String, Integer> varStringMap = (Map<String, Integer>) plotData.get(PLOT_VARIABLES);
                Map<Variable<?>, Integer> varIndices = new HashMap<>();
                Map<Variable<?>, DataColumn<?>> varMap = new HashMap<>();
                for (Map.Entry<String, Integer> entry : varStringMap.entrySet()) {
                    varMap.put(Variable.variableForKey(entry.getKey()), columns.get(entry.getValue()));
                }

                PlotGenerator.generatePlot(project, table, varMap, plotType, options);
            }

        }

        return project;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private HashMap<SerializationKey, Serializable> extractPlotData(Plot plot) {
        HashMap<SerializationKey, Serializable> plotData = new HashMap<>();
        plotData.put(PLOT_TYPE, plot.getPlotType());
        plotData.put(PLOT_OPTIONS, extractPlotOptions(plot.getOptions()));

        List<? extends DataColumn<?>> columns = plot.getDataTable().getLeafColumns();
        HashMap<String, Integer> varMap = new HashMap<>();
        for (Map.Entry<Variable<?>, DataColumn<?>> entry : plot.getVariableMap().entrySet()) {
            varMap.put(entry.getKey().toJSONString(), columns.indexOf(entry.getValue()));
        }
        plotData.put(PLOT_VARIABLES, varMap);

        return plotData;
    }

    private HashMap<String, Serializable> extractPlotOptions(Map<PlotOption<?>, Object> options) {
        HashMap<String, Serializable> sProperties = new HashMap<>();
        for (Map.Entry<PlotOption<?>, Object> entry : options.entrySet()) {
            sProperties.put(entry.getKey().toJSONString(), (Serializable) entry.getValue());
        }
        return sProperties;
    }

    private HashMap<SerializationKey, Serializable> extractTableData(TopsoilProject project, FXDataTable table) {
        HashMap<SerializationKey, Serializable> tableData = new HashMap<>();
        tableData.put(TABLE_LABEL, table.getTitle());
        tableData.put(TABLE_TEMPLATE, table.getTemplate());
        tableData.put(TABLE_UNCERTAINTY, table.getUncertainty());
        tableData.put(TABLE_FRACTION_DIGITS, table.getMaxFractionDigits());

        ArrayList<HashMap<SerializationKey, Serializable>> columnData = new ArrayList<>();
        for (DataColumn<?> column : table.getColumns()) {
            columnData.add(extractColumnData(column));
        }
        tableData.put(TABLE_COLUMNS, columnData);

        List<? extends DataColumn<?>> columns = table.getLeafColumns();
        ArrayList<HashMap<SerializationKey, Serializable>> rowData = new ArrayList<>();
        for (DataRow row : table.getRows()) {
            rowData.add(extractRowData(row, columns));
        }
        tableData.put(TABLE_ROWS, rowData);

        ArrayList<HashMap<SerializationKey, Serializable>> plots = new ArrayList<>();
        for (PlotView plot : project.getPlotMap().get(table)) {
            plots.add(extractPlotData(plot));
        }
        tableData.put(TABLE_PLOTS, plots);

        return tableData;
    }

    private FXDataTable createDataTable(Map<SerializationKey, Object> tableData) {
        String label = String.valueOf(tableData.get(TABLE_LABEL));
        DataTemplate template = (DataTemplate) tableData.get(TABLE_TEMPLATE);

        List<FXDataColumn<?>> columns = new ArrayList<>();
        List<Map<SerializationKey, Object>> columnDataList = (List<Map<SerializationKey, Object>>) tableData.get(TABLE_COLUMNS);
        for (Map<SerializationKey, Object> columnData : columnDataList) {
            columns.add(createDataColumn(columnData));
        }

        List<FXDataRow> rows = new ArrayList<>();
        List<Map<SerializationKey, Object>> rowDataList = (List<Map<SerializationKey, Object>>) tableData.get(TABLE_ROWS);
        List<FXDataColumn<?>> leafColumns = TopsoilTableUtils.getLeafColumns(columns);
        for (Map<SerializationKey, Object> rowData : rowDataList) {
            rows.add(createDataRow(rowData, leafColumns));
        }

        Uncertainty uncertainty = (Uncertainty) tableData.get(TABLE_UNCERTAINTY);
        int maxFractionDigits = (int) tableData.get(TABLE_FRACTION_DIGITS);

        FXDataTable table = new FXDataTable(template, label, uncertainty, columns, rows);
        table.setMaxFractionDigits(maxFractionDigits);

        return table;
    }

    private HashMap<SerializationKey, Serializable> extractColumnData(DataColumn<?> column) {
        HashMap<SerializationKey, Serializable> columnData = new HashMap<>();
        columnData.put(COLUMN_LABEL, column.getTitle());
        columnData.put(COLUMN_SELECTED, column.isSelected());

        if (column.countChildren() > 0) {
            ArrayList<HashMap<SerializationKey, Serializable>> children = new ArrayList<>();
            for (DataColumn<?> child : column.getChildren()) {
                children.add(extractColumnData(child));
            }
            columnData.put(COLUMN_CHILDREN, children);
        } else {
            columnData.put(COLUMN_TYPE, column.getType());
            columnData.put(COLUMN_DEFAULT_VALUE, (Serializable) column.getDefaultValue());
        }
        return columnData;
    }

    private FXDataColumn<?> createDataColumn(Map<SerializationKey, Object> columnData) {
        FXDataColumn<?> column;
        String label = String.valueOf(columnData.get(COLUMN_LABEL));
        boolean selected = (boolean) columnData.get(COLUMN_SELECTED);
        Class<?> type = (Class<?>) columnData.get(COLUMN_TYPE);
        if (type == Number.class) {
            Number defaultValue = (Number) columnData.get(COLUMN_DEFAULT_VALUE);
            column = new FXDataColumn<>(new SimpleDataColumn<>(label, selected, defaultValue, Number.class));
        } else {
            String defaultValue = String.valueOf(columnData.get(COLUMN_DEFAULT_VALUE));
            column = new FXDataColumn<>(new SimpleDataColumn<>(label, selected, defaultValue, String.class));
        }

        List<Map<SerializationKey, Object>> childList = (List<Map<SerializationKey, Object>>) columnData.get(COLUMN_CHILDREN);
        if (childList != null) {
            for (Map<SerializationKey, Object> childData : childList) {
                column.getChildren().add(createDataColumn(childData));
            }
        }

        return column;
    }

    private HashMap<SerializationKey, Serializable> extractRowData(DataRow row, List<? extends DataColumn<?>> columns) {
        HashMap<SerializationKey, Serializable> rowData = new HashMap<>();
        rowData.put(ROW_LABEL, row.getTitle());
        rowData.put(ROW_SELECTED, row.isSelected());

        ArrayList<HashMap<SerializationKey, Serializable>> values = new ArrayList<>();
        HashMap<SerializationKey, Serializable> valueData;
        for (Map.Entry<? extends DataColumn<?>, Object> entry : row.getColumnValueMap().entrySet()) {
            valueData = new HashMap<>();
            valueData.put(VALUE_COL_INDEX, columns.indexOf(entry.getKey()));
            valueData.put(VALUE, (Serializable) entry.getValue());      // TODO Check that values are actually Serializable
            values.add(valueData);
        }
        rowData.put(ROW_VALUES, values);

        ArrayList<HashMap<SerializationKey, Serializable>> childData = new ArrayList<>();
        for (DataRow child : row.getChildren()) {
            childData.add(extractRowData(child, columns));
        }
        rowData.put(ROW_CHILDREN, childData);

        return rowData;
    }

    private FXDataRow createDataRow(Map<SerializationKey, Object> data, List<FXDataColumn<?>> columns) {
        FXDataRow row = new FXDataRow(new SimpleDataRow(String.valueOf(data.get(ROW_LABEL))));
        row.setSelected((boolean) data.get(ROW_SELECTED));

        // Rebuild row values
        List<Map<SerializationKey, Object>> valueDataList = (List<Map<SerializationKey, Object>>) data.get(ROW_VALUES);
        FXDataColumn<?> column;
        for (Map<SerializationKey, Object> valueData : valueDataList) {
            column = columns.get((int) valueData.get(VALUE_COL_INDEX));
            if (column.getType() == Number.class) {
                row.setValueForColumn((FXDataColumn<Number>) column, (Number) valueData.get(VALUE));
            } else {
                row.setValueForColumn((FXDataColumn<String>) column, String.valueOf(valueData.get(VALUE)));
            }
        }

        // Rebuild row children
        List<Map<SerializationKey, Object>> childList = (List<Map<SerializationKey, Object>>) data.get(ROW_CHILDREN);
        for (Map<SerializationKey, Object> childData : childList) {
            row.getChildren().add(createDataRow(childData, columns));
        }

        return row;
    }

    enum SerializationKey {
        PROJECT_TABLES,
        PROJECT_LAMBDAS,

        TABLE_LABEL,
        TABLE_TEMPLATE,
        TABLE_COLUMNS,
        TABLE_ROWS,
        TABLE_UNCERTAINTY,
        TABLE_FRACTION_DIGITS,
        TABLE_PLOTS,

        COLUMN_LABEL,
        COLUMN_SELECTED,
        COLUMN_TYPE,
        COLUMN_DEFAULT_VALUE,
        COLUMN_CHILDREN,

        SEGMENT_LABEL,
        SEGMENT_SELECTED,
        SEGMENT_CHILDREN,

        ROW_LABEL,
        ROW_SELECTED,
        ROW_VALUES,
        ROW_CHILDREN,

        VALUE_COL_INDEX,
        VALUE,

        PLOT_TABLE_LABEL,
        PLOT_TYPE,
        PLOT_OPTIONS,
        PLOT_VARIABLES
    }
}
