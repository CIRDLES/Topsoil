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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Dimension2D;
import javafx.geometry.Side;
import javafx.util.StringConverter;

import com.sun.javafx.charts.ChartLayoutAnimator;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

/**
 * A axis class that plots a range of numbers with major tick marks every "tickUnit". You can use any Number type with
 * this axis, Long, Double, BigDecimal etc.
 * <p>
 * @since JavaFX 2.0
 */
public final class NumberAxis extends ValueAxis<Number> {

    private static final int NUMBER_OF_TICKS = 10;

    /**
     * We use these for auto ranging to pick a user friendly tick unit. We handle tick units in the range of 1e-10 to
     * 1e+12
     */
    private static final double[] TICK_UNIT_DEFAULTS = {
        1.0E-10d, 2.5E-10d, 5.0E-10d, 1.0E-9d, 2.5E-9d, 5.0E-9d, 1.0E-8d, 2.5E-8d, 5.0E-8d, 1.0E-7d, 2.5E-7d, 5.0E-7d,
        1.0E-6d, 2.5E-6d, 5.0E-6d, 1.0E-5d, 2.5E-5d, 5.0E-5d, 1.0E-4d, 2.5E-4d, 5.0E-4d, 0.0010d, 0.0025d, 0.0050d,
        0.01d, 0.025d, 0.05d, 0.1d, 0.25d, 0.5d, 1.0d, 2.5d, 5.0d, 10.0d, 25.0d, 50.0d, 100.0d, 250.0d, 500.0d,
        1000.0d, 2500.0d, 5000.0d, 10000.0d, 25000.0d, 50000.0d, 100000.0d, 250000.0d, 500000.0d, 1000000.0d,
        2500000.0d, 5000000.0d, 1.0E7d, 2.5E7d, 5.0E7d, 1.0E8d, 2.5E8d, 5.0E8d, 1.0E9d, 2.5E9d, 5.0E9d, 1.0E10d,
        2.5E10d, 5.0E10d, 1.0E11d, 2.5E11d, 5.0E11d, 1.0E12d, 2.5E12d, 5.0E12d
    };
    /**
     * These are matching decimal formatter strings
     */
    private static final String[] TICK_UNIT_FORMATTER_DEFAULTS = {"0.0000000000", "0.00000000000", "0.0000000000",
                                                                  "0.000000000", "0.0000000000", "0.000000000",
                                                                  "0.00000000", "0.000000000", "0.00000000",
                                                                  "0.0000000", "0.00000000", "0.0000000", "0.000000",
                                                                  "0.0000000", "0.000000", "0.00000", "0.000000",
                                                                  "0.00000", "0.0000", "0.00000", "0.0000", "0.000",
                                                                  "0.0000", "0.000", "0.00", "0.000", "0.00", "0.0",
                                                                  "0.00", "0.0", "0", "0.0", "0", "#,##0"};

    private Object currentAnimationID;
    private final ChartLayoutAnimator animator = new ChartLayoutAnimator(this);
    private IntegerProperty currentRangeIndexProperty = new SimpleIntegerProperty(this, "currentRangeIndex", -1);
    private DefaultFormatter defaultFormatter = new DefaultFormatter(this);
    private final TickGenerator tickGenerator = new AxisTickGenerator(this);

    private final ObjectProperty<String> fontFamily = new SimpleObjectProperty<String>();
    public ObjectProperty<String> fontFamilyProperty() {return fontFamily;}
    
    private final DoubleProperty fontSizeTickLabel = new SimpleDoubleProperty();
    public DoubleProperty fontSizeTickLabelProperty() {return fontSizeTickLabel;}
    
    private final DoubleProperty fontSizeAxisLabel = new SimpleDoubleProperty();
    public DoubleProperty fontSizeAxisLabelProperty(){return fontSizeAxisLabel;}

