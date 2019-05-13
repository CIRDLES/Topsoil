package org.cirdles.topsoil.app.data.composite;

import org.cirdles.topsoil.app.util.IntuitiveStringComparator;

import java.util.Comparator;

public class DataComponentComparator implements Comparator<DataComponent> {

    private IntuitiveStringComparator<String> stringComparator = new IntuitiveStringComparator<>();

    @Override
    public int compare(DataComponent component1, DataComponent component2) {
        return stringComparator.compare(component1.getLabel(), component2.getLabel());
    }

}