package win.transgirls.streamproof.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import win.transgirls.streamproof.api.StreamproofAPI;

public final class IsolatedSubmitQueue {
    private FeatureRenderDispatcher source;
    private FeatureRenderDispatcher dispatcher;
    private SubmitNodeStorage storage;

    public SubmitNodeStorage storage() {
        FeatureRenderDispatcher game = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        if (this.source != game) {
            if (this.dispatcher != null) {
                this.dispatcher.close();
            }
            this.storage = new SubmitNodeStorage();
            this.dispatcher = new FeatureRenderDispatcher(this.storage, game.modelManager, game.bufferSource, game.atlasManager, game.outlineBufferSource, game.crumblingBufferSource, game.font, game.gameRenderState);
            this.source = game;
        }
        return this.storage;
    }

    public void render() {
        if (this.dispatcher != null) {
            this.dispatcher.renderAllFeatures();
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.forceDraw(this.source.bufferSource);
            }
            this.dispatcher.endFrame();
            this.storage.endFrame();
        }
    }
}
