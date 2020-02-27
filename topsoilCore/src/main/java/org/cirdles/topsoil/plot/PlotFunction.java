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

        public static final PlotFunction GET_AXIS_EXTENTS = new PlotFunction("getAxisExtents", SCATTER, Double[].class);
        public static final PlotFunction SET_AXIS_EXTENTS = new PlotFunction("changeAxisExtents", SCATTER, null);
        public static final PlotFunction RESIZE = new PlotFunction("resize", PlotType.SCATTER, null);
        public static final PlotFunction RECENTER = new PlotFunction("resetView", SCATTER, null);
        public static final PlotFunction SNAP_TO_CORNERS = new PlotFunction("snapToConcordia", SCATTER, null);

    }

}
