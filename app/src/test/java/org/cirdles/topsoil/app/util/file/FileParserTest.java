package org.cirdles.topsoil.app.util.file;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.cirdles.topsoil.app.util.file.FileParserTest.TestData.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author marottajb
 */
public class FileParserTest extends ApplicationTest {

    @Test
    public void test_validTSV() {
        testStringParsing(VALID_TSV);
        testFileParsing(VALID_TSV);
    }

    @Test
    public void test_validCSV() {
        testStringParsing(VALID_CSV);
        testFileParsing(VALID_CSV);
    }

    @Test
    public void test_validTXT() {
        testStringParsing(VALID_CUSTOM);
        testFileParsing(VALID_CUSTOM);
    }

    private void testStringParsing(TestData sample) {
        try {
            String delim;
            if (sample == VALID_CUSTOM) {
                delim = sample.getDelim();
            } else {
                 delim = FileParser.getDelimiter(sample.getContent());
            }
            assertEquals(sample.getDelim(), delim);

            String[] headers = FileParser.parseHeaders(sample.getContent(), delim);
            assertArrayEquals(sample.getHeaders(), headers);

            Double[][] data = FileParser.parseData(sample.getContent(), delim);
            assertArrayEquals(sample.getData(), data);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void testFileParsing(TestData sample) {
        try {
            Path path = Paths.get(getClass().getResource(sample.getPath()).toURI());

            String delim;
            if (sample == VALID_CUSTOM) {
                delim = sample.getDelim();
            } else {
                delim = FileParser.getDelimiter(path);
            }
            assertEquals(sample.getDelim(), delim);

            String[] headers = FileParser.parseHeaders(path, delim);
            assertArrayEquals(sample.getHeaders(), headers);

            Double[][] data = FileParser.parseData(path, delim);
            assertArrayEquals(sample.getData(), data);

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    enum TestData {

        VALID_TSV("header1\theader2\theader3\n" +
                  "1.1\t2.1\t3.1\n" +
                  "1.2\t2.2\t3.2\n" +
                  "1.3\t2.3\t3.3",

                  "\t",

                  new String[]{"header1", "header2", "header3"},

                  new Double[][]{
                        new Double[]{1.1, 2.1, 3.1},
                        new Double[]{1.2, 2.2, 3.2},
                        new Double[]{1.3, 2.3, 3.3}
                  },

                  "valid.tsv"),

        VALID_CSV("header1,header2,header3\n" +
                  "1.1,2.1,3.1\n" +
                  "1.2,2.2,3.2\n" +
                  "1.3,2.3,3.3",

                  ",",

                  new String[]{"header1", "header2", "header3"},

                  new Double[][]{
                          new Double[]{1.1, 2.1, 3.1},
                          new Double[]{1.2, 2.2, 3.2},
                          new Double[]{1.3, 2.3, 3.3}
                  },

                  "valid.csv"),

        VALID_CUSTOM("header1~header2~header3\n" +
                     "1.1~2.1~3.1\n" +
                     "1.2~2.2~3.2\n" +
                     "1.3~2.3~3.3",

                     "~",

                     new String[]{"header1", "header2", "header3"},

                     new Double[][]{
                             new Double[]{1.1, 2.1, 3.1},
                             new Double[]{1.2, 2.2, 3.2},
                             new Double[]{1.3, 2.3, 3.3}
                     },

                     "valid-custom-tilde.txt");

        private String content;
        private String delim;
        private String[] headers;
        private Double[][] data;
        private String path;

        TestData(String content, String delim, String[] headers, Double[][] data, String path) {
            this.content = content;
            this.delim = delim;
            this.headers = headers;
            this.data = data;
            this.path = path;
        }

        public String getContent() {
            return content;
        }

        public String getDelim() {
            return delim;
        }

        public String[] getHeaders() {
            return headers;
        }

        public Double[][] getData() {
            return data;
        }

        public String getPath() {
            return path;
        }
    }
}
