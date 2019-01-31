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

import java.awt.image.BufferedImage;
import java.util.function.Predicate;

/**
 * Tests an image to see if it is blank. An image is blank iff all of its pixels
 * are the same color.
 */
public class IsBlankImage implements Predicate<BufferedImage> {

    public IsBlankImage() {
        super();
    }

    @Override
    public boolean test(BufferedImage image) {
        boolean result = true;
        int sampleRGB = image.getRGB(0, 0);

        search:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (image.getRGB(x, y) != sampleRGB) {
                    result = false;
                    break search;
                }
            }
        }

        return result;
    }

}
