package org.cirdles.topsoil.data;

abstract class AbstractDataComponent<C extends DataComponent<C>> implements DataComponent<C> {

    private String title;
    private boolean selected;

    protected AbstractDataComponent(String title) {
        this(title, true);
    }

    protected AbstractDataComponent(String title, boolean selected) {
        this.title = title;
        this.selected = selected;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
