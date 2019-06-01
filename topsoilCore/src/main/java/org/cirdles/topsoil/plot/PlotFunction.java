package org.cirdles.topsoil.plot;

public final class PlotFunction {

    private final String name;
    private final PlotType plotType;
    private final Class<?> returnType;

    private PlotFunction(String name, PlotType plotType, Class<?> returnType) {
        this.name = name;
        this.plotType = plotType;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public PlotType getPlotType() {
        return plotType;
    }

    public static final class Scatter {

        private static PlotType SCATTER = PlotType.SCATTER;

        private Scatter() {}

        public static PlotFunction GET_AXIS_EXTENTS = new PlotFunction("getAxisExtents", SCATTER, Double[].class);
        public static PlotFunction SET_AXIS_EXTENTS = new PlotFunction("setAxisExtents", SCATTER, null);
        public static PlotFunction RECENTER = new PlotFunction("recenter", SCATTER, null);
        public static PlotFunction SNAP_TO_CORNERS = new PlotFunction("snapToCorners", SCATTER, null);

    }

}
