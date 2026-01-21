package win.transgirls.streamproof.types;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;

public class GuiRenderData {
    public DrawContext graphics;

    public static GuiRenderData with(Object graphics) {
        GuiRenderData data = new GuiRenderData();
        data.graphics = (DrawContext) graphics;

        return data;
    }
}