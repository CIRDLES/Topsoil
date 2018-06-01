/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.topsoil.plot.base;

import Jama.Matrix;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.cirdles.topsoil.plot.AbstractDataView;
import org.cirdles.topsoil.plot.TicGeneratorForAxes;
import org.cirdles.topsoil.plot.feature.Concordia.ErrorEllipse;

/**
 *
 * @author brycebarrett
 */
public class EllipsesPlotDataView extends AbstractDataView {


    private double[] xValues;
    private double[] yValues;

    private double[] sigXValues;
    private double[] sigYValues;
    private double[] rho;

    //decides if points should be displayed
    private boolean pointsVisible = true;


    public EllipsesPlotDataView(Rectangle bounds, double[] xValues, double[] yValues,
                                double[] sigXValues, double[] sigYValues, double[] rho) {

        super(bounds, 75, 75);
        this.xValues = xValues;
        this.yValues = yValues;

        this.sigXValues = sigXValues;
        this.sigYValues = sigYValues;
        this.rho = rho;

        //init min/max values for axis
        minX = 0;
        maxX = 0;
        minY = 0;
        maxY = 0;

    }

    @Override
    public void paint(GraphicsContext gc) {
        super.paint(gc);


        //Draw Ellipses
        int entriesNum = xValues.length;
        for (int i = 0; i < entriesNum; i++) {

            generateEllipsePathIII(xValues[i], yValues[i], rho[i],
                    sigXValues[i], sigYValues[i], gc);
        }


        //set up the points within the graph window
        if (pointsVisible) {
            gc.setFill(Color.BLACK);
            for (int i = 0; i < xValues.length; i++) {

                //check to be sure values are within current graph area
                if (xValues[i] > minX && xValues[i] < maxX
                        && yValues[i] > minY && yValues[i] < maxY) {

                    /*first two values(x/y coordinates) need to be offset
                    as the fillOval method actually maps those first 2 inputs
                    to the upper left point of the oval/circle*/

                    gc.fillOval(mapX(xValues[i]) - 2.5, mapY(yValues[i]) - 2.5, 5, 5);
                }

            }
        }

        // tics for Y Axis
        float verticalTextShift = 3.1f;
        gc.setFont(Font.font("Lucida Sans", 10));
        gc.setFill(Color.BLACK);
        if (ticsY != null) {
            for (int i = 0; i < ticsY.length; i++) {
                if (ticsY[i].doubleValue() >= minY && ticsY[i].doubleValue() <= maxY) {
                    try {
                        gc.strokeLine(
                                mapX(minX), mapY(ticsY[i].doubleValue()), mapX(minX) - 8, mapY(ticsY[i].doubleValue()));

                        gc.fillText(ticsY[i].toPlainString(),//
                                (float) mapX(minX) - 45f,
                                (float) mapY(ticsY[i].doubleValue()) + verticalTextShift);
                    } catch (Exception e) {
                    }
                }
            }
        }

        // tics for X Axis
        float horizontalTextShift = 0.3f;
        if (ticsX != null) {
            for (int i = 0; i < ticsX.length; i++) {
                if (ticsX[i].doubleValue() >= minX && ticsX[i].doubleValue() <= maxX) {
                    try {
                        gc.strokeLine(
                                mapX(ticsX[i].doubleValue()), mapY(maxY), mapX(ticsX[i].doubleValue()), mapY(maxY) - 8);

                        gc.fillText(ticsX[i].toPlainString(),//
                                (float) mapX(ticsX[i].doubleValue()),// - horizontalTextShift),
                                (float) mapY(maxY) - 30f);
                    } catch (Exception e) {
                    }
                }
            }
        }


    }


