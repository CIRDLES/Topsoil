package org.cirdles.jfxutils;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
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
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author John
 */
public class NodeToSVGConverter {
    
    // SVG is a US standard and should have numbers formatted in the appropriate style
    private static NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

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
            svg.setAttribute("preserveAspectRatio", "xMinYMin");
            svg.setAttribute("viewBox", String.format(Locale.US, "0 0 %f %f",
                                                      node.getBoundsInLocal().getWidth(),
                                                      node.getBoundsInLocal().getHeight()));
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
        if (!node.isVisible() || node.getOpacity() == 0) {
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
            element = document.createElement("g");

            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                Element childElement = convertNodeToElement(child, document);

                if (childElement != null) {
                    element.appendChild(childElement);
                }
            }
            
            // handle clip property
            if (node.getClip() != null) {
                Element clip = convertNodeToElement(node.getClip(), document);
                
                Element clipPath = document.createElement("clipPath");
                clipPath.appendChild(clip);
                String clipID = "clip" + node.getClip().hashCode();
                clipPath.setAttribute("id", clipID);
                
                Element defs = document.createElement("defs");
                defs.appendChild(clipPath);
                
                element.setAttribute("clip-path", "url(#" + clipID + ")");
                element.appendChild(defs);
            }

            if (node instanceof Region) {
                convertRegion(node, element, document);
            }
        } else if (node instanceof Line) {
            Line line = (Line) node;
            if (colorToRGBString((Color) line.getStroke()).equals("none")) {
                return null;
            }

            element = document.createElement("line");
            element.setAttribute("x1", numberFormat.format(line.getStartX()));
            x = line.getStartX();
            element.setAttribute("y1", numberFormat.format(line.getStartY()));
            y = line.getStartY();
            element.setAttribute("x2", numberFormat.format(line.getEndX()));
            element.setAttribute("y2", numberFormat.format(line.getEndY()));

            element.setAttribute("stroke", colorToRGBString((Color) line.getStroke()));
            element.setAttribute("stroke-width", numberFormat.format(line.getStrokeWidth()));
        } else if (node instanceof Path) {
            element = convertPath(node, element, document);
        } else if (node instanceof Circle) {
            element = convertCircle(node, element, document);
        } else if (node instanceof Rectangle) {
            element = convertRectangle((Rectangle) node, element, document);
        } else if (node instanceof Text) {
            Text text = (Text) node;

            element = document.createElement("text");

            element.setTextContent(text.getText());
            element.setAttribute("x", numberFormat.format(text.getX()));
            x = text.getX();
            element.setAttribute("y", numberFormat.format(text.getY()));
            y = text.getY();
            element.setAttribute("font-family", "Verdana");
            element.setAttribute("font-size", numberFormat.format(text.getFont().getSize()));
        }

        try {
            //Getting the transforms
            StringBuilder tranforms_partstring = new StringBuilder();

            convertTransforms(node, tranforms_partstring, x, y);

            element.setAttribute("transform", String.format(Locale.US, tranforms_partstring.toString() + "translate(%f %f)",
                                                            node.getLayoutX(),
                                                            node.getLayoutY()));
            if (!node.getStyle().equals("")) {
                element.setAttribute("style", node.getStyle());
            }
        } catch (NullPointerException ex) {
            System.err.printf("Nodes of class %s are not supported.", node.getClass().getName());
        }

        return element;
    }

    private void convertTransforms(Node node, StringBuilder tranforms_partstring, double x, double y) {
        // Heads up : May not work if multiple tranform of the same type in the scene
        for (Transform t : node.getTransforms()) {
            if (t instanceof Rotate) {
                //Heads up : Don't take in account the values PivotX, Y and Z
                Rotate r = (Rotate) t;
                tranforms_partstring.append(String.format(Locale.US, "rotate(%f, %f %f) ", r.getAngle(), x, y));
            } else if (t instanceof Translate) {
                Translate r = (Translate) t;
                tranforms_partstring.append(String.format(Locale.US, "translate(%f %f) ", t.getTx(), t.getTy()));
            }
        }
    }

    private Element convertCircle(Node node, Element element, Document document) throws DOMException {
        Circle circle = (Circle) node;
        element = document.createElement("circle");
        element.setAttribute("cx", numberFormat.format(circle.getCenterX() + circle.getLayoutX()));
        element.setAttribute("cy", numberFormat.format(circle.getCenterY() + circle.getLayoutY()));
        element.setAttribute("r", numberFormat.format(circle.getRadius()));
        element.setAttribute("stroke", colorToRGBString((Color) circle.getStroke()));
        element.setAttribute("fill", colorToRGBString((Color) circle.getFill()));
        try {
            element.setAttribute("opacity", numberFormat.format(circle.opacityProperty().get()));
        } catch (NullPointerException ex) {
            System.out.println(circle + " has no opacity defined");
        }
        return element;
    }

    private Element convertPath(Node node, Element element, Document document) throws DOMException {
        Path path = (Path) node;
        element = document.createElement("path");
        StringBuilder d = new StringBuilder();
        for (PathElement pathElement : path.getElements()) {
            if (pathElement instanceof MoveTo) {
                MoveTo moveTo = (MoveTo) pathElement;
                
                d.append(String.format(Locale.US, "M %f %f ",
                        moveTo.getX() + path.getLayoutX(),
                        moveTo.getY() + path.getLayoutY()));
            } else if (pathElement instanceof CubicCurveTo) {
                CubicCurveTo cubicCurveTo = (CubicCurveTo) pathElement;
                
                d.append(String.format(Locale.US, "C %f %f %f %f %f %f ",
                        cubicCurveTo.getControlX1() + path.getLayoutX(),
                        cubicCurveTo.getControlY1() + path.getLayoutY(),
                        cubicCurveTo.getControlX2() + path.getLayoutX(),
                        cubicCurveTo.getControlY2() + path.getLayoutY(),
                        cubicCurveTo.getX() + path.getLayoutX(),
                        cubicCurveTo.getY() + path.getLayoutY()));
            } else if (pathElement instanceof LineTo) {
                LineTo lineTo = (LineTo) pathElement;
                
                d.append(String.format(Locale.US, "L %f %f ",
                        lineTo.getX() + path.getLayoutX(),
                        lineTo.getY() + path.getLayoutY()));
            }
        }
        element.setAttribute("d", d.toString().trim());
        element.setAttribute("stroke", colorToRGBString((Color) path.getStroke()));
        element.setAttribute("stroke-width", numberFormat.format(path.getStrokeWidth()));
        element.setAttribute("fill", colorToRGBString((Color) path.getFill()));
        try {
            element.setAttribute("opacity", numberFormat.format(path.opacityProperty().get()));
        } catch (NullPointerException ex) {
            System.out.println(path + " has no opacity defined");
        }
        return element;
    }
    
    private void convertRegion(Node node, Element element, Document document) throws DOMException {
        Region region = (Region) node;
        
        if (region.getBorder() != null) {
            Border border = region.getBorder();
            
            for (BorderStroke borderStroke : border.getStrokes()) {
                BorderWidths borderWidths = borderStroke.getWidths();
                
                if (borderStroke.getTopStroke().isOpaque() && borderWidths.getTop() > 0) {
                    Line line = new Line(0, 0, region.getWidth(), 0);
                    line.setStroke(borderStroke.getTopStroke());
                    line.setStrokeWidth(borderWidths.getTop());
                    
                    element.appendChild(convertNodeToElement(line, document));
                }
                
                if (borderStroke.getBottomStroke().isOpaque() && borderWidths.getBottom() > 0) {
                    Line line = new Line(0, region.getHeight(), region.getWidth(), region.getHeight());
                    line.setStroke(borderStroke.getRightStroke());
                    line.setStrokeWidth(borderWidths.getRight());
                    
                    element.appendChild(convertNodeToElement(line, document));
                }
                
                if (borderStroke.getLeftStroke().isOpaque() && borderWidths.getLeft() > 0) {
                    Line line = new Line(0, 0, 0, region.getHeight());
                    line.setStroke(borderStroke.getLeftStroke());
                    line.setStrokeWidth(borderWidths.getLeft());
                    
                    element.appendChild(convertNodeToElement(line, document));
                }
                
                if (borderStroke.getRightStroke().isOpaque() && borderWidths.getRight() > 0) {
                    Line line = new Line(region.getWidth(), 0, region.getWidth(), region.getHeight());
                    line.setStroke(borderStroke.getRightStroke());
                    line.setStrokeWidth(borderWidths.getRight());
                    
                    element.appendChild(convertNodeToElement(line, document));
                }
            }
        }
    }

    private Element convertRectangle(Rectangle rectangle, Element element, Document document) {
        Element rect = document.createElement("rect");
        rect.setAttribute("x", numberFormat.format(rectangle.getX()));
        rect.setAttribute("y", numberFormat.format(rectangle.getY()));
        rect.setAttribute("width", numberFormat.format(rectangle.getWidth()));
        rect.setAttribute("height", numberFormat.format(rectangle.getHeight()));
        
        return rect;
    }

    private String colorToRGBString(Color color) {
        if (color == null || color == Color.TRANSPARENT || !color.isOpaque()) {
            return "none";
        }

        int red = (int) (color.getRed() * 256);
        int green = (int) (color.getGreen() * 256);
        int blue = (int) (color.getBlue() * 256);

        return String.format(Locale.US, "rgb(%d, %d, %d)", red, green, blue);
    }
}
