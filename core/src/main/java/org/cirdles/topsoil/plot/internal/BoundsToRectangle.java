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
package org.cirdles.topsoil.plot.internal;

import javafx.geometry.Bounds;

import java.awt.Rectangle;
import java.util.function.Function;

/**
 * Converts JavaFX {@code Bounds} into an AWT {@code Rectangle}. This must be
 * performed in such a way that the resulting rectangle fits entirely inside of
 * the given bounds.
 */
public class BoundsToRectangle implements Function<Bounds, Rectangle> {

    public BoundsToRectangle() {
        super();
    }

    @Override
    public Rectangle apply(Bounds bounds) {
        int x = (int) Math.ceil(bounds.getMinX());
        int y = (int) Math.ceil(bounds.getMinY());
        int width = (int) bounds.getMaxX() - x;
        int height = (int) bounds.getMaxY() - y;

        return new Rectangle(x, y, width, height);
    }

}
