package org.cirdles.topsoil.app.util;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.util.HashSet;
import java.util.List;

public class ListUtils {

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
