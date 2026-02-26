package win.transgirls.streamproof.api.types;

public enum ComponentCategory {
    World("World"),
    Gui("Gui"),
    Other("Other"),
    Hidden("Hidden");

    public final String label;

    ComponentCategory(String label) {
        this.label = label;
    }
}