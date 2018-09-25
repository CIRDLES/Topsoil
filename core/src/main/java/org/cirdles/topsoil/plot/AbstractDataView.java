package org.cirdles.topsoil.plot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 *
 * @author brycebarrett
 */
public abstract class AbstractDataView extends Canvas {

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
    
    /**
     * Some plot properties
     */
//    private final ObservableMap<String, Object> plotProperties = FXCollections.observableMap(new DefaultProperties());

    protected String TITLE = "Default Graph";
    protected String X_AXIS_LABEL = "X Default";
    protected String Y_AXIS_LABEL = "Y Default";

    protected double UNCERTAINTY = 2.0;


    /**
     * data objects
     */
    protected List<Map<String, Object>> plotData = Collections.emptyList();

    protected ArrayList xValues = new ArrayList();
    protected ArrayList yValues = new ArrayList();
    protected ArrayList sigXValues = new ArrayList();
    protected ArrayList sigYValues = new ArrayList();
    protected ArrayList rhoValues = new ArrayList();



    /**
     * ZOOM_FACTOR needs to always be above 1
     */
    protected static double ZOOM_FACTOR = 1.05;
    
    protected BigDecimal[] ticsY;
    /**
     *
     */
    protected BigDecimal[] ticsX;
    

    /**
     *
     */
    public AbstractDataView() {
        super();
    }

    /**
     *
     * @param bounds
     */
    protected AbstractDataView(Rectangle bounds, int leftMargin, int topMargin) {
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
    }

    /**
     *
     * @param gc
     */
    protected void paintInit(GraphicsContext gc) {
        relocate(x, y);
        gc.clearRect(0, 0, width, height);
    }

    /**
     *
     * @param gc
     */
    public void paint(GraphicsContext gc) {
        paintInit(gc);

        drawBorder(gc);
    }

    private void drawBorder(GraphicsContext gc) {
        // fill it in
        gc.setFill(Paint.valueOf("WHITE"));
        gc.fillRect(0, 0, width, height);

        /*// draw bordere
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
        gc.fillText(X_AXIS_LABEL, leftMargin + (graphWidth/2), topMargin/4);
        
        gc.rotate(-90);
        gc.fillText(Y_AXIS_LABEL, -(leftMargin + (graphWidth/2)), topMargin/4);
        gc.rotate(90);


    }

    /**
     *
     * @param x
     * @return
     */
    public double mapX(double x) {
        return (((x - getMinX_Display()) / getRangeX_Display()) * graphWidth) + leftMargin;
    }

    /**
     *
     * @param y
     * @return
     */
    protected double mapY(double y) {
        return (((getMaxY_Display() - y) / getRangeY_Display()) * graphHeight) + topMargin;
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    public void refreshPanel(boolean doReScale, boolean inLiveMode) {
        try {
            preparePanel();
        } catch (Exception e) {
        }
    }

    public abstract void preparePanel();

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
     *
     * @return
     */
    public double getMinX_Display() {
        return minX + getDisplayOffsetX();
    }

    /**
     *
     * @return
     */
    public double getMaxX_Display() {
        return maxX + getDisplayOffsetX();
    }

    /**
     *
     * @return
     */
    public double getMinY_Display() {
        return minY + getDisplayOffsetY();
    }

    /**
     *
     * @return
     */
    public double getMaxY_Display() {
        return maxY + getDisplayOffsetY();
    }

    /**
     *
     * @return
     */
    public double getRangeX_Display() {
        return (getMaxX_Display() - getMinX_Display());
    }

    /**
     *
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
     *
     * @return
     */
    public int getLeftMargin() {
        return leftMargin;
    }
    /**
     *
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
            
            ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphWidth/50.0));
            ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight/50.0));
            
            
            
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
    protected boolean scrollMouseInHouse(ScrollEvent evt){
        return ((evt.getX() >= leftMargin)
                && (evt.getY() >= topMargin)
                && (evt.getY() <= graphHeight + topMargin)
                && (evt.getX() <= (graphWidth + leftMargin)));
        
    }
    
    public void mouseWheelMoved(ScrollEvent evt, GraphicsContext gc) {
        if (scrollMouseInHouse(evt)) {
            
            double scrollAmount = evt.getDeltaY();
            
            
            if(scrollAmount < 0){
                double zoomOut = Math.ceil(ZOOM_FACTOR) - ZOOM_FACTOR;
                
                minX *= zoomOut;
                maxX *= zoomOut;
                minY *= zoomOut;
                maxY *= zoomOut;
                
                ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight/50.0));
                ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphHeight/50.0));
            }
            else{
                
                minX *= ZOOM_FACTOR;
                maxX *= ZOOM_FACTOR;
                minY *= ZOOM_FACTOR;
                maxY *= ZOOM_FACTOR;

                ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight/50.0));
                ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphHeight/50.0));
                
            }
            
            paint(gc);
            
        }
    }
    
    /**
     * 
     * @return ZOOM_FACTOR; value to determine how much a window is zoomed in/out upon
     * scroll wheel use
     */
    public double getZoomFactor(){
        return ZOOM_FACTOR;
    }
    
    /**
     * Method to allow the user the ability to play around with how much the 
     * graph will zoom upon scroll wheel use.
     * 
     * @param newZoom 
     */
    public void setZoomFactor(double newZoom){
        //The zoom factor cannot be lower than 1 or else zooming will not be viable
        //also the zoom factor must be under 2
        if(newZoom <= 1){
            newZoom = 1.05;
        }
        if(newZoom >= 2){
            newZoom = 1.95;
        }
        
        ZOOM_FACTOR = newZoom;
    }
    
    public void setXAxisLabel(String newXAxisLabel){
        X_AXIS_LABEL = newXAxisLabel;
        
    }

    public void setPlotData(List<Map<String, Object>> data){
        plotData = data;
    }

    public List<Map<String, Object>> getPlotData(){
        return plotData;
    }
    
    public void setYAxisLabel(String newYAxisLabel){
        Y_AXIS_LABEL = newYAxisLabel;
    }
    
    public void setTitle(String newTitle){
        TITLE = newTitle;
    }

    public void setUncertainty(double val) { UNCERTAINTY = val; }
    
}