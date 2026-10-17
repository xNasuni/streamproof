package win.transgirls.streamproof.tools;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.command.RenderDispatcher;
import win.transgirls.streamproof.api.StreamproofAPI;

public final class IsolatedSubmitQueue {
    private RenderDispatcher source;
    private RenderDispatcher dispatcher;
    private OrderedRenderCommandQueueImpl storage;

    public OrderedRenderCommandQueueImpl storage() {
        RenderDispatcher game = MinecraftClient.getInstance().gameRenderer.getEntityRenderDispatcher();
        if (this.source != game) {
            if (this.dispatcher != null) {
                this.dispatcher.close();
            }
            this.storage = new OrderedRenderCommandQueueImpl();
            this.dispatcher = new RenderDispatcher(this.storage, game.blockRenderManager, game.vertexConsumers, game.atlasManager, game.outlineVertexConsumers, game.crumblingOverlayVertexConsumers, game.textRenderer);
            this.source = game;
        }
        return this.storage;
    }

    public void render() {
        if (this.dispatcher != null) {
            this.dispatcher.render();
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.forceDraw(this.source.vertexConsumers);
            }
            this.dispatcher.endLayeredCustoms();
            this.storage.onNextFrame();
        }
    }
}
