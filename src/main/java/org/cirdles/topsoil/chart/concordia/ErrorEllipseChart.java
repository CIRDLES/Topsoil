/*
 * Copyright (C) 2014 John Zeringue
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cirdles.topsoil.chart.concordia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.cirdles.topsoil.chart.DataConverter;
import org.cirdles.topsoil.chart.NumberAxis;
import org.cirdles.topsoil.chart.NumberChart;

/**
 * <p>
 * A <code>NumbertChart</code> use to show a Concordia Graph, containing for now only Error Ellipse.</p>
 * <p>
 * It support adding, removing or changing the Ellipses.</p>
 *
 * @author John Zeringue (known as El Zeringus in Spain)
 * @see NumberChart
 */
public class ErrorEllipseChart extends NumberChart {
    
    public static final Color ellipseOutlineColorDefault = Color.BLACK;
    public static final Color ellipseFillColorDefault = Color.RED;
    public static final double ellipseFillOpacityDefault = 0.3;
    public static final boolean ellipseOutlineShownDefault = true;

    private final DataConverter<ErrorEllipse> converter;

    private final ErrorEllipsePlotter errorEllipsePlotter;
    private final ErrorEllipseFiller errorEllipseFiller;
    private final ConcordiaLinePlotter concordiaLinePlotter;

    ConcordiaLine concordiaLine;

    private final DoubleProperty confidenceLevel = new DoublePropertyBase(1) {

        @Override
        public Object getBean() {
            return ErrorEllipseChart.this;
        }

        @Override
        public String getName() {
            return "confidenceLevel";
        }

        @Override
        protected void invalidated() {
            layoutPlotChildren();
        }
    };
    
    ObjectProperty<Color> ellipseOutlineColorProperty = new SimpleObjectProperty<>(ellipseOutlineColorDefault);
    public ObjectProperty<Color> ellipseOutlineColorProperty() {
        return ellipseOutlineColorProperty;
    }

    ObjectProperty<Color> ellipseFillColorProperty = new SimpleObjectProperty<>(ellipseFillColorDefault);
    public ObjectProperty<Color> ellipseFillColorProperty() {
        return ellipseFillColorProperty;
    }

    DoubleProperty ellipseFillOpacityProperty = new SimpleDoubleProperty(ellipseFillOpacityDefault);
    public DoubleProperty ellipseFillOpacityProperty() {
        return ellipseFillOpacityProperty;
    }

    BooleanProperty ellipseOutlineShownProperty = new SimpleBooleanProperty(ellipseOutlineShownDefault);
    public BooleanProperty ellipseOutlineShownProperty() {
        return ellipseOutlineShownProperty;
    }

    private final ErrorEllipseChartStyleAccessor ccStyleAccessor = new ErrorEllipseChartStyleAccessor() {
        BooleanProperty concordiaLineShownProperty = new SimpleBooleanProperty(ErrorEllipseChartStyleAccessor.concordiaLineShownDefault);

        @Override
        public BooleanProperty concordiaLineShownProperty() {
            return concordiaLineShownProperty;
        }
        @Override
        public DoubleProperty axisXAnchorTickProperty() {
            return ((NumberAxis) getXAxis()).getTickGenerator().anchorTickProperty();
        }


        @Override
        public DoubleProperty axisXTickUnitProperty() {
           return ((NumberAxis) getXAxis()).getTickGenerator().tickUnitProperty();
        }

        @Override
        public DoubleProperty axisYAnchorTickProperty() {
            return ((NumberAxis) getYAxis()).getTickGenerator().anchorTickProperty();
        }


        @Override
        public DoubleProperty axisYTickUnitProperty() {
            return ((NumberAxis) getYAxis()).getTickGenerator().tickUnitProperty();
        }

        @Override
        public BooleanProperty axisXAutoTickProperty() {
            return ((NumberAxis) getXAxis()).getTickGenerator().autoTickingProperty();
        }

        @Override
        public BooleanProperty axisYAutoTickProperty() {
            return ((NumberAxis) getYAxis()).getTickGenerator().autoTickingProperty();
        }

    };

    public ErrorEllipseChart() {
        this(new DefaultConverter());
    }

    public ErrorEllipseChart(DataConverter<ErrorEllipse> converter) {
        super();

        getStylesheets().add(ErrorEllipseChart.class.getResource("ConcordiaChart.css").toExternalForm());
        setAnimated(false);
        getXAxis().setAnimated(false);
        getXAxis().setLabel("\u00B2\u2070\u2077Pb/\u00B2\u00B3\u2075U"); // "207Pb/235U"
        getYAxis().setAnimated(false);
        getYAxis().setLabel("\u00B2\u2070\u2076Pb/\u00B2\u00B3\u2078U"); // "206Pb/238U"


        errorEllipsePlotter = new ErrorEllipsePlotter(this, null);
        errorEllipseFiller = new ErrorEllipseFiller(this, null);
        concordiaLinePlotter = new ConcordiaLinePlotter(this, ccStyleAccessor);

        this.converter = converter;
    }

    public ErrorEllipseChart(ObservableList<Series<Number, Number>> data) {
        this();
        setData(data);
    }

    @Override
    protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
//        item.getNode().getStyleClass().add("series" + getData().indexOf(series));

