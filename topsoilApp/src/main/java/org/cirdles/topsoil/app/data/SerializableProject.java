package org.cirdles.topsoil.app.data;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.row.DataRoot;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.uncertainty.Uncertainty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerializableProject implements Serializable {

    private static final long serialVersionUID = -4335988535761575359L;

    private HashMap<ProjectKey, Serializable> data = new HashMap<>();

    public SerializableProject(TopsoilProject project) {
        ArrayList<HashMap<TableKey, Serializable>> tables = new ArrayList<>();
        for (DataTable table : project.getDataTables()) {
            tables.add(getTableData(table));
        }
        data.put(ProjectKey.TABLES, tables);
    }

    public TopsoilProject reconstruct() {
        List<Map<TableKey, Object>> tableDataList = (List<Map<TableKey, Object>>) data.get(ProjectKey.TABLES);
        List<DataTable> tables = new ArrayList<>();
        for (Map<TableKey, Object> tableData : tableDataList) {
            tables.add(makeDataTable(tableData));
        }
        return new TopsoilProject(tables.toArray(new DataTable[]{}));
    }

    private HashMap<TableKey, Serializable> getTableData(DataTable table) {
        HashMap<TableKey, Serializable> tableData = new HashMap<>();
        tableData.put(TableKey.LABEL, table.getLabel());
        tableData.put(TableKey.TEMPLATE, table.getTemplate());
        tableData.put(TableKey.ISO_SYSTEM, table.getIsotopeSystem());
        tableData.put(TableKey.UNCERTAINTY, table.getUnctFormat());

        ArrayList<HashMap<HeaderKey, Serializable>> rootChildData = new ArrayList<>();
        for (DataComponent child : table.getColumnRoot().getChildren()) {
            rootChildData.add(getHeaderData(child));
        }
        tableData.put(TableKey.COL_ROOT, rootChildData);

        List<DataColumn<?>> columns = table.getDataColumns();
        ArrayList<HashMap<DataKey, Serializable>> segments = new ArrayList<>();
        for (DataSegment segment : table.getDataRoot().getChildren()) {
            segments.add(getSegmentData(segment, columns));
        }
        tableData.put(TableKey.SEGMENTS, segments);

        return tableData;
    }

    private DataTable makeDataTable(Map<TableKey, Object> tableData) {
        String label = String.valueOf(tableData.get(TableKey.LABEL));
        DataTemplate template = (DataTemplate) tableData.get(TableKey.TEMPLATE);
        List<Map<HeaderKey, Object>> headerData = (List<Map<HeaderKey, Object>>) tableData.get(TableKey.COL_ROOT);
        ColumnRoot columnRoot = makeColumnRoot(headerData);
        DataRoot dataRoot = makeDataRoot((List<Map<DataKey, Object>>) tableData.get(TableKey.SEGMENTS) , columnRoot.getLeafNodes());
        IsotopeSystem isotopeSystem = (IsotopeSystem) tableData.get(TableKey.ISO_SYSTEM);
        Uncertainty uncertainty = (Uncertainty) tableData.get(TableKey.UNCERTAINTY);

        return new DataTable(template, label, columnRoot, dataRoot, isotopeSystem, uncertainty);
    }

    private HashMap<HeaderKey, Serializable> getHeaderData(DataComponent header) {
        HashMap<HeaderKey, Serializable> headerData = new HashMap<>();
        headerData.put(ComponentKey.LABEL, header.getLabel());
        headerData.put(ComponentKey.SELECTED, header.isSelected());

        if (header instanceof DataCategory) {
            ArrayList<HashMap<HeaderKey, Serializable>> children = new ArrayList<>();
            for (DataComponent child : ((DataCategory) header).getChildren()) {
                children.add(getHeaderData(child));
            }
            headerData.put(CategoryKey.CHILDREN, children);
        } else if (header instanceof DataColumn) {
            headerData.put(ColumnKey.TYPE, ((DataColumn) header).getType());
        }

        return headerData;
    }

    private ColumnRoot makeColumnRoot(List<Map<HeaderKey, Object>> data) {
        ColumnRoot root = new ColumnRoot();
        for (Map<HeaderKey, Object> childData : data) {
            if (childData.containsKey(CategoryKey.CHILDREN)) {
                root.getChildren().add(makeDataCategory(childData));
            } else {
                root.getChildren().add(makeDataColumn(childData));
            }
        }
        return root;
    }

    private DataCategory makeDataCategory(Map<HeaderKey, Object> categoryData) {
        DataCategory category = new DataCategory(String.valueOf(categoryData.get(ComponentKey.LABEL)));
        category.setSelected((boolean) categoryData.get(ComponentKey.SELECTED));
        List<Map<HeaderKey, Object>> children = (List<Map<HeaderKey, Object>>) categoryData.get(CategoryKey.CHILDREN);
        for (Map<HeaderKey, Object> childData : children) {
            if (childData.containsKey(CategoryKey.CHILDREN)) {
                category.getChildren().add(makeDataCategory(childData));
            } else {
                DataColumn<?> col = makeDataColumn(childData);
                if (category.getChildren().contains(col)) {
                    System.out.println("shit");
                }
                category.getChildren().add(makeDataColumn(childData));
            }
        }
        return category;
    }

    private DataColumn<?> makeDataColumn(Map<HeaderKey, Object> columnData) {
        DataColumn<?> column;
        String label = String.valueOf(columnData.get(ComponentKey.LABEL));
        Class<?> type = (Class<?>) columnData.get(ColumnKey.TYPE);
        if (type == Number.class) {
            column = DataColumn.numberColumn(label);
        } else {
            column = DataColumn.stringColumn(label);
        }
        return column;
    }

    private DataRoot makeDataRoot(List<Map<DataKey, Object>> segmentDataList, List<DataColumn<?>> columns) {
        DataRoot dataRoot = new DataRoot();
        for (Map<DataKey, Object> segmentData : segmentDataList) {
            dataRoot.getChildren().add(makeDataSegment(segmentData, columns));
        }
        return dataRoot;
    }

    private HashMap<DataKey, Serializable> getSegmentData(DataSegment segment, List<DataColumn<?>> columns) {
        HashMap<DataKey, Serializable> segmentData = new HashMap<>();
        segmentData.put(ComponentKey.LABEL, segment.getLabel());
        segmentData.put(ComponentKey.SELECTED, segment.isSelected());

        ArrayList<HashMap<DataKey, Serializable>> rows = new ArrayList<>();
        for (DataRow row : segment.getChildren()) {
            rows.add(getRowData(row, columns));
        }
        segmentData.put(SegmentKey.CHILDREN, rows);

        return segmentData;
    }

    private DataSegment makeDataSegment(Map<DataKey, Object> data, List<DataColumn<?>> columns) {
        DataSegment segment = new DataSegment(String.valueOf(data.get(ComponentKey.LABEL)));
        segment.setSelected((boolean) data.get(ComponentKey.SELECTED));
        List<Map<DataKey, Object>> rowDataList = (List<Map<DataKey, Object>>) data.get(SegmentKey.CHILDREN);
        for (Map<DataKey, Object> rowData : rowDataList) {
            segment.getChildren().add(makeDataRow(rowData, columns));
        }
        return segment;
    }

    private HashMap<DataKey, Serializable> getRowData(DataRow row, List<DataColumn<?>> columns) {
        HashMap<DataKey, Serializable> rowData = new HashMap<>();
        rowData.put(ComponentKey.LABEL, row.getLabel());
        rowData.put(ComponentKey.SELECTED, row.isSelected());

        ArrayList<HashMap<PropertyKey, Serializable>> properties = new ArrayList<>();
        HashMap<PropertyKey, Serializable> propertyData;
        for (Map.Entry<DataColumn<?>, Property<?>> entry : row.getProperties().entrySet()) {
            propertyData = new HashMap<>();
            propertyData.put(PropertyKey.COL_INDEX, columns.indexOf(entry.getKey()));
            propertyData.put(PropertyKey.CLASS, entry.getValue().getClass());
            propertyData.put(PropertyKey.VALUE, (Serializable) entry.getValue().getValue());
            properties.add(propertyData);
        }
        rowData.put(RowKey.PROPERTIES, properties);

        return rowData;
    }

    private DataRow makeDataRow(Map<DataKey, Object> data, List<DataColumn<?>> columns) {
        DataRow row = new DataRow(String.valueOf(data.get(ComponentKey.LABEL)));
        row.setSelected((boolean) data.get(ComponentKey.SELECTED));
        List<Map<PropertyKey, Object>> propertyDataList = (List<Map<PropertyKey, Object>>) data.get(RowKey.PROPERTIES);
        DataColumn<?> column;
        for (Map<PropertyKey, Object> propertyData : propertyDataList) {
            column = columns.get((int) propertyData.get(PropertyKey.COL_INDEX));
            if (column.getType() == Number.class) {
                Property<Number> property = new SimpleDoubleProperty((Double) propertyData.get(PropertyKey.VALUE));
                row.setPropertyForColumn((DataColumn<Number>) column, property);
            } else {
                Property<String> property = new SimpleStringProperty(String.valueOf(propertyData.get(PropertyKey.VALUE)));
                row.setPropertyForColumn((DataColumn<String>) column, property);
            }
        }
        return row;
    }

    enum ProjectKey {
        TABLES,
        LAMBDAS
    }

    enum TableKey {
        LABEL,
        TEMPLATE,
        COL_ROOT,
        SEGMENTS,
        ISO_SYSTEM,
        UNCERTAINTY
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

    enum PropertyKey {
        COL_INDEX,
        CLASS,
        VALUE
    }
}