    // -------------- PUBLIC PROPERTIES --------------------------------------------------------------------------------
    /**
     * When true zero is always included in the visible range. This only has effect if auto-ranging is on.
     */
    private BooleanProperty forceZeroInRange = new BooleanPropertyBase(true) {
        @Override
        protected void invalidated() {
            // This will effect layout if we are auto ranging
            if (isAutoRanging()) {
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return NumberAxis.this;
        }

        @Override
        public String getName() {
            return "forceZeroInRange";
        }
    };

    public final boolean isForceZeroInRange() {
        return forceZeroInRange.getValue();
    }

    public final void setForceZeroInRange(boolean value) {
        forceZeroInRange.setValue(value);
    }

    public final BooleanProperty forceZeroInRangeProperty() {
        return forceZeroInRange;
    }

    public TickGenerator getTickGenerator() {
        return tickGenerator;
    }
    
    private void setTextsSize(){
        Font newFont = new Font(fontFamily.get(), fontSizeTickLabel.get());
            tickLabelFontProperty().set(newFont);
            
            for (Node n : getChildren()) {
                if (n instanceof Label) {
                    Label nLabel = (Label) n;
                    Font newFontLabel = new Font(fontFamily.get(), fontSizeAxisLabel.get());
                    nLabel.setFont(newFontLabel);

                }
            }
            layoutChildren();
    }

    // -------------- CONSTRUCTORS -------------------------------------------------------------------------------------
    /**
     * Create a auto-ranging NumberAxis
     */
    public NumberAxis() {
        
        //When the fontfamily is modified, we modify the actual font
        fontFamily.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setTextsSize();
        });
        
