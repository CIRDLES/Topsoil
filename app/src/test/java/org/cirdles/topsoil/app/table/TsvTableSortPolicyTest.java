/*
 * Copyright 2016 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.topsoil.app.table;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static javafx.collections.FXCollections.emptyObservableList;
import static javafx.collections.FXCollections.observableArrayList;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by johnzeringue on 1/17/16.
 */
public class TsvTableSortPolicyTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private BiConsumer<ObservableList<Entry>, Comparator<? super Entry>> fxCollectionsSort;

    @Mock
    private Function<TableView<Entry>, ObservableList<Entry>> itemsGetter;

    @Mock
    private Function<TableView<Entry>, ObservableList<TableColumn<Entry, ?>>> sortOrderGetter;

    @Mock
    private TableView<Entry> tableView;

    @Mock
    private TableColumn<Entry, ?> tableColumn;

    @Mock
    private ObservableList<Entry> items;

    private Callback<TableView<Entry>, Boolean> sortPolicy;

    @Before
    public void setUp() {
        sortPolicy = new TsvTableSortPolicy(
                fxCollectionsSort,
                itemsGetter,
                sortOrderGetter);
    }

    @Test
    public void testWithEmptySortOrder() {
        when(itemsGetter.apply(tableView))
                .thenReturn(items);

        when(sortOrderGetter.apply(tableView))
                .thenReturn(emptyObservableList());

        sortPolicy.call(tableView);

        verify(fxCollectionsSort).accept(
                eq(items),
                not(eq(tableView.getComparator())));
    }

    @Test
    public void testWithNonEmptySortOrder() {
        when(itemsGetter.apply(tableView))
                .thenReturn(items);

        when(sortOrderGetter.apply(tableView))
                .thenReturn(observableArrayList(tableColumn));

        sortPolicy.call(tableView);

        verify(fxCollectionsSort).accept(items, tableView.getComparator());
    }

}
