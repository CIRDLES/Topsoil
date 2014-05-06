/*
 * Copyright 2014 CIRDLES.
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
package org.cirdles.topsoil.chart;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import static java.lang.Math.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;

/**
 * Generates ticks for a given range either automatically or by using a given tick unit and anchor tick.
 * <p>
 * @author John Zeringue <john.joseph.zeringue@gmail.com>
 */
public class TickGenerator {

    /**
     * The maximum number of major ticks produced by auto ticking. This may be configurable in the future, but for now
     * it's fixed.
     */
    private final static int MAX_TICKS = 10;

    private final DoubleProperty anchorTick = new DoublePropertyBase(0) {

        @Override
        public Object getBean() {
            return TickGenerator.this;
        }

        @Override
        public String getName() {
            return "anchorTick";
        }
    };

    private final BooleanProperty autoTicking = new BooleanPropertyBase(true) {

        @Override
        public Object getBean() {
            return TickGenerator.this;
        }

        @Override
        public String getName() {
            return "autoTicking";
        }
    };

    private final IntegerProperty minorTickCount = new IntegerPropertyBase(4) {

        @Override
        public Object getBean() {
            return TickGenerator.this;
        }

        @Override
        public String getName() {
            return "minorTickCount";
        }
    };

    private final DoubleProperty tickUnit = new DoublePropertyBase(1) {

        @Override
        public Object getBean() {
            return TickGenerator.this;
        }

        @Override
        public String getName() {
            return "tickUnit";
        }
    };

    /**
     * Creates a new TickGenerator that uses auto ticking.
     */
    public TickGenerator() {
    }

    /**
     * Creates a new TickGenerator with the specified anchor tick and tick unit and auto ticking disabled.
     * <p>
     * @param anchorTick the new generator's anchor tick
     * @param tickUnit the new generator's tick unit
     */
    public TickGenerator(double anchorTick, double tickUnit) {
        this.anchorTick.set(anchorTick);
        this.tickUnit.set(tickUnit);

        this.autoTicking.set(false);
    }

    public DoubleProperty anchorTickProperty() {
        return anchorTick;
    }

    public BooleanProperty autoTickingProperty() {
        return autoTicking;
    }

    private double firstMajorTick(double lowerBound) {
        return getAnchorTick() + getTickUnit() * ceil((lowerBound - getAnchorTick()) / getTickUnit());
    }
    
    private double floorToZero(double num) {
        if (num < 0) {
            return ceil(num);
        } else {
            return floor(num);
        }
    }

    public double getAnchorTick() {
        return anchorTick.get();
    }

    public int getMinorTickCount() {
        return minorTickCount.get();
    }

    public double getTickUnit() {
        return tickUnit.get();
    }

    public boolean isAutoTicking() {
        return autoTicking.get();
    }

    public List<Number> majorTicksForRange(double lowerBound, double upperBound) {
        List<Number> ticks = new ArrayList<>();

        if (isAutoTicking()) {
            recalculateTickPropertiesForRange(lowerBound, upperBound);
        }

        for (double tick = firstMajorTick(lowerBound); tick <= upperBound; tick += getTickUnit()) {
            ticks.add(tick);
        }

        return ticks;
    }

    /**
     * Returns the "nice" version of the given number. If round is true, the algorithm will "round" the value.
     * Otherwise, it will find the "ceiling".
     * <p>
     * @param number the number to be made nice
     * @param round indicates whether to round or ceiling
     * @return a "nice" number
     */
    private static double makeNice(double number, boolean round) {
        double orderOfMagnitude; // the number's order of magnitude
        double significand; // the number's significand
        double niceSignificand; // a nicer significand

        orderOfMagnitude = floor(log10(number));
        significand = number / pow(10, orderOfMagnitude);

        if (round) {
            if (significand < 1.5) {
                niceSignificand = 1;
            } else if (significand < 3) {
                niceSignificand = 2;
            } else if (significand < 7) {
                niceSignificand = 5;
            } else {
                niceSignificand = 10;
            }
        } else {
            if (significand <= 1) {
                niceSignificand = 1;
            } else if (significand <= 2) {
                niceSignificand = 2;
            } else if (significand <= 5) {
                niceSignificand = 5;
            } else {
                niceSignificand = 10;
            }
        }

        return niceSignificand * pow(10, orderOfMagnitude);
    }

    public IntegerProperty minorTickCountProperty() {
        return minorTickCount;
    }

    public List<Number> minorTicksForRange(double lowerBound, double upperBound) {
        List<Number> ticks = new ArrayList<>();

        if (isAutoTicking()) {
            recalculateTickPropertiesForRange(lowerBound, upperBound);
        }

        double minorTickUnit = getTickUnit() / getMinorTickCount();

        for (double majorTick = firstMajorTick(lowerBound) - getTickUnit(); majorTick <= upperBound; majorTick += getTickUnit()) {
            double minorTick = majorTick;
            for (int minorTickNumber = 1; minorTickNumber < getMinorTickCount(); minorTickNumber++) {
                minorTick += minorTickUnit;

                if (minorTick > upperBound) {
                    break;
                } else if (minorTick >= lowerBound) {
                    ticks.add(minorTick);
                }
            }
        }

        return ticks;
    }

    private void recalculateTickPropertiesForRange(double lowerBound, double upperBound) {
        double niceRange = makeNice(upperBound - lowerBound, false);

        setTickUnit(makeNice(niceRange / (MAX_TICKS - 1), true));
        setAnchorTick(floor(lowerBound / getTickUnit()) * getTickUnit());
    }

    public void setAnchorTick(double value) {
        anchorTick.set(value);
    }

    public void setAutoTicking(boolean value) {
        autoTicking.set(value);
    }

    public void setMinorTickCount(int value) {
        minorTickCount.set(value);
    }

    public void setTickUnit(double value) {
        tickUnit.set(value);
    }

    public DoubleProperty tickUnitProperty() {
        return tickUnit;
    }
}
