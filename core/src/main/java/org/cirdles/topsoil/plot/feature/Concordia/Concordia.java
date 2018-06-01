package org.cirdles.topsoil.plot.feature.Concordia;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.cirdles.topsoil.plot.AbstractDataView;
import org.cirdles.topsoil.plot.TicGeneratorForAxes;

/**
 * Class that will handle the creation of the Wetherill Concordia Line
 */
public class Concordia extends AbstractDataView{

    private double lambda235;
    private double lambda238;

    private double[] xValues;
    private double[] yValues;

    private double minT;
    private double maxT;


    private String concordiaType = "WC";
    private ConcordiaLine lineToBePlotted = null;

    //decides if points should be displayed
    private boolean pointsVisible = true;


    public Concordia(Rectangle bounds, double lambda235, double lambda238, double[] xValues, double[] yValues) {

        super(bounds, 75, 75);
        this.xValues = xValues;
        this.yValues = yValues;

        this.lambda235 = lambda235;
        this.lambda238 = lambda238;

        //init min/max values for axis
        minX = 0;
        maxX = 0;
        minY = 0;
        maxY = 0;

    }

    @Override
    public void paint(GraphicsContext gc) {
        super.paint(gc);

        if(concordiaType == "WC"){

            lineToBePlotted = buildConcordia();

            drawConcordiaLineSegments(gc, lineToBePlotted);

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

    private ConcordiaLine buildConcordia(){

        double minX_t = Math.log1p(getMinX_Display()) / lambda235;
        double minY_t = Math.log1p(getMinY_Display()) / lambda238;

        minT = (Math.max(minX_t, minY_t));

        double maxX_t = Math.log1p(getMaxX_Display()) / lambda235;
        double maxY_t = Math.log1p(getMaxY_Display()) / lambda238;

        maxT = (Math.min(maxX_t, maxY_t));

        ConcordiaLine myConcordiaLine
                = new ConcordiaLine(new ConcordiaLineSegment(lambda235, lambda238, minT, maxT));


        myConcordiaLine.refineLineByRecursiveHalving(5);

        return myConcordiaLine;
    }

    private void drawConcordiaLineSegments(GraphicsContext gc, ConcordiaLine lineToDraw){

        gc.beginPath();

        ParametricCurveSegment drawSeg = lineToDraw.getStartSeg();
        ParametricCurveSegment saveSeg = drawSeg;

        gc.moveTo(
                (float) mapX(drawSeg.minX()),
                (float) mapY(drawSeg.minY()));

        gc.bezierCurveTo(
                (float) mapX(drawSeg.minX()),
                (float) mapY(drawSeg.minY()),
                (float) mapX(drawSeg.controlX()),
                (float) mapY(drawSeg.controlY()),
                (float) mapX(drawSeg.maxX()),
                (float) mapY(drawSeg.maxY()));

        saveSeg = drawSeg;
        drawSeg = drawSeg.getRightSeg();

        while ((drawSeg != null) && (drawSeg.getLeftSeg() != null)) { // stops traversal to fake upper envelope
            gc.bezierCurveTo(//
                    mapX(drawSeg.minX()), //
                    mapY(drawSeg.minY()), //
                    mapX(drawSeg.controlX()), //
                    mapY(drawSeg.controlY()), //
                    mapX(drawSeg.maxX()), //
                    mapY(drawSeg.maxY()));

            saveSeg = drawSeg;
            drawSeg = drawSeg.getRightSeg();
        }
        gc.setStroke(Color.BLACK);
        gc.stroke();
    }




    @Override
    public void preparePanel() {

        minX = xValues[0];
        maxX = xValues[0];

        for (int i = 0; i < xValues.length; i++) {
            minX = Math.min(xValues[i], minX);
            maxX = Math.max(xValues[i], maxX);
        }


        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.05);
        minX -= xMarginStretch;
        maxX += xMarginStretch;


        ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphWidth / 25.0));

        minY = yValues[0];
        maxY = yValues[0];

        for (int i = 0; i < yValues.length; i++) {
            minY = Math.min(yValues[i], minY);
            maxY = Math.max(yValues[i], maxY);
        }


        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
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
     * @param newUncertain
     * @param gc
     */
    public void refreshData(double[] xValuesNew, double[] yValuesNew, double[] newYSig,
                            double[] newXSig, double[] newUncertain, GraphicsContext gc) {

        xValues = new double[xValuesNew.length];
        yValues = new double[yValuesNew.length];

        //want a deep copy for xValues
        for (int i = 0; i < xValuesNew.length; i++) {
            xValues[i] = xValuesNew[i];
        }
        //want a deep copy for yValues
        for (int i = 0; i < yValuesNew.length; i++) {
            yValues[i] = yValuesNew[i];
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

}
