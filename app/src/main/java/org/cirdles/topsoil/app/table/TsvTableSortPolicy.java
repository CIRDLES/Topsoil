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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.entry.SimpleEntry;

import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by johnzeringue on 1/17/16.
 */
public class TsvTableSortPolicy implements Callback<TableView<Entry>, Boolean> {

    private final BiConsumer<ObservableList<Entry>, Comparator<? super Entry>> fxCollectionsSort;
    private final Function<TableView<Entry>, ObservableList<Entry>> itemsGetter;
    private final Function<TableView<Entry>, ObservableList<TableColumn<Entry, ?>>> sortOrderGetter;

    public TsvTableSortPolicy() {
        this(FXCollections::sort, TableView::getItems, TableView::getSortOrder);
    }

    TsvTableSortPolicy(
            BiConsumer<ObservableList<Entry>, Comparator<? super Entry>> fxCollectionsSort,
            Function<TableView<Entry>, ObservableList<Entry>> itemsGetter,
            Function<TableView<Entry>, ObservableList<TableColumn<Entry, ?>>> sortOrderGetter) {

        this.fxCollectionsSort = fxCollectionsSort;
        this.itemsGetter = itemsGetter;
        this.sortOrderGetter = sortOrderGetter;
    }

    @Override
    public Boolean call(TableView<Entry> tableView) {
        Comparator<Entry> comparator;

        if (sortOrderGetter.apply(tableView).isEmpty()) {
            comparator = SimpleEntry::compare;
        } else {
            comparator = tableView.getComparator();
        }

        fxCollectionsSort.accept(itemsGetter.apply(tableView), comparator);
        return true;
    }

}
