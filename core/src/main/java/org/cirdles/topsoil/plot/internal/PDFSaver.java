package org.cirdles.topsoil.plot.internal;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.Optional;

/**
 * {@code PDFSaver} saves an image of the current javascript plot to a PDF designated file
 *
 * @author Bryce Barrett
 */
public class PDFSaver {

    private static final File FILE_CHOOSER_INITIAL_DIRECTORY
            = new File(System.getProperty("user.home"));


    //***********************
    // Methods
    //***********************

    public static void saveToPDF(WritableImage plotToSave){
        generateSaveUI().ifPresent(stream -> {
                writeToPDF(plotToSave, stream);
        });

    }

    private static void writeToPDF(WritableImage plotToSave, OutputStream out){

        String pathToImage = FILE_CHOOSER_INITIAL_DIRECTORY.toString() + "/nametonotbespoken123412.png";
        File file = new File(pathToImage);

        try{
            ImageIO.write(SwingFXUtils.fromFXImage(plotToSave, null), "png", file);

            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();
            PDImageXObject pdImage;
            PDPageContentStream content;

            pdImage = PDImageXObject.createFromFile(pathToImage, doc);
            content = new PDPageContentStream(doc, page);
            content.drawImage(pdImage, 80, 210, 500, 400);
            content.close();
            doc.addPage(page);
            doc.save(out);
            doc.close();
            file.delete();

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private static FileChooser getFileChooser() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Export to PDF");
        fileChooser.setInitialDirectory(FILE_CHOOSER_INITIAL_DIRECTORY);

        fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("PDF Document", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        return fileChooser;
    }

    private static Optional<OutputStream> generateSaveUI() {
        return Optional.ofNullable(getFileChooser().showSaveDialog(null))
                .map(file -> {
                    try {
                        return new FileOutputStream(file);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                });
    }
}
