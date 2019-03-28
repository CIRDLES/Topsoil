package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.ProjectManager;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.row.DataRoot;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.menu.helpers.VisualizationsMenuHelper;
import org.cirdles.topsoil.constant.Lambda;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.plot.PlotProperty;
import org.cirdles.topsoil.plot.PlotType;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.variable.Variable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private HashMap<ProjectKey, Serializable> data = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SerializableProject(TopsoilProject project) {
        ArrayList<HashMap<TableKey, Serializable>> tables = new ArrayList<>();
        ArrayList<HashMap<PlotKey, Serializable>> plots = new ArrayList<>();
        for (DataTable table : project.getDataTables()) {
            tables.add(extractTableData(table));
            for (PlotType plotType : ProjectManager.getOpenPlotTypesForTable(table)) {
                TopsoilPlotView plotView = ProjectManager.getOpenPlotView(table, plotType);
                if (plotView != null) {
                    plots.add(extractPlotData(table, plotView));
                }
            }
        }
        data.put(ProjectKey.TABLES, tables);
        data.put(ProjectKey.PLOTS, plots);

        HashMap<Lambda, Double> lambdas = new HashMap<>();
        for (Lambda l : Lambda.values()) {
            lambdas.put(l, (double) l.getValue());
        }
        data.put(ProjectKey.LAMBDAS, lambdas);
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
        List<Map<TableKey, Object>> tableDataList = (List<Map<TableKey, Object>>) data.get(ProjectKey.TABLES);
        List<DataTable> tables = new ArrayList<>();
        for (Map<TableKey, Object> tableData : tableDataList) {
            tables.add(createDataTable(tableData));
        }
        TopsoilProject project = new TopsoilProject(tables.toArray(new DataTable[]{}));

        List<Map<PlotKey, Object>> plotDataList = (List<Map<PlotKey, Object>>) data.get(ProjectKey.PLOTS);
        for (Map<PlotKey, Object> plotData : plotDataList) {
            DataTable table = null;
            String tableLabel = String.valueOf(plotData.get(PlotKey.TABLE_LABEL));
            for (DataTable t : tables) {
                if (t.getLabel().equals(tableLabel)) {
                    table = t;
                    break;
                }
            }
            PlotType plotType = (PlotType) plotData.get(PlotKey.PLOT_TYPE);
            VisualizationsMenuHelper.generatePlot(plotType, table, (Map<PlotProperty, Object>) plotData.get(PlotKey.PROPERTIES));
        }

        Map<Lambda, Number> lambdaMap = (Map<Lambda, Number>) data.get(ProjectKey.LAMBDAS);
        for (Map.Entry<Lambda, Number> entry : lambdaMap.entrySet()) {
            entry.getKey().setValue(entry.getValue());
        }

        return project;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private HashMap<PlotKey, Serializable> extractPlotData(DataTable table, TopsoilPlotView plotView) {
        HashMap<PlotKey, Serializable> plotData = new HashMap<>();
        plotData.put(PlotKey.TABLE_LABEL, table.getLabel());
        plotData.put(PlotKey.PLOT_TYPE, plotView.getPlot().getPlotType());
        plotData.put(PlotKey.PROPERTIES, extractPlotProperties(plotView.getPropertiesPanel().getPlotProperties()));

        return plotData;
    }

    private HashMap<PlotProperty, Serializable> extractPlotProperties(Map<PlotProperty, Object> properties) {
        HashMap<PlotProperty, Serializable> sProperties = new HashMap<>();
        for (Map.Entry<PlotProperty, Object> entry : properties.entrySet()) {
            sProperties.put(entry.getKey(), (Serializable) entry.getValue());
        }
        return sProperties;
    }

    private HashMap<TableKey, Serializable> extractTableData(DataTable table) {
        HashMap<TableKey, Serializable> tableData = new HashMap<>();
        tableData.put(TableKey.LABEL, table.getLabel());
        tableData.put(TableKey.TEMPLATE, table.getTemplate());
        tableData.put(TableKey.ISO_SYSTEM, table.getIsotopeSystem());
        tableData.put(TableKey.UNCERTAINTY, table.getUncertainty());

        ArrayList<HashMap<HeaderKey, Serializable>> rootChildData = new ArrayList<>();
        for (DataComponent child : table.getColumnRoot().getChildren()) {
            rootChildData.add(extractHeaderData(child));
        }
        tableData.put(TableKey.COL_ROOT, rootChildData);

        List<DataColumn<?>> columns = table.getDataColumns();
        ArrayList<HashMap<DataKey, Serializable>> segments = new ArrayList<>();
        for (DataSegment segment : table.getDataRoot().getChildren()) {
            segments.add(extractSegmentData(segment, columns));
        }
        tableData.put(TableKey.SEGMENTS, segments);

        HashMap<Variable<?>, Integer> varMap = new HashMap<>();
        for (Map.Entry<Variable<?>, DataColumn<?>> entry : table.getVariableColumnMap().entrySet()) {
            varMap.put(entry.getKey(), columns.indexOf(entry.getValue()));
        }
        tableData.put(TableKey.VARIABLE_ASSIGNMENTS, varMap);

        return tableData;
    }

    private DataTable createDataTable(Map<TableKey, Object> tableData) {
        String label = String.valueOf(tableData.get(TableKey.LABEL));
        DataTemplate template = (DataTemplate) tableData.get(TableKey.TEMPLATE);
        List<Map<HeaderKey, Object>> headerData = (List<Map<HeaderKey, Object>>) tableData.get(TableKey.COL_ROOT);
        ColumnRoot columnRoot = createColumnRoot(headerData);
        DataRoot dataRoot = createDataRoot((List<Map<DataKey, Object>>) tableData.get(TableKey.SEGMENTS) , columnRoot.getLeafNodes());
        IsotopeSystem isotopeSystem = (IsotopeSystem) tableData.get(TableKey.ISO_SYSTEM);
        Uncertainty uncertainty = (Uncertainty) tableData.get(TableKey.UNCERTAINTY);

        DataTable table = new DataTable(template, label, columnRoot, dataRoot, isotopeSystem, uncertainty);
        List<DataColumn<?>> columns = table.getDataColumns();
        Map<Variable<?>, Integer> varIndices = (Map<Variable<?>, Integer>) tableData.get(TableKey.VARIABLE_ASSIGNMENTS);

        for (Map.Entry<Variable<?>, Integer> entry : varIndices.entrySet()) {
            table.setColumnForVariable(entry.getKey(), columns.get(entry.getValue()));
        }

        return table;
    }

    private HashMap<HeaderKey, Serializable> extractHeaderData(DataComponent header) {
        HashMap<HeaderKey, Serializable> headerData = new HashMap<>();
        headerData.put(ComponentKey.LABEL, header.getLabel());
        headerData.put(ComponentKey.SELECTED, header.isSelected());

        if (header instanceof DataCategory) {
            ArrayList<HashMap<HeaderKey, Serializable>> children = new ArrayList<>();
            for (DataComponent child : ((DataCategory) header).getChildren()) {
                children.add(extractHeaderData(child));
            }
            headerData.put(CategoryKey.CHILDREN, children);
        } else if (header instanceof DataColumn) {
            headerData.put(ColumnKey.TYPE, ((DataColumn) header).getType());
        }

        return headerData;
    }

    private ColumnRoot createColumnRoot(List<Map<HeaderKey, Object>> data) {
        ColumnRoot root = new ColumnRoot();
        for (Map<HeaderKey, Object> childData : data) {
            if (childData.containsKey(CategoryKey.CHILDREN)) {
                root.getChildren().add(createDataCategory(childData));
            } else {
                root.getChildren().add(createDataColumn(childData));
            }
        }
        return root;
    }

    private DataCategory createDataCategory(Map<HeaderKey, Object> categoryData) {
        DataCategory category = new DataCategory(String.valueOf(categoryData.get(ComponentKey.LABEL)));
        category.setSelected((boolean) categoryData.get(ComponentKey.SELECTED));
        List<Map<HeaderKey, Object>> children = (List<Map<HeaderKey, Object>>) categoryData.get(CategoryKey.CHILDREN);
        for (Map<HeaderKey, Object> childData : children) {
            if (childData.containsKey(CategoryKey.CHILDREN)) {
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

    private DataColumn<?> createDataColumn(Map<HeaderKey, Object> columnData) {
        DataColumn<?> column;
        String label = String.valueOf(columnData.get(ComponentKey.LABEL));
        Class<?> type = (Class<?>) columnData.get(ColumnKey.TYPE);
        if (type == Number.class) {
            column = DataColumn.numberColumn(label);
        } else {
            column = DataColumn.stringColumn(label);
        }
        column.setSelected((boolean) columnData.get(ComponentKey.SELECTED));
        return column;
    }

    private DataRoot createDataRoot(List<Map<DataKey, Object>> segmentDataList, List<DataColumn<?>> columns) {
        DataRoot dataRoot = new DataRoot();
        for (Map<DataKey, Object> segmentData : segmentDataList) {
            dataRoot.getChildren().add(createDataSegment(segmentData, columns));
        }
        return dataRoot;
    }

    private HashMap<DataKey, Serializable> extractSegmentData(DataSegment segment, List<DataColumn<?>> columns) {
        HashMap<DataKey, Serializable> segmentData = new HashMap<>();
        segmentData.put(ComponentKey.LABEL, segment.getLabel());
        segmentData.put(ComponentKey.SELECTED, segment.isSelected());

        ArrayList<HashMap<DataKey, Serializable>> rows = new ArrayList<>();
        for (DataRow row : segment.getChildren()) {
            rows.add(extractRowData(row, columns));
        }
        segmentData.put(SegmentKey.CHILDREN, rows);

        return segmentData;
    }

    private DataSegment createDataSegment(Map<DataKey, Object> data, List<DataColumn<?>> columns) {
        DataSegment segment = new DataSegment(String.valueOf(data.get(ComponentKey.LABEL)));
        segment.setSelected((boolean) data.get(ComponentKey.SELECTED));
        List<Map<DataKey, Object>> rowDataList = (List<Map<DataKey, Object>>) data.get(SegmentKey.CHILDREN);
        for (Map<DataKey, Object> rowData : rowDataList) {
            segment.getChildren().add(createDataRow(rowData, columns));
        }
        return segment;
    }

    private HashMap<DataKey, Serializable> extractRowData(DataRow row, List<DataColumn<?>> columns) {
        HashMap<DataKey, Serializable> rowData = new HashMap<>();
        rowData.put(ComponentKey.LABEL, row.getLabel());
        rowData.put(ComponentKey.SELECTED, row.isSelected());

        ArrayList<HashMap<ValueKey, Serializable>> properties = new ArrayList<>();
        HashMap<ValueKey, Serializable> propertyData;
        for (Map.Entry<DataColumn<?>, DataRow.DataValue<?>> entry : row.getValueMap().entrySet()) {
            propertyData = new HashMap<>();
            propertyData.put(ValueKey.COL_INDEX, columns.indexOf(entry.getKey()));
            propertyData.put(ValueKey.VALUE, (Serializable) entry.getValue().getValue());
            properties.add(propertyData);
        }
        rowData.put(RowKey.PROPERTIES, properties);

        return rowData;
    }

    private DataRow createDataRow(Map<DataKey, Object> data, List<DataColumn<?>> columns) {
        DataRow row = new DataRow(String.valueOf(data.get(ComponentKey.LABEL)));
        row.setSelected((boolean) data.get(ComponentKey.SELECTED));
        List<Map<ValueKey, Object>> propertyDataList = (List<Map<ValueKey, Object>>) data.get(RowKey.PROPERTIES);
        DataColumn<?> column;
        for (Map<ValueKey, Object> propertyData : propertyDataList) {
            column = columns.get((int) propertyData.get(ValueKey.COL_INDEX));
            if (column.getType() == Number.class) {
                row.setValueForColumn((DataColumn<Number>) column, (Number) propertyData.get(ValueKey.VALUE));
            } else {
                row.setValueForColumn((DataColumn<String>) column, String.valueOf(propertyData.get(ValueKey.VALUE)));
            }
        }
        return row;
    }

    enum ProjectKey {
        TABLES,
        PLOTS,
        LAMBDAS
    }

    enum TableKey {
        LABEL,
        TEMPLATE,
        COL_ROOT,
        SEGMENTS,
        ISO_SYSTEM,
        UNCERTAINTY,
        VARIABLE_ASSIGNMENTS
    }

    enum ComponentKey implements HeaderKey, DataKey {
        LABEL,
        SELECTED
    }

    interface HeaderKey {}

    enum CategoryKey implements HeaderKey {
        CHILDREN
    }

    enum ColumnKey implements HeaderKey {
        LABEL,
        SELECTED,
        TYPE
    }

    interface DataKey {}

    enum SegmentKey implements DataKey {
        CHILDREN
    }

    enum RowKey implements DataKey {
        PROPERTIES
    }

    enum ValueKey {
        COL_INDEX,
        VALUE
    }

    enum PlotKey {
        TABLE_LABEL,
        PLOT_TYPE,
        PROPERTIES
    }
}
