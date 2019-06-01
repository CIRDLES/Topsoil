package org.cirdles.topsoil.plot.internal;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.cirdles.topsoil.plot.Plot;

import java.io.*;
import java.nio.file.Files;

/**
 * {@code PDFSaver} saves an image of the current javascript plot to a PDF designated file
 *
 * @author Bryce Barrett
 */
public class PDFSaver {

    public static void save(Plot plot, File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }
        if (plot == null) {
            throw new IllegalArgumentException("Plot cannot be null.");
        }

        try {
            File tempFile = Files.createTempFile(null, "svg").toFile();
            new SVGSaver().save(plot.toSVGDocument(), tempFile);

            InputStream in = new FileInputStream(tempFile);
            OutputStream out = new FileOutputStream(file);

            Transcoder transcoder = new PDFTranscoder();
            transcoder.addTranscodingHint(PDFTranscoder.KEY_AUTO_FONTS, false);
            TranscoderInput transIn = new TranscoderInput(in);
            TranscoderOutput transOut = new TranscoderOutput(out);
            transcoder.transcode(transIn, transOut);

            out.flush();
            out.close();
            in.close();
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
    }

}
