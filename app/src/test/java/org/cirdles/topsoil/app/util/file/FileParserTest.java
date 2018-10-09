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

    @Test
    public void text_exampleUPbData() {
        testStringParsing(EXAMPLE_UPB);
        testFileParsing(EXAMPLE_UPB);
    }

    @Test
    public void test_exampleUThData() {
        testStringParsing(EXAMPLE_UTH);
        testFileParsing(EXAMPLE_UTH);
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
	        System.out.println(Arrays.toString(sample.getHeaders()));
	        System.out.println(Arrays.toString(headers));
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

                     "valid-custom-tilde.txt"),

        EXAMPLE_UPB("207Pb*/235U,±2σ (%),206Pb*/238U,±2σ (%),corr coef\n"
                    + "29.165688743,1.519417676,0.712165893,1.395116767,0.918191745\n"
                    + "29.031535970,1.799945600,0.714916493,1.647075269,0.915069472\n"
                    + "29.002008069,1.441943510,0.709482828,1.324922704,0.918845083\n"
                    + "29.203969765,1.320690194,0.707078490,1.216231698,0.920906132\n"
                    + "29.194452092,1.359029744,0.709615006,1.248057588,0.918344571\n"
                    + "29.293320455,1.424328137,0.710934267,1.309135282,0.919124777\n"
                    + "28.497489852,1.353243890,0.686951820,1.245648095,0.920490463\n"
                    + "29.218573677,1.383868032,0.715702180,1.271276031,0.918639641\n"
                    + "28.884872020,1.264304654,0.702153693,1.164978444,0.921438073\n"
                    + "28.863259209,1.455550200,0.700081472,1.335582301,0.917579003\n"
                    + "29.014325453,1.614480021,0.701464404,1.478394505,0.915709384\n"
                    + "29.917885787,1.564622589,0.725185047,1.434906094,0.917094067\n"
                    + "30.159907714,1.488528691,0.724886106,1.366282212,0.917874287\n"
                    + "28.963153308,1.480754780,0.698240706,1.359750830,0.918282249\n"
                    + "29.350104553,1.513999270,0.711983592,1.384417989,0.914411266\n"
                    + "29.979576581,1.595745814,0.724426340,1.458894294,0.914239775\n"
                    + "29.344673618,1.551935035,0.714166474,1.420060290,0.915025602",

                    ",",

                    new String[]{"207Pb*/235U", "±2σ (%)", "206Pb*/238U", "±2σ (%)", "corr coef"},

                    new Double[][]{
                            new Double[]{29.165688743,1.519417676,0.712165893,1.395116767,0.918191745},
                            new Double[]{29.031535970,1.799945600,0.714916493,1.647075269,0.915069472},
                            new Double[]{29.002008069,1.441943510,0.709482828,1.324922704,0.918845083},
                            new Double[]{29.203969765,1.320690194,0.707078490,1.216231698,0.920906132},
                            new Double[]{29.194452092,1.359029744,0.709615006,1.248057588,0.918344571},
                            new Double[]{29.293320455,1.424328137,0.710934267,1.309135282,0.919124777},
                            new Double[]{28.497489852,1.353243890,0.686951820,1.245648095,0.920490463},
                            new Double[]{29.218573677,1.383868032,0.715702180,1.271276031,0.918639641},
                            new Double[]{28.884872020,1.264304654,0.702153693,1.164978444,0.921438073},
                            new Double[]{28.863259209,1.455550200,0.700081472,1.335582301,0.917579003},
                            new Double[]{29.014325453,1.614480021,0.701464404,1.478394505,0.915709384},
                            new Double[]{29.917885787,1.564622589,0.725185047,1.434906094,0.917094067},
                            new Double[]{30.159907714,1.488528691,0.724886106,1.366282212,0.917874287},
                            new Double[]{28.963153308,1.480754780,0.698240706,1.359750830,0.918282249},
                            new Double[]{29.350104553,1.513999270,0.711983592,1.384417989,0.914411266},
                            new Double[]{29.979576581,1.595745814,0.724426340,1.458894294,0.914239775},
                            new Double[]{29.344673618,1.551935035,0.714166474,1.420060290,0.915025602}
                    },

                    "upb-example-data.csv"),

        EXAMPLE_UTH("230Th*/238U,234U*/238U,±2σ (abs),±2σ (abs),corr coef\n" +
                    "0.787174467,1.112997105,0.002472973,0.004812142,0.0\n" +
                    "0.785279872,1.104535717,0.003488836,0.003504504,0.0\n" +
                    "0.757751874,1.098862611,0.003437122,0.004937997,0.0\n" +
                    "0.756755971,1.095577076,0.002292904,0.003613920,0.0\n" +
                    "0.769622435,1.104933730,0.003069412,0.005966043,0.0\n" +
                    "0.754230241,1.099870658,0.004366264,0.003039218,0.0\n" +
                    "0.760346901,1.104336707,0.004460567,0.004830971,0.0\n" +
                    "0.759050757,1.098264378,0.002174949,0.005414218,0.0\n" +
                    "0.766429941,1.102943341,0.007290816,0.003127906,0.0\n" +
                    "0.777301225,1.101947646,0.003167341,0.003224137,0.0\n" +
                    "0.779894193,1.118173335,0.003500865,0.006966140,0.0\n" +
                    "0.781888816,1.118372424,0.003270823,0.008000935,0.0\n" +
                    "0.782985109,1.117974657,0.003832090,0.004330706,0.0\n" +
                    "0.781688601,1.111205641,0.004060869,0.007176835,0.0\n" +
                    "0.762741024,1.107920030,0.002969033,0.004823140,0.0\n" +
                    "0.763237957,1.103739947,0.004908767,0.004548051,0.0\n" +
                    "0.769023512,1.110209805,0.003525521,0.005196072,0.0\n" +
                    "0.769917231,1.105831646,0.002727962,0.005963751,0.0\n" +
                    "0.776501596,1.112997925,0.003282191,0.004246044,0.0\n" +
                    "0.776998549,1.111107425,0.003054914,0.003777516,0.0\n" +
                    "0.798544256,1.117575886,0.001562462,0.005461398,0.0\n" +
                    "0.800139952,1.114788659,0.002006875,0.005468221,0.0\n" +
                    "0.800439225,1.113992263,0.003009748,0.003583907,0.0\n" +
                    "0.803930031,1.118172970,0.002447054,0.003765470,0.0\n" +
                    "0.763039792,1.107123872,0.003881829,0.007757655,0.0",

                    ",",

                    new String[]{"230Th*/238U", "234U*/238U", "±2σ (abs)", "±2σ (abs)", "corr coef"},

                    new Double[][]{
                            new Double[]{0.787174467,1.112997105,0.002472973,0.004812142,0.0},
                            new Double[]{0.785279872,1.104535717,0.003488836,0.003504504,0.0},
                            new Double[]{0.757751874,1.098862611,0.003437122,0.004937997,0.0},
                            new Double[]{0.756755971,1.095577076,0.002292904,0.003613920,0.0},
                            new Double[]{0.769622435,1.104933730,0.003069412,0.005966043,0.0},
                            new Double[]{0.754230241,1.099870658,0.004366264,0.003039218,0.0},
                            new Double[]{0.760346901,1.104336707,0.004460567,0.004830971,0.0},
                            new Double[]{0.759050757,1.098264378,0.002174949,0.005414218,0.0},
                            new Double[]{0.766429941,1.102943341,0.007290816,0.003127906,0.0},
                            new Double[]{0.777301225,1.101947646,0.003167341,0.003224137,0.0},
                            new Double[]{0.779894193,1.118173335,0.003500865,0.006966140,0.0},
                            new Double[]{0.781888816,1.118372424,0.003270823,0.008000935,0.0},
                            new Double[]{0.782985109,1.117974657,0.003832090,0.004330706,0.0},
                            new Double[]{0.781688601,1.111205641,0.004060869,0.007176835,0.0},
                            new Double[]{0.762741024,1.107920030,0.002969033,0.004823140,0.0},
                            new Double[]{0.763237957,1.103739947,0.004908767,0.004548051,0.0},
                            new Double[]{0.769023512,1.110209805,0.003525521,0.005196072,0.0},
                            new Double[]{0.769917231,1.105831646,0.002727962,0.005963751,0.0},
                            new Double[]{0.776501596,1.112997925,0.003282191,0.004246044,0.0},
                            new Double[]{0.776998549,1.111107425,0.003054914,0.003777516,0.0},
                            new Double[]{0.798544256,1.117575886,0.001562462,0.005461398,0.0},
                            new Double[]{0.800139952,1.114788659,0.002006875,0.005468221,0.0},
                            new Double[]{0.800439225,1.113992263,0.003009748,0.003583907,0.0},
                            new Double[]{0.803930031,1.118172970,0.002447054,0.003765470,0.0},
                            new Double[]{0.763039792,1.107123872,0.003881829,0.007757655,0.0}
                    },

                    "uth-example-data.csv");

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
