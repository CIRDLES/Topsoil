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

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by johnzeringue on 2/29/16.
 */
public class IsBlankImageTest {

    private IsBlankImage isBlankImage;

    @Before
    public void setUp() {
        isBlankImage = new IsBlankImage();
    }

    @Test
    public void testBlankWhiteImage() {
        BufferedImage image = loadImage("blank-white.png");

        assertThat(isBlankImage.test(image)).isTrue();
    }

    @Test
    public void testBlankRedImage() {
        BufferedImage image = loadImage("blank-red.png");

        assertThat(isBlankImage.test(image)).isTrue();
    }

    @Test
    public void testBowring() {
        BufferedImage image = loadImage("bowring.png");

        assertThat(isBlankImage.test(image)).isFalse();
    }

    private static BufferedImage loadImage(String filename) {
        try {
            return ImageIO.read(
                    IsBlankImageTest.class.getResourceAsStream(filename));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
