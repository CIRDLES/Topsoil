/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.topsoil.app.plot;


import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


/**
 *
 * @author brycebarrett
 */
public class CanvasController{

    
    @FXML
    private Canvas can;
    
    private AxisSetup graphAxis;
    /*
    public CanvasController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CanvasWindow.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
      }*/
    
    @FXML
    protected void initialize() {
        
        graphAxis = new AxisSetup("Base", 775, 600);
        GraphicsContext gc = can.getGraphicsContext2D();
        graphAxis.buildAxis(gc);
        
    }
    
}
    
    
