package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.isotope.IsotopeType;

/**
 * A variety of example data for each isotope system supported by Topsoil.
 * Contains sample data table as if they were pasted from clipboard, allowing
 * for a quick use with {@link MenuItemEventHandler} and the Open Example Table
 * menu.
 *
 * @author Adrien Laubus
 * @see FileParser
 * @see MenuItemEventHandler
 */
public class ExampleDataTable {

    //***********************
    // Attributes
    //***********************
    /**
     * Sample data for the Uranium Lead isotope system.
     */
    private String UPbSampleData;
    /**
     * Sample data for the Uranium Thorium isotope system.
     */
    private String UThSampleData;
    /**
     * Sample data when no isotope system is selected.
     */
    private String GenericSampleData;

    //***********************
    // Constructors
    //***********************
    /**
     * Constructs the {@code ExampleDataTable}, with sample data for each
     * isotope system.
     */
    public ExampleDataTable() {
        UPbSampleData = "207Pb*/235U,206Pb*/238U,±2σ (%),±2σ (%),corr coef\n"
                + "29.165688743,0.712165893,1.519417676,1.395116767,0.918191745\n"
                + "29.031535970,0.714916493,1.799945600,1.647075269,0.915069472\n"
                + "29.002008069,0.709482828,1.441943510,1.324922704,0.918845083\n"
                + "29.203969765,0.707078490,1.320690194,1.216231698,0.920906132\n"
                + "29.194452092,0.709615006,1.359029744,1.248057588,0.918344571\n"
                + "29.293320455,0.710934267,1.424328137,1.309135282,0.919124777\n"
                + "28.497489852,0.686951820,1.353243890,1.245648095,0.920490463\n"
                + "29.218573677,0.715702180,1.383868032,1.271276031,0.918639641\n"
                + "28.884872020,0.702153693,1.264304654,1.164978444,0.921438073\n"
                + "28.863259209,0.700081472,1.455550200,1.335582301,0.917579003\n"
                + "29.014325453,0.701464404,1.614480021,1.478394505,0.915709384\n"
                + "29.917885787,0.725185047,1.564622589,1.434906094,0.917094067\n"
                + "30.159907714,0.724886106,1.488528691,1.366282212,0.917874287\n"
                + "28.963153308,0.698240706,1.480754780,1.359750830,0.918282249\n"
                + "29.350104553,0.711983592,1.513999270,1.384417989,0.914411266\n"
                + "29.979576581,0.724426340,1.595745814,1.458894294,0.914239775\n"
                + "29.344673618,0.714166474,1.551935035,1.420060290,0.915025602";
        UThSampleData = "230Th*/238U,234U*/238U,±2σ (abs),±2σ (abs),corr coef\n"
                + "0.787174467,1.112997105,0.002472973,0.004812142,0.0\n"
                + "0.785279872,1.104535717,0.003488836,0.003504504,0.0\n"
                + "0.757751874,1.098862611,0.003437122,0.004937997,0.0\n"
                + "0.756755971,1.095577076,0.002292904,0.00361392,0.0\n"
                + "0.769622435,1.10493373,0.003069412,0.005966043,0.0\n"
                + "0.754230241,1.099870658,0.004366264,0.003039218,0.0\n"
                + "0.760346901,1.104336707,0.004460567,0.004830971,0.0\n"
                + "0.759050757,1.098264378,0.002174949,0.005414218,0.0\n"
                + "0.766429941,1.102943341,0.007290816,0.003127906,0.0\n"
                + "0.777301225,1.101947646,0.003167341,0.003224137,0.0\n"
                + "0.779894193,1.118173335,0.003500865,0.00696614,0.0\n"
                + "0.781888816,1.118372424,0.003270823,0.008000935,0.0\n"
                + "0.782985109,1.117974657,0.00383209,0.004330706,0.0\n"
                + "0.781688601,1.111205641,0.004060869,0.007176835,0.0\n"
                + "0.762741024,1.10792003,0.002969033,0.00482314,0.0\n"
                + "0.763237957,1.103739947,0.004908767,0.004548051,0.0\n"
                + "0.769023512,1.110209805,0.003525521,0.005196072,0.0\n"
                + "0.769917231,1.105831646,0.002727962,0.005963751,0.0\n"
                + "0.776501596,1.112997925,0.003282191,0.004246044,0.0\n"
                + "0.776998549,1.111107425,0.003054914,0.003777516,0.0\n"
                + "0.798544256,1.117575886,0.001562462,0.005461398,0.0\n"
                + "0.800139952,1.114788659,0.002006875,0.005468221,0.0\n"
                + "0.800439225,1.113992263,0.003009748,0.003583907,0.0\n"
                + "0.803930031,1.11817297,0.002447054,0.00376547,0.0\n"
                + "0.763039792,1.107123872,0.003881829,0.007757655,0.0";
        GenericSampleData = null;
    }

    /**
     * Returns the correct Example Data table for the specified
     * {@code IsotopeType}.
     *
     * @param isotopeType the isotope type desired for the sample data table
     * @return a Sample Data table
     */
    public String getSampleData(IsotopeType isotopeType) {
        switch (isotopeType) {
            case UPb:
                return UPbSampleData;
            case UTh:
                return UThSampleData;
            default:
                return GenericSampleData;
        }
    }

    /**
     * Returns the Uranium Lead Example Data table.
     *
     * @return GenericSampleData
     */
    public String getUPbSampleData() {
        return UPbSampleData;
    }

    /**
     * Sets the text of the UPbSampleData to change the data used.
     *
     * @param UPbSampleData Correctly delimited data table
     */
    public void setUPbSampleData(String UPbSampleData) {
        this.UPbSampleData = UPbSampleData;
    }

    /**
     * Returns the Uranium Thorium Example Data table.
     *
     * @return UThSampleData
     */
    public String getUThSampleData() {
        return UThSampleData;
    }

    /**
     * Sets the text of the UThSampleData to change the data used.
     *
     * @param UThSampleData Correctly delimited data table
     */
    public void setUThSampleData(String UThSampleData) {
        this.UThSampleData = UThSampleData;
    }

    /**
     * Returns the Generic Example Data table.
     *
     * @return GenericSampleData
     */
    public String getGenericSampleData() {
        return GenericSampleData;
    }

    /**
     * Sets the text of the GenericSampleData to change the data used.
     *
     * @param GenericSampleData Correctly delimited data table
     */
    public void setGenericSampleData(String GenericSampleData) {
        this.GenericSampleData = GenericSampleData;
    }

}
