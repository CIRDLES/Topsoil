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

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import org.junit.Before;
import org.junit.Test;

import java.awt.Rectangle;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by johnzeringue on 2/29/16.
 */
public class BoundsToRectangleTest {

    private BoundsToRectangle boundsToRectangle;

    @Before
    public void setUp() {
        boundsToRectangle = new BoundsToRectangle();
    }

    @Test
    public void testApplyToIntegerBounds() {
        Bounds bounds = new BoundingBox(10, 12, 15, 5);

        Rectangle rectangle = boundsToRectangle.apply(bounds);

        assertThat(rectangle.getX()).isEqualTo(10);
        assertThat(rectangle.getY()).isEqualTo(12);
        assertThat(rectangle.getWidth()).isEqualTo(15);
        assertThat(rectangle.getHeight()).isEqualTo(5);
    }

    @Test
    public void testRoundsXAndYUp() {
        Bounds bounds = new BoundingBox(3.001, 2.001, 4, 1);

        Rectangle rectangle = boundsToRectangle.apply(bounds);

        assertThat(rectangle.getX()).isEqualTo(4);
        assertThat(rectangle.getY()).isEqualTo(3);
    }

    @Test
    public void testTrimsWidthAndHeight() {
        Bounds bounds = new BoundingBox(7.4, 3.1, 3.6, 2);

        Rectangle rectangle = boundsToRectangle.apply(bounds);

        assertThat(rectangle.getWidth()).isEqualTo(3);
        assertThat(rectangle.getHeight()).isEqualTo(1);
    }

}
