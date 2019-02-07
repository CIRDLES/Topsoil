package org.cirdles.topsoil.app.util.serialization.objects;

import javafx.beans.property.ObjectProperty;
import org.cirdles.topsoil.app.model.*;
import org.cirdles.topsoil.app.model.node.BranchNode;
import org.cirdles.topsoil.app.model.node.DataNode;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.variable.Variable;

import java.io.Serializable;
import java.util.*;

import static org.cirdles.topsoil.app.util.serialization.objects.SerializableDataTable.TableKey.COLUMN_TREE;

/**
 * @author marottajb
 */
public class SerializableDataTable implements Serializable {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = 8734376815882803639L;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final Map<SerializableDataKey<? super DataTable>, Serializable> data = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SerializableDataTable(DataTable table) {
        data.put(TableKey.LABEL, table.getLabel());
        data.put(TableKey.DATA_SEGMENTS, extractData(table));
        data.put(COLUMN_TREE, extractHeaders(table.getColumnTree()));
        data.put(TableKey.ISO_SYSTEM, table.getIsotopeSystem());
        data.put(TableKey.UNCT_FORMAT, table.getUnctFormat());
        HashMap<Variable, String> varToColName = new HashMap<>();
        for (Map.Entry<Variable, DataColumn> entry : table.getVariableColumnMap().entrySet()) {
            varToColName.put(entry.getKey(), entry.getValue().getLabel());
        }
        data.put(TableKey.VARIABLE_MAP, varToColName);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Map<SerializableDataKey<? super DataTable>, Serializable> getData() {
        return data;
    }

    public DataTable getDataTable() {
        String label = String.valueOf(data.get(TableKey.LABEL));
        IsotopeSystem isoSystem = (IsotopeSystem) data.get(TableKey.ISO_SYSTEM);
        Uncertainty unctFormat = (Uncertainty) data.get(TableKey.UNCT_FORMAT);
        ColumnTree columnTree = reconstructColumns();
        List<DataSegment> dataSegments = reconstructData(columnTree);

        return new DataTable(label, isoSystem, unctFormat, columnTree, dataSegments);
    }

    public void setOpenPlotData(ArrayList<SerializablePlotData> plots) {
        data.put(TableKey.OPEN_PLOTS, plots);
    }

