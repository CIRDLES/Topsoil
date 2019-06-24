package org.cirdles.topsoil.utils;

import org.cirdles.topsoil.data.DataComponent;
import org.cirdles.topsoil.utils.IntuitiveStringComparator;

import java.util.Comparator;

public class DataComponentComparator implements Comparator<DataComponent> {

    private IntuitiveStringComparator<String> stringComparator = new IntuitiveStringComparator<>();

    @Override
    public int compare(DataComponent component1, DataComponent component2) {
        return stringComparator.compare(component1.getTitle(), component2.getTitle());
    }

}