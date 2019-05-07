package org.cirdles.topsoil.app.file.serialization;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.row.DataRoot;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.control.plot.PlotGenerator;
import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotProperties;
import org.cirdles.topsoil.plot.PlotType;
import org.cirdles.topsoil.Uncertainty;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;

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
        ArrayList<HashMap<SerializationKey, Serializable>> plots = new ArrayList<>();
        for (DataTable table : project.getDataTables()) {
            tables.add(extractTableData(table));
            for (TopsoilProject.OpenPlot openPlot : project.getOpenPlots()) {
                if (openPlot.getTable() == table) {
                    plots.add(extractPlotData(table, openPlot.getPlot()));
                }
            }
        }
        data.put(PROJECT_TABLES, tables);
        data.put(PROJECT_PLOTS, plots);

        HashMap<Lambda, Number> lambdas = new HashMap<>(project.getLambdas());
        data.put(PROJECT_LAMBDAS, lambdas);
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
        List<DataTable> tables = new ArrayList<>();
        for (Map<SerializationKey, Object> tableData : tableDataList) {
            tables.add(createDataTable(tableData));
        }
        TopsoilProject project = new TopsoilProject(tables.toArray(new DataTable[]{}));

        Map<Lambda, Number> lambdaMap = (Map<Lambda, Number>) data.get(PROJECT_LAMBDAS);
        for (Map.Entry<Lambda, Number> entry : lambdaMap.entrySet()) {
            project.setLambdaValue(entry.getKey(), entry.getValue());
        }

        List<Map<SerializationKey, Object>> plotDataList = (List<Map<SerializationKey, Object>>) data.get(PROJECT_PLOTS);
        for (Map<SerializationKey, Object> plotData : plotDataList) {
            DataTable table = null;
            String tableLabel = String.valueOf(plotData.get(PLOT_TABLE_LABEL));
            for (DataTable t : tables) {
                if (t.getLabel().equals(tableLabel)) {
                    table = t;
                    break;
                }
            }

            PlotType plotType = (PlotType) plotData.get(PLOT_TYPE);

            Map<String, Object> propertyMap = (Map<String, Object>) plotData.get(PLOT_PROPERTIES);
            PlotProperties properties = new PlotProperties();
            for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
                properties.set(PlotProperties.propertyForKey(entry.getKey()), entry.getValue());
            }
            PlotGenerator.generatePlot(project, plotType, table, properties);
        }
        return project;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private HashMap<SerializationKey, Serializable> extractPlotData(DataTable table, Plot plot) {
        HashMap<SerializationKey, Serializable> plotData = new HashMap<>();
        plotData.put(PLOT_TABLE_LABEL, table.getLabel());
        plotData.put(PLOT_TYPE, plot.getPlotType());
        plotData.put(PLOT_PROPERTIES, extractPlotProperties(plot.getProperties()));

        return plotData;
    }

    private HashMap<String, Serializable> extractPlotProperties(PlotProperties properties) {
        HashMap<String, Serializable> sProperties = new HashMap<>();
        for (Map.Entry<PlotProperties.Property<?>, Object> entry : properties.getProperties().entrySet()) {
            sProperties.put(entry.getKey().getKeyString(), (Serializable) entry.getValue());
        }
        return sProperties;
    }

    private HashMap<SerializationKey, Serializable> extractTableData(DataTable table) {
        HashMap<SerializationKey, Serializable> tableData = new HashMap<>();
        tableData.put(TABLE_LABEL, table.getLabel());
        tableData.put(TABLE_TEMPLATE, table.getTemplate());
        tableData.put(TABLE_ISO_SYSTEM, table.getIsotopeSystem());
        tableData.put(TABLE_UNCERTAINTY, table.getUncertainty());

        ArrayList<HashMap<SerializationKey, Serializable>> rootChildData = new ArrayList<>();
        for (DataComponent child : table.getColumnRoot().getChildren()) {
            rootChildData.add(extractHeaderData(child));
        }
        tableData.put(TABLE_COL_ROOT, rootChildData);

        List<DataColumn<?>> columns = table.getDataColumns();
        ArrayList<HashMap<SerializationKey, Serializable>> segments = new ArrayList<>();
        for (DataSegment segment : table.getDataRoot().getChildren()) {
            segments.add(extractSegmentData(segment, columns));
        }
        tableData.put(TABLE_SEGMENTS, segments);

        HashMap<String, Integer> varMap = new HashMap<>();
        for (Map.Entry<Variable<?>, DataColumn<?>> entry : table.getVariableColumnMap().entrySet()) {
            varMap.put(entry.getKey().getKeyString(), columns.indexOf(entry.getValue()));
        }
        tableData.put(TABLE_VARIABLES, varMap);

        return tableData;
    }

    private DataTable createDataTable(Map<SerializationKey, Object> tableData) {
        String label = String.valueOf(tableData.get(TABLE_LABEL));
        DataTemplate template = (DataTemplate) tableData.get(TABLE_TEMPLATE);
        List<Map<SerializationKey, Object>> headerData = (List<Map<SerializationKey, Object>>) tableData.get(TABLE_COL_ROOT);
        ColumnRoot columnRoot = createColumnRoot(headerData);
        DataRoot dataRoot = createDataRoot((List<Map<SerializationKey, Object>>) tableData.get(TABLE_SEGMENTS) , columnRoot.getLeafNodes());
        IsotopeSystem isotopeSystem = (IsotopeSystem) tableData.get(TABLE_ISO_SYSTEM);
        Uncertainty uncertainty = (Uncertainty) tableData.get(TABLE_UNCERTAINTY);

        DataTable table = new DataTable(template, label, columnRoot, dataRoot, isotopeSystem, uncertainty);
        List<DataColumn<?>> columns = table.getDataColumns();
        Map<String, Integer> varStringMap = (Map<String, Integer>) tableData.get(TABLE_VARIABLES);
        Map<Variable<?>, Integer> varIndices = new HashMap<>();
        for (Map.Entry<String, Integer> entry : varStringMap.entrySet()) {
            varIndices.put(Variables.variableForKey(entry.getKey()), entry.getValue());
        }

        for (Map.Entry<Variable<?>, Integer> entry : varIndices.entrySet()) {
            table.setColumnForVariable(entry.getKey(), columns.get(entry.getValue()));
        }

        return table;
    }

    private HashMap<SerializationKey, Serializable> extractHeaderData(DataComponent header) {
        HashMap<SerializationKey, Serializable> headerData = new HashMap<>();
        if (header instanceof DataCategory) {
            headerData.put(CATEGORY_LABEL, header.getLabel());
            headerData.put(CATEGORY_SELECTED, header.isSelected());
            ArrayList<HashMap<SerializationKey, Serializable>> children = new ArrayList<>();
            for (DataComponent child : ((DataCategory) header).getChildren()) {
                children.add(extractHeaderData(child));
            }
            headerData.put(CATEGORY_CHILDREN, children);
        } else if (header instanceof DataColumn) {
            headerData.put(COLUMN_LABEL, header.getLabel());
            headerData.put(COLUMN_SELECTED, header.isSelected());
            headerData.put(COLUMN_TYPE, ((DataColumn) header).getType());
        }
        return headerData;
    }

    private ColumnRoot createColumnRoot(List<Map<SerializationKey, Object>> data) {
        ColumnRoot root = new ColumnRoot();
        for (Map<SerializationKey, Object> childData : data) {
            if (childData.containsKey(CATEGORY_CHILDREN)) {
                root.getChildren().add(createDataCategory(childData));
            } else {
                root.getChildren().add(createDataColumn(childData));
            }
        }
        return root;
    }

    private DataCategory createDataCategory(Map<SerializationKey, Object> categoryData) {
        DataCategory category = new DataCategory(String.valueOf(categoryData.get(CATEGORY_LABEL)));
        category.setSelected((boolean) categoryData.get(CATEGORY_SELECTED));
        List<Map<SerializationKey, Object>> children = (List<Map<SerializationKey, Object>>) categoryData.get(CATEGORY_CHILDREN);
        for (Map<SerializationKey, Object> childData : children) {
            if (childData.containsKey(CATEGORY_CHILDREN)) {
                category.getChildren().add(createDataCategory(childData));
            } else {
                DataColumn<?> col = createDataColumn(childData);
                if (! category.getChildren().contains(col)) {
                    category.getChildren().add(col);
                }
            }
        }
        return category;
    }

    private DataColumn<?> createDataColumn(Map<SerializationKey, Object> columnData) {
        DataColumn<?> column;
        String label = String.valueOf(columnData.get(COLUMN_LABEL));
        Class<?> type = (Class<?>) columnData.get(COLUMN_TYPE);
        if (type == Number.class) {
            column = DataColumn.numberColumn(label);
        } else {
            column = DataColumn.stringColumn(label);
        }
        column.setSelected((boolean) columnData.get(COLUMN_SELECTED));
        return column;
    }

    private DataRoot createDataRoot(List<Map<SerializationKey, Object>> segmentDataList, List<DataColumn<?>> columns) {
        DataRoot dataRoot = new DataRoot();
        for (Map<SerializationKey, Object> segmentData : segmentDataList) {
            dataRoot.getChildren().add(createDataSegment(segmentData, columns));
        }
        return dataRoot;
    }

    private HashMap<SerializationKey, Serializable> extractSegmentData(DataSegment segment, List<DataColumn<?>> columns) {
        HashMap<SerializationKey, Serializable> segmentData = new HashMap<>();
        segmentData.put(SEGMENT_LABEL, segment.getLabel());
        segmentData.put(SEGMENT_SELECTED, segment.isSelected());

        ArrayList<HashMap<SerializationKey, Serializable>> rows = new ArrayList<>();
        for (DataRow row : segment.getChildren()) {
            rows.add(extractRowData(row, columns));
        }
        segmentData.put(SEGMENT_CHILDREN, rows);

        return segmentData;
    }

    private DataSegment createDataSegment(Map<SerializationKey, Object> data, List<DataColumn<?>> columns) {
        DataSegment segment = new DataSegment(String.valueOf(data.get(SEGMENT_LABEL)));
        segment.setSelected((boolean) data.get(SEGMENT_SELECTED));
        List<Map<SerializationKey, Object>> rowDataList = (List<Map<SerializationKey, Object>>) data.get(SEGMENT_CHILDREN);
        for (Map<SerializationKey, Object> rowData : rowDataList) {
            segment.getChildren().add(createDataRow(rowData, columns));
        }
        return segment;
    }

    private HashMap<SerializationKey, Serializable> extractRowData(DataRow row, List<DataColumn<?>> columns) {
        HashMap<SerializationKey, Serializable> rowData = new HashMap<>();
        rowData.put(ROW_LABEL, row.getLabel());
        rowData.put(ROW_SELECTED, row.isSelected());

        ArrayList<HashMap<SerializationKey, Serializable>> properties = new ArrayList<>();
        HashMap<SerializationKey, Serializable> propertyData;
        for (Map.Entry<DataColumn<?>, DataRow.DataValue<?>> entry : row.getValueMap().entrySet()) {
            propertyData = new HashMap<>();
            propertyData.put(VALUE_COL_INDEX, columns.indexOf(entry.getKey()));
            propertyData.put(VALUE, (Serializable) entry.getValue().getValue());
            properties.add(propertyData);
        }
        rowData.put(ROW_PROPERTIES, properties);

        return rowData;
    }

    private DataRow createDataRow(Map<SerializationKey, Object> data, List<DataColumn<?>> columns) {
        DataRow row = new DataRow(String.valueOf(data.get(ROW_LABEL)));
        row.setSelected((boolean) data.get(ROW_SELECTED));
        List<Map<SerializationKey, Object>> propertyDataList = (List<Map<SerializationKey, Object>>) data.get(ROW_PROPERTIES);
        DataColumn<?> column;
        for (Map<SerializationKey, Object> propertyData : propertyDataList) {
            column = columns.get((int) propertyData.get(VALUE_COL_INDEX));
            if (column.getType() == Number.class) {
                row.setValueForColumn((DataColumn<Number>) column, (Number) propertyData.get(VALUE));
            } else {
                row.setValueForColumn((DataColumn<String>) column, String.valueOf(propertyData.get(VALUE)));
            }
        }
        return row;
    }

    enum SerializationKey {
        PROJECT_TABLES,
        PROJECT_PLOTS,
        PROJECT_LAMBDAS,

        TABLE_LABEL,
        TABLE_TEMPLATE,
        TABLE_COL_ROOT,
        TABLE_SEGMENTS,
        TABLE_ISO_SYSTEM,
        TABLE_UNCERTAINTY,
        TABLE_VARIABLES,

        CATEGORY_LABEL,
        CATEGORY_SELECTED,
        CATEGORY_CHILDREN,

        COLUMN_LABEL,
        COLUMN_SELECTED,
        COLUMN_TYPE,

        SEGMENT_LABEL,
        SEGMENT_SELECTED,
        SEGMENT_CHILDREN,

        ROW_LABEL,
        ROW_SELECTED,
        ROW_PROPERTIES,

        VALUE_COL_INDEX,
        VALUE,

        PLOT_TABLE_LABEL,
        PLOT_TYPE,
        PLOT_PROPERTIES
    }
}