        //When the fontsize is modified, we modify the actual font
        fontSizeAxisLabel.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            setTextsSize();
        });
        
        fontSizeTickLabel.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            setTextsSize();
        });

    }

    /**
     * Create a non-auto-ranging NumberAxis with the given upper bound, lower bound and tick unit
     * <p>
     * @param lowerBound The lower bound for this axis, ie min plottable value
     * @param upperBound The upper bound for this axis, ie max plottable value
     * @param tickUnit The tick unit, ie space between tickmarks
     */
    public NumberAxis(double lowerBound, double upperBound, double tickUnit) {
        this();
        setLowerBound(lowerBound);
        setUpperBound(upperBound);

        tickGenerator.setAutoTicking(false);
        tickGenerator.setAnchorTick(lowerBound);
        tickGenerator.setTickUnit(tickUnit);
    }

    /**
     * Create a non-auto-ranging NumberAxis with the given upper bound, lower bound and tick unit
     * <p>
     * @param axisLabel The name to display for this axis
     * @param lowerBound The lower bound for this axis, ie min plottable value
     * @param upperBound The upper bound for this axis, ie max plottable value
     * @param tickUnit The tick unit, ie space between tickmarks
     */
    public NumberAxis(String axisLabel, double lowerBound, double upperBound, double tickUnit) {
        this(lowerBound, upperBound, tickUnit);
        setLabel(axisLabel);
    }

    // -------------- PROTECTED METHODS --------------------------------------------------------------------------------
    /**
     * Get the string label name for a tick mark with the given value
     * <p>
     * @param value The value to format into a tick label string
     * @return A formatted string for the given value
     */
    @Override
    protected String getTickMarkLabel(Number value) {
        StringConverter<Number> formatter = getTickLabelFormatter();
        if (formatter == null) {
            formatter = defaultFormatter;
        }
        return formatter.toString(value);
    }

    /**
     * Called to get the current axis range.
     * <p>
     * @return A range object that can be passed to setRange() and calculateTickValues()
     */
    @Override
    protected Object getRange() {
        return new double[]{
            getLowerBound(),
            getUpperBound(),
            tickGenerator.getTickUnit(),
            getScale(),
            currentRangeIndexProperty.get()
        };
    }

    /**
     * Called to set the current axis range to the given range. If isAnimating() is true then this method should animate
     * the range to the new range.
     * <p>
     * @param range A range object returned from autoRange()
     * @param animate If true animate the change in range
     */
    @Override
    protected void setRange(Object range, boolean animate) {
        final double[] rangeProps = (double[]) range;
        final double lowerBound = rangeProps[0];
        final double upperBound = rangeProps[1];
        final double tickUnit = rangeProps[2];
        final double scale = rangeProps[3];
        final double rangeIndex = rangeProps[4];
        currentRangeIndexProperty.set((int) rangeIndex);
        setLowerBound(lowerBound);
        setUpperBound(upperBound);
        currentLowerBound.set(lowerBound);
        setScale(scale);
    }

    /**
     * Calculate a list of all the data values for each tick mark in range
     * <p>
     * @param length The length of the axis in display units
     * @param range A range object returned from autoRange()
     * @return A list of tick marks that fit along the axis if it was the given length
     */
    @Override
    protected List<Number> calculateTickValues(double length, Object range) {
        final double[] rangeProps = (double[]) range;
        final double lowerBound = rangeProps[0];
        final double upperBound = rangeProps[1];

        return tickGenerator.majorTicksForRange(lowerBound, upperBound);
    }

    /**
     * Calculate a list of the data values for every minor tick mark
     * <p>
     * @return List of data values where to draw minor tick marks
     */
    @Override
    protected List<Number> calculateMinorTickMarks() {
        final double lowerBound = getLowerBound();
        final double upperBound = getUpperBound();

        return tickGenerator.minorTicksForRange(lowerBound, upperBound);
    }

    /**
     * Measure the size of the label for given tick mark value. This uses the font that is set for the tick marks
     * <p>
     * @param value tick mark value
     * @param range range to use during calculations
     * @return size of tick mark label for given value
     */
    @Override
    protected Dimension2D measureTickMarkSize(Number value, Object range) {
        final double[] rangeProps = (double[]) range;
        final double rangeIndex = rangeProps[4];
        return measureTickMarkSize(value, getTickLabelRotation(), (int) rangeIndex);
    }

    /**
     * Measure the size of the label for given tick mark value. This uses the font that is set for the tick marks
     * <p>
     * @param value tick mark value
     * @param rotation The text rotation
     * @param rangeIndex The index of the tick unit range
     * @return size of tick mark label for given value
     */
    private Dimension2D measureTickMarkSize(Number value, double rotation, int rangeIndex) {
        String labelText;
        StringConverter<Number> formatter = getTickLabelFormatter();
        if (formatter == null) {
            formatter = defaultFormatter;
        }
        if (formatter instanceof DefaultFormatter) {
            labelText = ((DefaultFormatter) formatter).toString(value, rangeIndex);
        } else {
            labelText = formatter.toString(value);
        }
        return measureTickMarkLabelSize(labelText, rotation);
    }

    /**
     * Called to set the upper and lower bound and anything else that needs to be auto-ranged
     * <p>
     * @param minValue The min data value that needs to be plotted on this axis
     * @param maxValue The max data value that needs to be plotted on this axis
     * @param length The length of the axis in display coordinates
     * @param labelSize The approximate average size a label takes along the axis
     * @return The calculated range
     */
    @Override
    protected Object autoRange(double minValue, double maxValue, double length, double labelSize) {
        final Side side = getSide();
        final boolean vertical = Side.LEFT.equals(side) || Side.RIGHT.equals(side);
        // check if we need to force zero into range
        if (isForceZeroInRange()) {
            if (maxValue < 0) {
                maxValue = 0;
            } else if (minValue > 0) {
                minValue = 0;
            }
        }
        final double range = maxValue - minValue;
        // pad min and max by 2%, checking if the range is zero
        final double paddedRange = (range == 0) ? 2 : Math.abs(range) * 1.02;
        final double padding = (paddedRange - range) / 2;
        // if min and max are not zero then add padding to them
        double paddedMin = minValue - padding;
        double paddedMax = maxValue + padding;
        // check padding has not pushed min or max over zero line
        if ((paddedMin < 0 && minValue >= 0) || (paddedMin > 0 && minValue <= 0)) {
            // padding pushed min above or below zero so clamp to 0
            paddedMin = 0;
        }
        if ((paddedMax < 0 && maxValue >= 0) || (paddedMax > 0 && maxValue <= 0)) {
            // padding pushed min above or below zero so clamp to 0
            paddedMax = 0;
        }
        // calculate the number of tick-marks we can fit in the given length
        int numOfTickMarks = (int) Math.floor(Math.abs(length) / labelSize);
        // can never have less than 2 tick marks one for each end
        numOfTickMarks = Math.max(numOfTickMarks, 2);
        // calculate tick unit for the number of ticks can have in the given data range
        double tickUnit = paddedRange / (double) numOfTickMarks;
        // search for the best tick unit that fits
        double tickUnitRounded = 0;
        double minRounded = 0;
        double maxRounded = 0;
        int count = 0;
        double reqLength = Double.MAX_VALUE;
        int rangeIndex = 10;
        // loop till we find a set of ticks that fit length and result in a total of less than 20 tick marks
        while (reqLength > length || count > 20) {
            // find a user friendly match from our default tick units to match calculated tick unit
            for (int i = 0; i < TICK_UNIT_DEFAULTS.length; i++) {
                double tickUnitDefault = TICK_UNIT_DEFAULTS[i];
                if (tickUnitDefault > tickUnit) {
                    tickUnitRounded = tickUnitDefault;
                    rangeIndex = i;
                    break;
                }
            }
            // move min and max to nearest tick mark
            minRounded = Math.floor(paddedMin / tickUnitRounded) * tickUnitRounded;
            maxRounded = Math.ceil(paddedMax / tickUnitRounded) * tickUnitRounded;
            // calculate the required length to display the chosen tick marks for real, this will handle if there are
            // huge numbers involved etc or special formatting of the tick mark label text
            double maxReqTickGap = 0;
            double last = 0;
            count = 0;
            for (double major = minRounded; major <= maxRounded; major += tickUnitRounded, count++) {
                double size = (vertical) ? measureTickMarkSize((Double) major, getTickLabelRotation(), rangeIndex)
                        .getHeight()
                              : measureTickMarkSize((Double) major, getTickLabelRotation(), rangeIndex).getWidth();
                if (major == minRounded) { // first
                    last = size / 2;
                } else {
                    maxReqTickGap = Math.max(maxReqTickGap, last + 6 + (size / 2));
                }
            }
            reqLength = (count - 1) * maxReqTickGap;
            tickUnit = tickUnitRounded;
            // check if we already found max tick unit
            if (tickUnitRounded == TICK_UNIT_DEFAULTS[TICK_UNIT_DEFAULTS.length - 1]) {
                // nothing we can do so just have to use this
                break;
            }
        }
        // calculate new scale
        final double newScale = calculateNewScale(length, minRounded, maxRounded);
        // return new range
        return new double[]{minRounded, maxRounded, tickUnitRounded, newScale, rangeIndex};
    }

    @Override
    public double getDisplayPosition(Number value) {
        double length = Side.LEFT.equals(getSide()) || Side.RIGHT.equals(getSide()) ? getHeight() : 0;
        return length + ((value.doubleValue() - currentLowerBound.get()) * getScale());
    }

    // -------------- STYLESHEET HANDLING ------------------------------------------------------------------------------
    /**
     * @treatAsPrivate implementation detail
     */
