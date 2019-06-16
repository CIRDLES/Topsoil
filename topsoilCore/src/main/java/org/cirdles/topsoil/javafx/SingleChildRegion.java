package org.cirdles.topsoil.javafx;

import javafx.scene.Node;
import javafx.scene.layout.Region;

public abstract class SingleChildRegion<T extends Node> extends Region {

    private final T child;

    protected SingleChildRegion(T child) {
        this.child = child;
        getChildren().add(child);

        setFocusTraversable(true);
    }

    protected T getChild() {
        return child;
    }

    @Override
    protected double computeMinWidth(double height) {
        return child.minWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        return child.minHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return child.prefWidth(height) +
                snappedLeftInset() +
                snappedRightInset();
    }

    @Override
    protected double computePrefHeight(double width) {
        return child.prefHeight(width) +
                snappedTopInset() +
                snappedBottomInset();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double x = snappedLeftInset(),
                y = snappedTopInset(),
                width = getWidth() - (snappedLeftInset() + snappedRightInset()),
                height = getHeight() - (snappedTopInset() + snappedBottomInset());
        child.resizeRelocate(x, y, width, height);
    }

}
