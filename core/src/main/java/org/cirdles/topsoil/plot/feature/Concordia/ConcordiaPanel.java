package org.cirdles.topsoil.plot.feature.Concordia;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import Jama.Matrix;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.cirdles.topsoil.plot.TicGeneratorForAxes;

/**
 *
 * @author brycebarrett
 */
public class ConcordiaPanel extends Canvas {

    protected double x;
    protected double y;
    protected double width;
    protected double height;

    /**
     *
     */
    protected int graphWidth;
    /**
     *
     */
    protected int graphHeight;
    /**
     *
     */
    private int topMargin = 0;
    /**
     *
     */
    private int leftMargin = 0;
    /**
     *
     */
    protected double minX;
    /**
     *
     */
    protected double maxX;
    /**
     *
     */
    protected double minY;
    /**
     *
     */
    protected double maxY;
    /**
     *
     */
    private double displayOffsetY = 0;
    /**
     *
     */
    private double displayOffsetX = 0;

    /**
     *
     */
    protected double zoomMinX;
    protected double zoomMinY;
    protected double zoomMaxX;
    protected double zoomMaxY;


    protected String TITLE = "Default Graph";
    protected String X_AXIS_LABEL = "238U/206Pb";
    protected String Y_AXIS_LABEL = "207Pb/206Pb";

    protected double UNCERTAINTY = 2.0;

    private boolean displayCenters = true;


    /**
     * data objects
     */
    protected List<Map<String, Object>> plotData = Collections.emptyList();

    protected double[] xValues;
    protected double[] yValues;
    protected double[] sigXValues;
    protected double[] sigYValues;
    protected double[] rho;

    //protected ArrayList xValues = new ArrayList();
    //protected ArrayList yValues = new ArrayList();
    //protected ArrayList sigXValues = new ArrayList();
    //protected ArrayList sigYValues = new ArrayList();
    //protected ArrayList rhoValues = new ArrayList();


    /**
     * ZOOM_FACTOR needs to always be above 1
     */
    protected static double ZOOM_FACTOR = 1.05;

    protected BigDecimal[] ticsY;
    /**
     *
     */
    protected BigDecimal[] ticsX;


    //Start Concordia

    private double lambda235;
    private double lambda238;

    private double minT;
    private double maxT;


    private String concordiaType = "TW";
    private ConcordiaLine lineToBePlotted = null;


    /**
     *
     */
    public ConcordiaPanel() {
        super();
    }

