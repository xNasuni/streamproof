package win.transgirls.streamproof.types;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class WorldRenderData {
    public MatrixStack stack;
    public VertexConsumerProvider buffers;

    public static WorldRenderData with(Object fallbackMatrix, Object fallbackBuffers) {
        WorldRenderData data = new WorldRenderData();
        try {
            data.stack = (MatrixStack) fallbackMatrix;
            data.buffers = (VertexConsumerProvider) fallbackBuffers;
        } catch (Throwable ignored) {
        }

        return data;
    }
}