package win.transgirls.streamproof.types;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;

public class RenderData {
    public DrawContext graphics;
    public VertexConsumerProvider.Immediate buffer;

    public static RenderData with(Object graphics) {
        RenderData data = new RenderData();
        data.graphics = (DrawContext) graphics;

        return data;
    }
}