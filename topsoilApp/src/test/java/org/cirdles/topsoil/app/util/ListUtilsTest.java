package org.cirdles.topsoil.app.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class ListUtilsTest {

    @Test
    public void mergeObservableLists_test() {
        ObservableList<String> sourceOne = FXCollections.observableArrayList("A");
        ObservableList<String> sourceTwo = FXCollections.observableArrayList("B");
        ObservableSet<String> merged = ListUtils.mergeObservableLists(Arrays.asList(sourceOne, sourceTwo));
        assertTrue(merged.contains("A"));
        assertTrue(merged.contains("B"));

        sourceOne.add(1, "C");
        sourceTwo.add(1, "D");
        assertTrue(merged.contains("C"));
        assertTrue(merged.contains("D"));
    }

}
