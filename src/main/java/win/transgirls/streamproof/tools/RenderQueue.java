package win.transgirls.streamproof.tools;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.types.RenderData;

import java.util.ArrayList;
import java.util.function.Consumer;

public class RenderQueue {
    private ArrayList<Consumer<RenderData>> renderQueue = new ArrayList<>();

    public void add(Consumer<RenderData> consumer) {
        renderQueue.add(consumer);
    }

    public void release(GuiGraphics g, MultiBufferSource.BufferSource b) {
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