    @Override
    public void preparePanel() {

        minX = xValues[0];
        maxX = xValues[0];

        for (int i = 0; i < xValues.length; i++) {
            minX = Math.min(xValues[i], minX);
            maxX = Math.max(xValues[i], maxX);
        }


        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.2);
        minX -= xMarginStretch;
        maxX += xMarginStretch;


        ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphWidth / 25.0));

        minY = yValues[0];
        maxY = yValues[0];

        for (int i = 0; i < yValues.length; i++) {
            minY = Math.min(yValues[i], minY);
            maxY = Math.max(yValues[i], maxY);
        }


        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.2);
        minY -= yMarginStretch;
        maxY += yMarginStretch;


        ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 25.0));

    }

    /**
     * Method to update the data being passed into the graph for when new data needs to be
     * displayed
     *
     * @param xValuesNew
     * @param yValuesNew
     * @param newYSig
     * @param newXSig
     * @param newRho
     * @param gc
     */
    public void refreshData(double[] xValuesNew, double[] yValuesNew, double[] newYSig,
                            double[] newXSig, double[] newRho, GraphicsContext gc) {

        xValues = new double[xValuesNew.length];
        yValues = new double[yValuesNew.length];
        sigXValues = new double[newXSig.length];
        sigYValues = new double[newYSig.length];
        rho = new double[newRho.length];

        //want a deep copy for xValues
        for (int i = 0; i < xValuesNew.length; i++) {
            xValues[i] = xValuesNew[i];
        }
        //want a deep copy for yValues
        for (int i = 0; i < yValuesNew.length; i++) {
            yValues[i] = yValuesNew[i];
        }

        for (int i = 0; i < newXSig.length; i++) {
            sigXValues[i] = newXSig[i];
        }

        for (int i = 0; i < newYSig.length; i++) {
            sigYValues[i] = newYSig[i];
        }

        for (int i = 0; i < newRho.length; i++) {
            rho[i] = newRho[i];
        }

        //redraw graph
        this.paint(gc);
    }

    /**
     * Method to return whether or not the points will be displayed along with
     * the Uncertainty Bars
     *
     * @return pointsVisible
     */
    public boolean getPointsVisible() {
        return pointsVisible;
    }

    /**
     * Method to set whether or not the points will be displayed
     *
     * @param newPointVisibility
     */
    public void setPointsVisible(boolean newPointVisibility) {

        pointsVisible = newPointVisibility;

    }


    protected void generateEllipsePathIII(double xVal, double yVal, double rho, double sigX, double sigY,
                                          GraphicsContext gc) {

        gc.beginPath();

        ErrorEllipse ee = new ErrorEllipse(
                xVal,
                yVal,
                rho,
                sigX,
                sigY,
                UNCERTAINTY);

        int pointCount = 13;

        Matrix ellipseXY = ee.getEllipseControlPoints();

        gc.moveTo(
                mapX(ellipseXY.get(0, 0)),
                mapY(ellipseXY.get(0, 1)));

        for (int i = 1; i < pointCount; i += 3) {
            gc.bezierCurveTo(
                    mapX(ellipseXY.get(i, 0)),
                    mapY(ellipseXY.get(i, 1)),
                    mapX(ellipseXY.get(i + 1, 0)),
                    mapY(ellipseXY.get(i + 1, 1)),
                    mapX(ellipseXY.get(i + 2, 0)),
                    mapY(ellipseXY.get(i + 2, 1)));
        }

        Color eFill = Color.valueOf("Gray");

        double red = eFill.getRed();
        double green = eFill.getGreen();
        double blue = eFill.getBlue();

        eFill = Color.color(red, green, blue, 0.5);

        gc.setFill(eFill);
        gc.fill();

        gc.setStroke(Color.BLACK);
        gc.stroke();

        gc.closePath();

    }


    /**
     * Will calculate matrix to be used to find the dot product (multiplication) with
     * the control points matrix
     * @param sigX
     * @param sigY
     * @param rho
     * @return matrix to be used in the calculation
     */
    private Matrix ellipseData(double sigX, double sigY, double rho) {

        double[][] eData = new double[][]
                {{sigX, rho * sigY},
                        {0, sigY * Math.sqrt(1 - rho * rho)}};


        return new Matrix(eData);

    }
}