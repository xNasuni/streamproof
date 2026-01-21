package win.transgirls.streamproof.tools;

public enum ComponentCategory {
    WORLD("World"),
    GUI("Gui"),
    NOT_FOUND("Not Installed"),
    HIDDEN("Hidden");

    public final String label;

    ComponentCategory(String label) {
        this.label = label;
    }
}