package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.isotope.IsotopeType;

/**
 * A variety of example data for each isotope system supported by Topsoil. Contains
 * sample data table as if they were pasted from clipboard, allowing for a quick use
 * with {@link MenuItemEventHandler} and the Open Example Table menu.
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
     * Constructs the {@code ExampleDataTable}, with sample data for each isotope system.
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
        UThSampleData = "207Pb*/235U,206Pb*/238U,±2σ (%),±2σ (%),corr coef\n"
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
        GenericSampleData = null;
    }

    /**
     * Returns the correct Example Data table for the specified {@code IsotopeType}.
     *
     * @param isotopeType the isotope type desired for the sample data table
     * @return  a Sample Data table
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
     * @return  GenericSampleData
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
     * @return  UThSampleData
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
     * @return  GenericSampleData
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
