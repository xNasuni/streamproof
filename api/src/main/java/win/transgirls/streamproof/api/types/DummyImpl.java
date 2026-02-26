package win.transgirls.streamproof.api.types;

public class DummyImpl implements Impl {
    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public boolean isWriting() {
        return false;
    }

    @Override
    public boolean isStreamproof(String id) {
        return false;
    }

    @Override
    public RenderTarget getRenderTarget(String id) {
        return null;
    }

    @Override
    public void add(String id, String label, ComponentCategory category, boolean defaultStreamproof) {

    }

    @Override
    public void begin(String id) {

    }

    @Override
    public void end(String id) {

    }

    @Override
    public void _startFbo(RenderTarget target) {

    }

    @Override
    public void _stopFbo(RenderTarget target) {

    }
}