//    private static class StyleableProperties {
//
//        private static final CssMetaData<NumberAxis, Number> TICK_UNIT
//                = new CssMetaData<NumberAxis, Number>("-fx-tick-unit",
//                                                      SizeConverter
//                                                      .getInstance(), 5.0) {
//
//                    @Override
//                    public boolean isSettable(NumberAxis n) {
//                        return n.tickUnit == null || !n.tickUnit.isBound();
//                    }
//
//                    @Override
//                    public StyleableProperty<Number> getStyleableProperty(NumberAxis n) {
//                        return (StyleableProperty<Number>) n.tickUnitProperty();
//                    }
//                };
//
//        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
//
//        static {
//            final List<CssMetaData<? extends Styleable, ?>> styleables
//                    = new ArrayList<CssMetaData<? extends Styleable, ?>>(ValueAxis
//                            .getClassCssMetaData());
//            styleables.add(TICK_UNIT);
//            STYLEABLES = Collections.unmodifiableList(styleables);
//        }
//    }
    /**
     * @return The CssMetaData associated with this class, which may include the CssMetaData of its super classes.
     * @since JavaFX 8.0
     */
//    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
//        return StyleableProperties.STYLEABLES;
//    }
    /**
     * {@inheritDoc}
     * <p>
     * @since JavaFX 8.0
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    // -------------- INNER CLASSES ------------------------------------------------------------------------------------
    /**
     * Default number formatter for NumberAxis, this stays in sync with auto-ranging and formats values appropriately.
     * You can wrap this formatter to add prefixes or suffixes;
     * <p>
     * @since JavaFX 2.0
     */
    public static class DefaultFormatter extends StringConverter<Number> {

        private DecimalFormat formatter;
        private String prefix = null;
        private String suffix = null;

        /**
         * used internally
         */
        private DefaultFormatter() {
        }

        /**
         * Construct a DefaultFormatter for the given NumberAxis
         * <p>
         * @param axis The axis to format tick marks for
         */
        public DefaultFormatter(final NumberAxis axis) {
            formatter = getFormatter(axis.isAutoRanging() ? axis.currentRangeIndexProperty.get() : -1);
            final ChangeListener axisListener = new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    formatter = getFormatter(axis.isAutoRanging() ? axis.currentRangeIndexProperty.get() : -1);
                }
            };
            axis.currentRangeIndexProperty.addListener(axisListener);
            axis.autoRangingProperty().addListener(axisListener);
        }

        /**
         * Construct a DefaultFormatter for the given NumberAxis with a prefix and/or suffix.
         * <p>
         * @param axis The axis to format tick marks for
         * @param prefix The prefix to append to the start of formatted number, can be null if not needed
         * @param suffix The suffix to append to the end of formatted number, can be null if not needed
         */
        public DefaultFormatter(NumberAxis axis, String prefix, String suffix) {
            this(axis);
            this.prefix = prefix;
            this.suffix = suffix;
        }

        private static DecimalFormat getFormatter(int rangeIndex) {
            if (rangeIndex < 0) {
                return new DecimalFormat();
            } else if (rangeIndex >= TICK_UNIT_FORMATTER_DEFAULTS.length) {
                return new DecimalFormat(TICK_UNIT_FORMATTER_DEFAULTS[TICK_UNIT_FORMATTER_DEFAULTS.length - 1]);
            } else {
                return new DecimalFormat(TICK_UNIT_FORMATTER_DEFAULTS[rangeIndex]);
            }
        }

        /**
         * Converts the object provided into its string form. Format of the returned string is defined by this
         * converter.
         * <p>
         * @return a string representation of the object passed in.
         * @see StringConverter#toString
         */
        @Override
        public String toString(Number object) {
            return toString(object, formatter);
        }

        private String toString(Number object, int rangeIndex) {
            return toString(object, getFormatter(rangeIndex));
        }

        private String toString(Number object, DecimalFormat formatter) {
            if (prefix != null && suffix != null) {
                return prefix + formatter.format(object) + suffix;
            } else if (prefix != null) {
                return prefix + formatter.format(object);
            } else if (suffix != null) {
                return formatter.format(object) + suffix;
            } else {
                return formatter.format(object);
            }
        }

        /**
         * Converts the string provided into a Number defined by the this converter. Format of the string and type of
         * the resulting object is defined by this converter.
         * <p>
         * @return a Number representation of the string passed in.
         * @see StringConverter#toString
         */
        @Override
        public Number fromString(String string) {
            try {
                int prefixLength = (prefix == null) ? 0 : prefix.length();
                int suffixLength = (suffix == null) ? 0 : suffix.length();
                return formatter.parse(string.substring(prefixLength, string.length() - suffixLength));
            } catch (ParseException e) {
                return null;
            }
        }
    }

}