        if (shouldAnimate()) {
            getPlotChildren().add(
                    errorEllipsePlotter.plot(converter.convert(item)));
            getPlotChildren().add(item.getNode());
            // fade in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), item.getNode());
            fadeIn.setToValue(1);
            fadeIn.play();
        } else {
            getPlotChildren().add(
                    errorEllipsePlotter.plot(converter.convert(item)));
        }
    }

    @Override
    protected void dataItemRemoved(Data<Number, Number> item, Series<Number, Number> series) {
        final Node ellipse = item.getNode();

        if (shouldAnimate()) {
            // fade out
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), ellipse);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> {
                getPlotChildren().remove(ellipse);
            });
            fadeOut.play();
        } else {
            getPlotChildren().remove(ellipse);
        }
    }

    @Override
    protected void dataItemChanged(Data<Number, Number> data) {

    }

    @Override
    protected void seriesAdded(Series<Number, Number> series, int seriesIndex) {
        // handle any data already in the series
        for (int i = 0; i < series.getData().size(); i++) {
            Data<Number, Number> item = series.getData().get(i);
            dataItemAdded(series, i, item);
        }
    }

    @Override
    protected void seriesRemoved(Series<Number, Number> series) {
        series.getData().stream()
                .forEach((item) -> {
                    dataItemRemoved(item, series);
                });
    }

    @Override
    protected void layoutPlotChildren() {
        getPlotChildren().clear();

        concordiaLine = new ConcordiaLine(xAxis.getLowerBound(), xAxis.getUpperBound(),
                                          yAxis.getLowerBound(), yAxis.getUpperBound());

        getPlotChildren().add(concordiaLinePlotter.plot(concordiaLine));

        // we have nothing to layout if no data is present
        if (getData() == null) {
            return;
        }

        // add ellipse fills
        getData().stream().forEach(series -> {
            series.getData().stream().forEach(item -> {
                getPlotChildren().add(
                        errorEllipseFiller.plot(converter.convert(item)));
            });
        });

        // update ellipse positions
        getData().stream().forEach(series -> {
            series.getData().stream().forEach(item -> {
                getPlotChildren().add(
                        errorEllipsePlotter.plot(converter.convert(item)));
            });
        });
    }

    @Override
    protected void updateAxisRange() {
        final Axis<Number> xAxis = getXAxis();
        final Axis<Number> yAxis = getYAxis();

        final List<Number> xData = new ArrayList<>();
        final List<Number> yData = new ArrayList<>();

        if (xAxis.isAutoRanging() || yAxis.isAutoRanging()) {
            getData().stream().forEach(series -> {
                series.getData().stream().forEach(item -> {
                    ErrorEllipse errorEllipse = converter.convert(item);

                    if (xAxis.isAutoRanging()) {
                        xData.add(errorEllipse.getMinX(getConfidenceLevel()));
                        xData.add(errorEllipse.getMaxX(getConfidenceLevel()));
                    }

                    if (yAxis.isAutoRanging()) {
                        yData.add(errorEllipse.getMinY(getConfidenceLevel()));
                        yData.add(errorEllipse.getMaxY(getConfidenceLevel()));
                    }
                });
            });

            if (xAxis.isAutoRanging()) {
                xAxis.invalidateRange(xData);
            }

            if (yAxis.isAutoRanging()) {
                yAxis.invalidateRange(yData);
            }
        }
    }

    public void snapConcordiaLineToCorners() {
        setPlotWindow(ConcordiaLine.getX(concordiaLine.getStartT()),
                      ConcordiaLine.getX(concordiaLine.getEndT()),
                      ConcordiaLine.getY(concordiaLine.getStartT()),
                      ConcordiaLine.getY(concordiaLine.getEndT()));
    }

    public DoubleProperty confidenceLevel() {
        return confidenceLevel;
    }

    public double getConfidenceLevel() {
        return confidenceLevel.get();
    }

    public void setConfidenceLevel(double value) {
        confidenceLevel.set(value);
    }

    public ErrorEllipseChartStyleAccessor getConcordiaChartStyleAccessor() {
        return ccStyleAccessor;
    }

    private static final class DefaultConverter implements DataConverter<ErrorEllipse> {

        @Override
        public ErrorEllipse convert(Data data) {
            if (data == null || !(data.getExtraValue() instanceof Map)) {
                throw new IllegalArgumentException();
            }

            Map extra = (Map) data.getExtraValue();

            double x = getField(extra, "xValue");
            double y = getField(extra, "yValue");
            double sigmaX = getField(extra, "sigmaX");
            double sigmaY = getField(extra, "sigmaY");
            double rho = getField(extra, "rho");

            return new ErrorEllipse() {

                @Override
                public double getX() {
                    return x;
                }

                @Override
                public double getSigmaX() {
                    return sigmaX;
                }

                @Override
                public double getY() {
                    return y;
                }

                @Override
                public double getSigmaY() {
                    return sigmaY;
                }

                @Override
                public double getRho() {
                    return rho;
                }

            };
        }

        protected double getField(Map map, String field) {
            return ((ObjectProperty<Number>) map.get(field)).get().doubleValue();
        }
    }
}
