package org.cirdles.topsoil.app.progress.table;

import javafx.beans.property.StringProperty;
import org.cirdles.topsoil.app.plot.Variable;

import java.util.List;
import java.util.Map;

/**
 * @author marottajb
 */
public class TopsoilDataTable {

    private List<List<Double>> data;
    private Map<Variable<Number>, List<Double>> dataColumnForVariable;
    private Map<List<Double>, StringProperty> nameForDataColumn;

    public TopsoilDataTable() {

    }

    public Map<Variable<Number>, List<Double>> getDataColumnByVariable() {
        return dataColumnForVariable;
    }


}
