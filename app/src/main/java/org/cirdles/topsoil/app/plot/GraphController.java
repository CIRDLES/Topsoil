package org.cirdles.topsoil.app.plot;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import org.cirdles.topsoil.app.table.TopsoilTableController;
import org.cirdles.topsoil.plot.AbstractDataView;
import org.cirdles.topsoil.plot.base.BasePlotDataView;
import org.cirdles.topsoil.plot.base.EllipsesPlotDataView;
import org.cirdles.topsoil.plot.base.UncertaintyBarsPlotDataView;
import org.cirdles.topsoil.plot.feature.Concordia.Concordia;
import org.cirdles.topsoil.plot.feature.Concordia.ConcordiaPanel;


/**
 * Simple adaptation of the new graphing functionality in Topsoil core
 *
 * @author brycebarrett
 */
public class GraphController implements Initializable {//extends HBox

    
    
    @FXML
    private AnchorPane canBack;
    
    /**
     * Data arrays that will be used for graphing
     */
    private double[] xValues;
    private double[] yValues;
    private double[] xSigValues;
    private double[] ySigValues;
    private double[] rhoValues;
    
    /**
     * area in which the graph will be placed. This includes all aspects of the 
     * graph. i.e. labels/title/axis marks
     */
    private int graphWidth;
    private int graphHeight;
    
    
    /*
    public GraphController(double[] xValues, double[] yValues, double[] xSigValues,
            double[] ySigValues, double[] rhoValues, int graphWidth, int graphHeight) {
        
        this.xValues = xValues;
        this.yValues = yValues;
        this.xSigValues = xSigValues;
        this.ySigValues = ySigValues;
        this.rhoValues = rhoValues;
        this.graphWidth = graphWidth;
        this.graphHeight = graphHeight;
        
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Plot.fxml"));
            //fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
      }*/
    
    /**
     * Method to allow data to be passed to the GraphController prior to the creation
     * of a new graph
     * @param table 
     */
    public void initData(TopsoilTableController table){
        
        List<Map<String, Object>> plotData = table.getPlotData();
        
        int arrSize = plotData.size();
        xValues = new double[arrSize];
        yValues = new double[arrSize];
        xSigValues = new double[arrSize];
        ySigValues = new double[arrSize];
        rhoValues = new double[arrSize];
        
        int i = 0;
        for(Map object: plotData){
            
            xValues[i] = Double.parseDouble(object.get("x").toString());
            yValues[i] = Double.parseDouble(object.get("y").toString());
            xSigValues[i] = Double.parseDouble(object.get("sigma_x").toString());
            ySigValues[i] = Double.parseDouble(object.get("sigma_y").toString());
            rhoValues[i] = Double.parseDouble(object.get("rho").toString());

            //test for tw
            //rhoValues[i] = 0;

            System.out.println("xSig Value: " + xSigValues[i]);

            System.out.println("ySig Value: " + ySigValues[i] + "\n");
                    
            i++;
            
        }
        
    }

    public void displayGraph() {
        Rectangle area = new Rectangle(761, 532);
        //little test



        ConcordiaPanel canvas = new ConcordiaPanel(area, 75, 75,
                9.8485e-10, 1.55125e-10, xValues, yValues, xSigValues, ySigValues, rhoValues);
        //sigXVals, sigYVals, uncer);


        //AbstractDataView canvas = new EllipsesPlotDataView(area, xValues, yValues, xSigValues, ySigValues, rhoValues);
        //sigXVals, sigYVals, uncer);
        
        
        canBack.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.preparePanel();
        canvas.paint(gc);
       
        canvas.setOnMousePressed((MouseEvent evt) ->{
            canvas.mousePressed(evt);
        });
        canvas.setOnMouseDragged((MouseEvent evt) -> {
            //canvas.mousePressed(evt);
            canvas.mouseDragged(evt, gc);
        });
        canvas.setOnScroll((ScrollEvent evt) -> {
            
            canvas.mouseWheelMoved(evt, gc);
        });
        
    }
    
      /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //displayGraph();

    }

}
