package win.transgirls.streamproof.tools;

import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.types.RenderData;

import java.util.ArrayList;
import java.util.function.Consumer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;

public class RenderQueue {
    private ArrayList<Consumer<RenderData>> renderQueue = new ArrayList<>();

    public void add(Object fallback, Consumer<RenderData> consumer) {
        if (Streamproof.renderSecrets != null && Streamproof.obsWrapper != null && Streamproof.obsWrapper.hooked) {
            renderQueue.add(consumer);
        } else {
            consumer.accept(RenderData.with(fallback));
        }
    }

    public void release(DrawContext g, VertexConsumerProvider.Immediate b) {
        RenderData data = new RenderData();
        data.graphics = g;
        data.buffer = b;

        renderQueue.forEach((consumer) -> {
            consumer.accept(data);
        });

        renderQueue.clear();
    }

    public void clear() {
        renderQueue.clear();
    }
}