    /**
     * @param bounds
     */
    public ConcordiaPanel(Rectangle bounds, int leftMargin, int topMargin, double lambda235,
                          double lambda238, double[] xValues, double[] yValues, double[] sigXValues, double[] sigYValues,
                          double[] rho) {
        super(bounds.getWidth(), bounds.getHeight());
        x = bounds.getX();
        y = bounds.getY();
        this.leftMargin = leftMargin;
        this.topMargin = topMargin;


        width = bounds.getWidth();
        height = bounds.getHeight();
        graphWidth = (int) width - leftMargin;
        graphHeight = (int) height - topMargin;

        this.ticsY = null;
        this.ticsX = null;

        this.leftMargin = leftMargin;
        this.topMargin = topMargin;

        this.lambda235 = lambda235;
        this.lambda238 = lambda238;

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

    /**
     * @param gc
     */
    protected void paintInit(GraphicsContext gc) {
        relocate(x, y);
        gc.clearRect(0, 0, width, height);

        gc.setFill(Paint.valueOf("WHITE"));
        gc.fillRect(0, 0, width, height);
    }

    /**
     * @param gc
     */
    public void paint(GraphicsContext gc) {
        paintInit(gc);


        if(concordiaType == "WC"){

            lineToBePlotted = buildConcordia();

            drawConcordiaLineSegments(gc, lineToBePlotted);

        }

        //Ellipses
        //Draw Ellipses
        int entriesNum = xValues.length;
        for (int i = 0; i < entriesNum; i++) {

            generateEllipsePathIII(xValues[i], yValues[i], rho[i],
                    sigXValues[i], sigYValues[i], gc);
        }

        if(displayCenters){

            gc.setFill(Color.BLACK);
            for (int i = 0; i < xValues.length; i++){

                //check to be sure values are within current graph area
                if(xValues[i] > minX && xValues[i] < maxX
                        && yValues[i] > minY && yValues[i] < maxY){

                /*first two values(x/y coordinates) need to be offset
                as the fillOval method actually maps those first 2 inputs
                to the upper left point of the oval/circle
                */
                    gc.fillOval(mapX(xValues[i]) - 2.5, mapY(yValues[i]) - 2.5, 5, 5);
                }

            }
        }


        //remove parts not within graph area
        gc.clearRect(0, 0, width, topMargin);

        gc.clearRect(0, 0, leftMargin, height);


        drawBorder(gc);


    }

    private void drawBorder(GraphicsContext gc) {

        /*// draw border
        gc.setStroke(Paint.valueOf("BLACK"));
        gc.setLineWidth(1);
        gc.strokeRect(0, 0, width, height);
        */
        //Outline for graph area
        gc.setStroke(Paint.valueOf("BLACK"));
        gc.setLineWidth(1);
        gc.strokeRect(
                leftMargin, topMargin, graphWidth - 1, graphHeight - 1);

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Lucida Sans", 15));
        gc.fillText(X_AXIS_LABEL, leftMargin + (graphWidth / 2), topMargin / 4);

        gc.rotate(-90);
        gc.fillText(Y_AXIS_LABEL, -(leftMargin + (graphWidth / 2)), topMargin / 4);
        gc.rotate(90);


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

    /**
     * @param x
     * @return
     */
    public double mapX(double x) {
        return (((x - getMinX_Display()) / getRangeX_Display()) * graphWidth) + leftMargin;
    }

    /**
     * @param y
     * @return
     */
    protected double mapY(double y) {
        return (((getMaxY_Display() - y) / getRangeY_Display()) * graphHeight) + topMargin;
    }

    /**
     * @param doReScale  the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    public void refreshPanel(boolean doReScale, boolean inLiveMode) {
        try {
            preparePanel();
        } catch (Exception e) {
        }
    }

    public void preparePanel(){

        minX = xValues[0];
        maxX = xValues[0];

        for (int i = 0; i < xValues.length; i++) {
            minX = Math.min(xValues[i], minX);
            maxX = Math.max(xValues[i], maxX);
        }


        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.8);
        minX -= xMarginStretch;
        maxX += xMarginStretch;



        ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphWidth / 50.0));

        minY = yValues[0];
        maxY = yValues[0];

        for (int i = 0; i < yValues.length; i++) {
            minY = Math.min(yValues[i], minY);
            maxY = Math.max(yValues[i], maxY);
        }


        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.8);
        minY -= yMarginStretch;
        maxY += yMarginStretch;



        ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 50.0));
    }

    /**
     * Method to start the process of creating the WC line
     * @return
     */
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

    /**
     *
     * @param gc
     * @param lineToDraw
     */
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



    protected void generateEllipsePathIII(double xVal, double yVal, double rho, double sigX, double sigY,
                                          GraphicsContext gc) {

        ErrorEllipse ee = new ErrorEllipse(xVal, yVal, rho, sigX, sigY, UNCERTAINTY);

        gc.beginPath();


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
     * @return the displayOffsetY
     */
    public double getDisplayOffsetY() {
        return displayOffsetY;
    }

    /**
     * @param displayOffsetY the displayOffsetY to set
     */
    public void setDisplayOffsetY(double displayOffsetY) {
        this.displayOffsetY = displayOffsetY;
    }

    /**
     * @return the displayOffsetX
     */
    public double getDisplayOffsetX() {
        return displayOffsetX;
    }

    /**
     * @param displayOffsetX the displayOffsetX to set
     */
    public void setDisplayOffsetX(double displayOffsetX) {
        this.displayOffsetX = displayOffsetX;
    }

    /**
     * @return
     */
    public double getMinX_Display() {
        return minX + getDisplayOffsetX();
    }

    /**
     * @return
     */
    public double getMaxX_Display() {
        return maxX + getDisplayOffsetX();
    }

    /**
     * @return
     */
    public double getMinY_Display() {
        return minY + getDisplayOffsetY();
    }

    /**
     * @return
     */
    public double getMaxY_Display() {
        return maxY + getDisplayOffsetY();
    }

    /**
     * @return
     */
    public double getRangeX_Display() {
        return (getMaxX_Display() - getMinX_Display());
    }

    /**
     * @return
     */
    public double getRangeY_Display() {
        return (getMaxY_Display() - getMinY_Display());
    }

    protected double convertMouseXToValue(double x) {
        return //
                ((x - leftMargin) / (double) graphWidth) //
                        * getRangeX_Display()//
                        + getMinX_Display();
    }

    protected double convertMouseYToValue(double y) {
        return //
                (1.0 - ((double) (y - topMargin) / graphHeight)) //
                        * getRangeY_Display()//
                        + getMinY_Display();
    }

    /**
     * @return
     */
    public int getLeftMargin() {
        return leftMargin;
    }

    /**
     * @return
     */
    public int getTopMargin() {
        return topMargin;
    }

    protected boolean mouseInHouse(MouseEvent evt) {
        return ((evt.getX() >= leftMargin)
                && (evt.getY() >= topMargin)
                && (evt.getY() <= graphHeight + topMargin)
                && (evt.getX() <= (graphWidth + leftMargin)));
    }

    public void mouseDragged(MouseEvent evt, GraphicsContext gc) {

        double slowDownScroll = .05;
        if (mouseInHouse(evt)) {
            zoomMaxX = evt.getX();
            zoomMaxY = evt.getY();

            double offsetX = convertMouseXToValue(zoomMinX) - convertMouseXToValue(zoomMaxX);
            double offsetY = convertMouseYToValue(zoomMinY) - convertMouseYToValue(zoomMaxY);

            minX += (slowDownScroll * offsetX);
            maxX += (slowDownScroll * offsetX);
            minY += (slowDownScroll * offsetY);
            maxY += (slowDownScroll * offsetY);

            ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphWidth / 50.0));
            ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 50.0));


            //zoomMinX = zoomMaxX;
            //zoomMinY = zoomMaxY;

            paint(gc);
        }
    }

    public void mousePressed(MouseEvent evt) {
        zoomMinX = evt.getX();
        zoomMinY = evt.getY();

    }

    /*
    public void mouseReleased(MouseEvent evt) {
        zoomMaxX = evt.getX();
        zoomMaxY = evt.getY();
    }*/

    /**
     * Extra mouse position check method needed as a ScrollEvent is not
     * a subclass of a mouseEvent. A MouseEvent is a type of InputEvent whereas
     * a ScrollEvent is a type of GestureEvent. This differs from how mouse/scroll
     * events are handled in Swing.
     */
    protected boolean scrollMouseInHouse(ScrollEvent evt) {
        return ((evt.getX() >= leftMargin)
                && (evt.getY() >= topMargin)
                && (evt.getY() <= graphHeight + topMargin)
                && (evt.getX() <= (graphWidth + leftMargin)));

    }

    public void mouseWheelMoved(ScrollEvent evt, GraphicsContext gc) {
        if (scrollMouseInHouse(evt)) {

            double scrollAmount = evt.getDeltaY();


            if (scrollAmount < 0) {
                double zoomOut = Math.ceil(ZOOM_FACTOR) - ZOOM_FACTOR;

                minX *= zoomOut;
                maxX *= zoomOut;
                minY *= zoomOut;
                maxY *= zoomOut;

                ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 50.0));
                ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphHeight / 50.0));
            } else {

                minX *= ZOOM_FACTOR;
                maxX *= ZOOM_FACTOR;
                minY *= ZOOM_FACTOR;
                maxY *= ZOOM_FACTOR;

                ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 50.0));
                ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphHeight / 50.0));

            }

            paint(gc);

        }
    }

    /**
     * @return ZOOM_FACTOR; value to determine how much a window is zoomed in/out upon
     * scroll wheel use
     */
    public double getZoomFactor() {
        return ZOOM_FACTOR;
    }

    /**
     * Method to allow the user the ability to play around with how much the
     * graph will zoom upon scroll wheel use.
     *
     * @param newZoom
     */
    public void setZoomFactor(double newZoom) {
        //The zoom factor cannot be lower than 1 or else zooming will not be viable
        //also the zoom factor must be under 2
        if (newZoom <= 1) {
            newZoom = 1.05;
        }
        if (newZoom >= 2) {
            newZoom = 1.95;
        }

        ZOOM_FACTOR = newZoom;
    }

    public void setXAxisLabel(String newXAxisLabel) {
        X_AXIS_LABEL = newXAxisLabel;

    }

    public void setPlotData(List<Map<String, Object>> data) {
        plotData = data;
    }

    public List<Map<String, Object>> getPlotData() {
        return plotData;
    }

    public void setYAxisLabel(String newYAxisLabel) {
        Y_AXIS_LABEL = newYAxisLabel;
    }

    public void setTitle(String newTitle) {
        TITLE = newTitle;
    }

    public void setUncertainty(double val) {
        UNCERTAINTY = val;
    }

}