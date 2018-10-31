package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.file.FileParser;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.app.menu.MenuItemEventHandler;

import java.io.IOException;

/**
 * A variety of example data for each isotope system supported by Topsoil.
 * Contains sample data table as if they were pasted from clipboard, allowing
 * for a quick use with {@link MenuItemEventHandler} and the Open Example Table
 * menu.
 *
 * @author Adrien Laubus
 *
 * @see FileParser
 * @see MenuItemEventHandler
 */
public class ExampleDataTable {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    public static final String UPB_DATA = "207Pb*/235U,±2σ (%),206Pb*/238U,±2σ (%),corr coef\n"
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
                                           + "29.344673618,1.551935035,0.714166474,1.420060290,0.915025602";
    public static final String UTH_DATA = "230Th*/238U,±2σ (abs),234U*/238U,±2σ (abs),corr coef\n"
                                           + "0.787174467,0.002472973,1.112997105,0.004812142,0.0\n"
                                           + "0.785279872,0.003488836,1.104535717,0.003504504,0.0\n"
                                           + "0.757751874,0.003437122,1.098862611,0.004937997,0.0\n"
                                           + "0.756755971,0.002292904,1.095577076,0.00361392,0.0\n"
                                           + "0.769622435,0.003069412,1.10493373,0.005966043,0.0\n"
                                           + "0.754230241,0.004366264,1.099870658,0.003039218,0.0\n"
                                           + "0.760346901,0.004460567,1.104336707,0.004830971,0.0\n"
                                           + "0.759050757,0.002174949,1.098264378,0.005414218,0.0\n"
                                           + "0.766429941,0.007290816,1.102943341,0.003127906,0.0\n"
                                           + "0.777301225,0.003167341,1.101947646,0.003224137,0.0\n"
                                           + "0.779894193,0.003500865,1.118173335,0.00696614,0.0\n"
                                           + "0.781888816,0.003270823,1.118372424,0.008000935,0.0\n"
                                           + "0.782985109,0.00383209,1.117974657,0.004330706,0.0\n"
                                           + "0.781688601,0.004060869,1.111205641,0.007176835,0.0\n"
                                           + "0.762741024,0.002969033,1.10792003,0.00482314,0.0\n"
                                           + "0.763237957,0.004908767,1.103739947,0.004548051,0.0\n"
                                           + "0.769023512,0.003525521,1.110209805,0.005196072,0.0\n"
                                           + "0.769917231,0.002727962,1.105831646,0.005963751,0.0\n"
                                           + "0.776501596,0.003282191,1.112997925,0.004246044,0.0\n"
                                           + "0.776998549,0.003054914,1.111107425,0.003777516,0.0\n"
                                           + "0.798544256,0.001562462,1.117575886,0.005461398,0.0\n"
                                           + "0.800139952,0.002006875,1.114788659,0.005468221,0.0\n"
                                           + "0.800439225,0.003009748,1.113992263,0.003583907,0.0\n"
                                           + "0.803930031,0.002447054,1.11817297,0.00376547,0.0\n"
                                           + "0.763039792,0.003881829,1.107123872,0.007757655,0.0";
    public static final String GEN_DATA = "";

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static ObservableDataTable getUPb() {
        return getExampleDataTable(IsotopeSystem.UPB);
    }

    public static ObservableDataTable getUTh() {
        return getExampleDataTable(IsotopeSystem.UTH);
    }

    public static ObservableDataTable getGen() {
        return getExampleDataTable(IsotopeSystem.GENERIC);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Returns the correct Example Data table for the specified
     * {@code IsotopeSystem}.
     *
     * @param   isotopeSystem
     *          the IsotopeSystem of the example table
     * @return  example ObservableDataTable
     */
    private static ObservableDataTable getExampleDataTable(IsotopeSystem isotopeSystem) {
        String content;
        UncertaintyFormat format;
        switch (isotopeSystem) {
            case UPB:
                content = UPB_DATA;
                format = UncertaintyFormat.TWO_SIGMA_PERCENT;
                break;
            case UTH:
                content = UTH_DATA;
                format = UncertaintyFormat.TWO_SIGMA_ABSOLUTE;
                break;
            default:
                content = GEN_DATA;
                format = UncertaintyFormat.ONE_SIGMA_ABSOLUTE;
                break;
        }

        try {
            String delim = FileParser.getDelimiter(content);
            String[] headers = FileParser.parseHeaders(content, delim);
            Double[][] data = FileParser.parseData(content, delim);

            ObservableDataTable table = new ObservableDataTable(data, true, headers, isotopeSystem, format);
            table.setTitle("Example UPb Data");

            return table;

        } catch (IOException e) {
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Uh Oh",
                    "Unable to load example data table: " + isotopeSystem.getName()
            );
        }
        return null;
    }
}
