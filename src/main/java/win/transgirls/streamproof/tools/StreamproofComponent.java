package win.transgirls.streamproof.tools;

import win.transgirls.streamproof.api.types.ComponentCategory;

public class StreamproofComponent {
    public String id;
    public String label;
    public boolean isStreamproof;
    public ComponentCategory category;

    StreamproofComponent(String id, String label, boolean streamproof, ComponentCategory category) {
        this.id = id;
        this.label = label;
        this.isStreamproof = streamproof;
        this.category = category;
    }
}