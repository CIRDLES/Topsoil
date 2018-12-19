package org.cirdles.topsoil.plot.internal;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDInlineImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    public static void saveToPDF(WritableImage plotToSave, String uncertaintyFormat){
        generateSaveUI().ifPresent(stream -> {
                writeToPDF(plotToSave, stream, uncertaintyFormat);
        });

    }

    private static void writeToPDF(WritableImage plotToSave, OutputStream out, String uncertaintyFormat){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try{
            ImageIO.write(SwingFXUtils.fromFXImage(plotToSave, null), "png", bos);
            bos.flush();
            String base64String=Base64.encode(bos.toByteArray());
            bos.close();
            byte[] byteArray = Base64.decode(base64String);


            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();
            PDImageXObject pdImage;
            PDPageContentStream content;

            pdImage = PDImageXObject.createFromByteArray(doc, byteArray, "plotImg");
            content = new PDPageContentStream(doc, page);
            content.drawImage(pdImage, 80, 210, 500, 400);
            content.beginText();
            content.setFont(PDType1Font.TIMES_ROMAN, 18);
            content.newLineAtOffset(200, 200);
            uncertaintyFormat = uncertaintyFormat.replace("σ", "s");
            content.showText("Uncertainty Format: " + uncertaintyFormat);
            content.endText();

            content.close();
            doc.addPage(page);
            doc.save(out);
            doc.close();

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