    public List<SerializablePlotData> getOpenPlotData() {
        return (List<SerializablePlotData>) data.get(TableKey.OPEN_PLOTS);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//    }
//
//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.defaultWriteObject();
//    }

    private ArrayList<HashMap<SerializableDataKey<? extends DataNode>, Serializable>> extractHeaders(
            BranchNode<? extends DataNode> branchNode) {
        ArrayList<HashMap<SerializableDataKey<? extends DataNode>, Serializable>> headers = new ArrayList<>();
        HashMap<SerializableDataKey<? extends DataNode>, Serializable> properties;
        DataCategory childBranch;
        for (DataNode node : branchNode.getChildren()) {
            properties = new HashMap<>();
            if (node instanceof DataColumn) {
                properties.put(ColumnKey.LABEL, node.getLabel());
            } else if (node instanceof DataCategory) {
                childBranch = (DataCategory) node;
                properties.put(CategoryKey.LABEL, childBranch.getLabel());
                properties.put(CategoryKey.CHILDREN, extractHeaders(childBranch));
            }
            headers.add(properties);
        }
        return headers;
    }

    private ArrayList<HashMap<SerializableDataKey<DataSegment>, Serializable>> extractData(DataTable table) {
        ArrayList<HashMap<SerializableDataKey<DataSegment>, Serializable>> segments = new ArrayList<>();
        HashMap<SerializableDataKey<DataSegment>, Serializable> segmentProperties;
        ArrayList<Map<SerializableDataKey<DataRow>, Serializable>> rows = new ArrayList<>();
        HashMap<SerializableDataKey<DataRow>, Serializable> rowProperties;

        for (DataSegment seg : table.getChildren()) {
            segmentProperties = new HashMap<>();
            segmentProperties.put(SegmentKey.LABEL, seg.getLabel());

            for (DataRow row : seg.getChildren()) {
                rowProperties = new HashMap<>();
                HashMap<String, Serializable> valueMap = new HashMap<>();
                rowProperties.put(RowKey.LABEL, row.getLabel());
                for (Map.Entry<DataColumn, ObjectProperty<Object>> entry : row.getDataPropertyMap().entrySet()) {
                    // TODO Ensure this value is Serializable vvv
                    valueMap.put(entry.getKey().getLabel(), (Serializable) entry.getValue().get());
                }
                rowProperties.put(RowKey.VALUE_MAP, valueMap);
                rows.add(rowProperties);
            }
            segmentProperties.put(SegmentKey.ROWS, rows);
            segments.add(segmentProperties);
        }
        return segments;
    }

    private ColumnTree reconstructColumns() {
        List<Map<SerializableDataKey<? extends DataNode>, Object>> headers =
                (List<Map<SerializableDataKey<? extends DataNode>, Object>>) data.get(COLUMN_TREE);
        ColumnTree columnTree = new ColumnTree(reconstructHeaders(headers));
        return columnTree;
    }
    private List<DataNode> reconstructHeaders(List<Map<SerializableDataKey<? extends DataNode>, Object>> header) {
        String label;
        List<DataNode> nodes = new ArrayList<>();
        List<DataNode> children;
        for (Map<SerializableDataKey<? extends DataNode>, Object> headerMap : header) {
            if (headerMap.containsKey(ColumnKey.LABEL)) {
                label = String.valueOf(headerMap.get(ColumnKey.LABEL));
                nodes.add(new DataColumn(label));
            } else if (headerMap.containsKey(CategoryKey.LABEL)) {
                label = String.valueOf(headerMap.get(CategoryKey.LABEL));
                List<Map<SerializableDataKey<? extends DataNode>, Object>> childMaps =
                        (List<Map<SerializableDataKey<? extends DataNode>, Object>>) headerMap.get(CategoryKey.CHILDREN);
                children = reconstructHeaders(childMaps);
                nodes.add(new DataCategory(label, children.toArray(new DataNode[]{})));
            }
        }
        return nodes;
    }

    private List<DataSegment> reconstructData(ColumnTree columnTree) {
        List<Map<SerializableDataKey<DataSegment>, Object>> segmentMaps =
                (List<Map<SerializableDataKey<DataSegment>, Object>>) data.get(TableKey.DATA_SEGMENTS);
        List<DataSegment> dataSegments = new ArrayList<>();
        String segmentLabel;
        List<DataRow> segmentRows;
        List<Map<SerializableDataKey<DataRow>, Object>> rowMaps;
        String rowLabel;
        Map<DataColumn, Object> columnValueMap;

        for (Map<SerializableDataKey<DataSegment>, Object> segMap : segmentMaps) {
            segmentLabel = String.valueOf(segMap.get(SegmentKey.LABEL));
            segmentRows = new ArrayList<>();
            rowMaps = (List<Map<SerializableDataKey<DataRow>, Object>>) segMap.get(SegmentKey.ROWS);
            for (Map<SerializableDataKey<DataRow>, Object> rowMap : rowMaps) {
                rowLabel = String.valueOf(rowMap.get(RowKey.LABEL));
                columnValueMap = convertRowValueMap((Map<String, Object>) rowMap.get(RowKey.VALUE_MAP), columnTree);
                segmentRows.add(new DataRow(rowLabel, columnValueMap));
            }
            dataSegments.add(new DataSegment(segmentLabel, segmentRows.toArray(new DataRow[]{})));
        }
        return dataSegments;
    }

    private Map<DataColumn, Object> convertRowValueMap(Map<String, Object> valueMap, ColumnTree columns) {
        Map<DataColumn, Object> rtnval = new HashMap<>();
        DataNode targetColumn;
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            targetColumn = columns.find(entry.getKey());
            if (targetColumn instanceof DataColumn) {
                rtnval.put((DataColumn) targetColumn, entry.getValue());
            }
        }
        return rtnval;
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    public enum TableKey implements SerializableDataKey<DataTable> {
        LABEL,
        DATA_SEGMENTS,
        COLUMN_TREE,
        VARIABLE_MAP,
        ISO_SYSTEM,
        UNCT_FORMAT,
        OPEN_PLOTS
    }

    public enum CategoryKey implements SerializableDataKey<DataCategory> {
        LABEL,
        CHILDREN
    }

    public enum ColumnKey implements SerializableDataKey<DataColumn> {
        LABEL
    }

    public enum SegmentKey implements SerializableDataKey<DataSegment> {
        LABEL,
        ROWS
    }

    public enum RowKey implements SerializableDataKey<DataRow> {
        LABEL,
        VALUE_MAP
    }

}
