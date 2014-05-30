package org.cirdles.jfxutils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author John
 */
public class NodeToSVGConverter {

    DocumentBuilder documentBuilder;
    Transformer transformer;

    public NodeToSVGConverter() {
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        } catch (ParserConfigurationException | TransformerConfigurationException ex) {
            Logger.getLogger(NodeToSVGConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Write a single JavaFX node to the given SVG file.
     * 
     * @param node
     * @param output 
     */
    public void convert(Node node, File output) {
        try {
            Document document = documentBuilder.newDocument();
            document.setXmlStandalone(true);

            Element svg = document.createElement("svg");
            svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
            svg.setAttribute("version", "1.1");
            svg.appendChild(convertNodeToElement(node, document));

            document.appendChild(svg);

            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD SVG 1.1//EN");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd");
            
            transformer.transform(new DOMSource(document), new StreamResult(output));
        } catch (TransformerException ex) {
            Logger.getLogger(NodeToSVGConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Write a list of JavaFX nodes to the given SVG file.
     * 
     * @param nodes
     * @param output 
     */
    public void convert(ObservableList<Node> nodes, File output) {
        try {
            Document document = documentBuilder.newDocument();
            document.setXmlStandalone(true);

            Element svg = document.createElement("svg");
            svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
            svg.setAttribute("version", "1.1");

            for (Node node : nodes) {
                Element element = convertNodeToElement(node, document);

                if (element != null) {
                    System.out.println(node);
                    svg.appendChild(element);
                } else {
                    System.out.println(node + " is null");
                }
            }

            document.appendChild(svg);

            transformer.transform(new DOMSource(document), new StreamResult(output));
        } catch (TransformerException ex) {
            Logger.getLogger(NodeToSVGConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Recursively converts a node (and all of its children if it's a parent) into an SVG XML element.
     * 
     * @param node
     * @param document
     * @return 
     */
    private Element convertNodeToElement(Node node, Document document) {
        // Don't show nodes that aren't visible!
        if (!node.isVisible()) {
            return null;
        }
    
        Element element = null;
        /*
         * Store the X and Y position for the element, to be reused in the tranformation at the end of the function.
         * May, sometime, not be set.
         */
        double x = 0;
        double y = 0;
        
         if (node instanceof Parent) {
             //"I said goddamn! Goddamn... Goddamn"
            element = document.createElement("g");

            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                Element childElement = convertNodeToElement(child, document);

                if (childElement != null) {
                    element.appendChild(childElement);
                }
            }
        } else if (node instanceof Line) {
            Line line = (Line) node;

            element = document.createElement("line");
            element.setAttribute("x1", String.valueOf(line.getStartX())); x = line.getStartX();
            element.setAttribute("y1", String.valueOf(line.getStartY())); y = line.getStartY();
            element.setAttribute("x2", String.valueOf(line.getEndX()));
            element.setAttribute("y2", String.valueOf(line.getEndY()));

            element.setAttribute("stroke", colorToRGBString((Color) line.getStroke()));
            element.setAttribute("stroke-width", String.valueOf(line.getStrokeWidth()));
        } else if (node instanceof Path) {
            Path path = (Path) node;

            element = document.createElement("path");

            StringBuilder d = new StringBuilder();
            for (PathElement pathElement : path.getElements()) {
                if (pathElement instanceof MoveTo) {
                    MoveTo moveTo = (MoveTo) pathElement;

                    d.append(String.format("M %f %f ",
                                           moveTo.getX() + path.getLayoutX(),
                                           moveTo.getY() + path.getLayoutY()));
                } else if (pathElement instanceof CubicCurveTo) {
                    CubicCurveTo cubicCurveTo = (CubicCurveTo) pathElement;

                    d.append(String.format("C %f %f %f %f %f %f ",
                                           cubicCurveTo.getControlX1() + path.getLayoutX(),
                                           cubicCurveTo.getControlY1() + path.getLayoutY(),
                                           cubicCurveTo.getControlX2() + path.getLayoutX(),
                                           cubicCurveTo.getControlY2() + path.getLayoutY(),
                                           cubicCurveTo.getX() + path.getLayoutX(),
                                           cubicCurveTo.getY() + path.getLayoutY()));
                } else if (pathElement instanceof LineTo) {
                    LineTo lineTo = (LineTo) pathElement;

                    d.append(String.format("L %f %f ",
                                           lineTo.getX() + path.getLayoutX(),
                                           lineTo.getY() + path.getLayoutY()));
                }
            }

            element.setAttribute("d", d.toString().trim());

            element.setAttribute("stroke", colorToRGBString((Color) path.getStroke()));
            element.setAttribute("stroke-width", String.valueOf(path.getStrokeWidth()));
            element.setAttribute("fill", colorToRGBString((Color) path.getFill()));

            try {
                element.setAttribute("opacity", String.valueOf(path.opacityProperty().get()));
            } catch (NullPointerException ex) {
                System.out.println(path + " has no opacity defined");
            }
        }else if (node instanceof Circle) {
            Circle circle = (Circle) node;

            element = document.createElement("circle");

            element.setAttribute("cx", String.valueOf(circle.getCenterX() + circle.getLayoutX()));
            element.setAttribute("cy", String.valueOf(circle.getCenterY() + circle.getLayoutY()));
            element.setAttribute("r", String.valueOf(circle.getRadius()));
            element.setAttribute("stroke", colorToRGBString((Color) circle.getStroke()));
            element.setAttribute("fill", colorToRGBString((Color) circle.getFill()));

            try {
                element.setAttribute("opacity", String.valueOf(circle.opacityProperty().get()));
            } catch (NullPointerException ex) {
                System.out.println(circle + " has no opacity defined");
            }
        } else if (node instanceof Text) {
            Text text = (Text) node;    

            element = document.createElement("text");

            element.setTextContent(text.getText());
            element.setAttribute("x", String.valueOf(text.getX())); x= text.getX();
            element.setAttribute("y", String.valueOf(text.getY())); y= text.getY();
            element.setAttribute("font-family", "Verdana");
            element.setAttribute("font-size", String.valueOf(text.getFont().getSize()));
        } 

        try {
            //Getting the transforms
            StringBuilder tranforms_partstring = new StringBuilder();
           
            //Heads up : May not work if multiple tranform of the same type in the scene
            for(Transform t: node.getTransforms()){
                if(t instanceof Rotate){
                    //Heads up : Don't take in account the values PivotX, Y and Z
                    Rotate r = (Rotate) t;
                    tranforms_partstring.append(String.format("rotate(%f, %f %f) ", r.getAngle(), x, y));
                } else if (t instanceof Translate){
                    Translate r = (Translate) t;
                    tranforms_partstring.append(String.format("translate(%f %f) ", t.getTx(), t.getTy()));
                }
            }
            
            element.setAttribute("transform", String.format(tranforms_partstring.toString()+"translate(%f %f)",
                                                            node.getLayoutX(),
                                                            node.getLayoutY()));
            if (!node.getStyle().equals("")) {
                element.setAttribute("style", node.getStyle());
            }
        } catch (NullPointerException ex) {
            System.err.printf("Nodes of class %s are not supported.", node.getClass().getName());
        }

        return element; //haribo
    }

    private String colorToRGBString(Color color) {
        if (color == null || color == Color.TRANSPARENT) {
            return "none";
        }

        int red = (int) (color.getRed() * 256);
        int green = (int) (color.getGreen() * 256);
        int blue = (int) (color.getBlue() * 256);

        return String.format("rgb(%d, %d, %d)", red, green, blue);
    }
}
