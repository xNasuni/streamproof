package win.transgirls.streamproof.api.types;

public interface Impl {
    boolean isAvailable();

    boolean isWriting();

    boolean isStreamproof(String id);

    RenderTarget getRenderTarget(String id);

    void add(String id, String label, ComponentCategory category, boolean defaultStreamproof);

    void begin(String id);

    void end(String id);

    void _startFbo(RenderTarget target);

    void _stopFbo(RenderTarget target);
}