package org.cirdles.topsoil.app.util;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.util.HashSet;
import java.util.List;

public class ListUtils {

    /**
     * Returns an {@code ObservableSet}, the contents of which will be the union of the provided {@code ObservableList}s.
     *
     * @param sourceLists   source ObservableLists
     * @param <T>           type of elements in ObservableLists
     *
     * @return              ObservableSet
     */
    public static <T> ObservableSet<T> mergeObservableLists(List<ObservableList<T>> sourceLists) {
        ObservableSet<T> merged = FXCollections.observableSet(new HashSet<>());
        for (ObservableList<T> source : sourceLists) {
            if (source != null) {
                merged.addAll(source);
                source.addListener((ListChangeListener.Change<? extends T> c) -> {
                    while (c.next()) {
                        for (T item : c.getRemoved()) {
                            merged.remove(item);
                        }
                        for (T item : c.getAddedSubList()) {
                            merged.add(item);
                        }
                    }
                });
            }
        }
        return merged;
    }

}
