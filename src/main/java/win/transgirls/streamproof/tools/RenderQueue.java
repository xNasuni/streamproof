package win.transgirls.streamproof.tools;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL20C;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.systems.gl.GL;
import win.transgirls.streamproof.types.GuiRenderData;

import java.util.ArrayList;
import java.util.function.Consumer;

import net.minecraft.client.gui.DrawContext;
import win.transgirls.streamproof.types.WorldRenderData;

public class RenderQueue {
    private final ArrayList<Consumer<GuiRenderData>> guiRenderQueue = new ArrayList<>();
    private final ArrayList<Consumer<WorldRenderData>> worldRenderQueue = new ArrayList<>();

    public void deferGui(Object fallbackGraphics, Consumer<GuiRenderData> consumer) {
        if (Streamproof.obsWrapper != null && Streamproof.obsWrapper.hooked) {
            guiRenderQueue.add(consumer);
        } else {
            consumer.accept(GuiRenderData.with(fallbackGraphics));
        }
    }

    public void deferWorld(MatrixStack fallbackMatrix, VertexConsumerProvider fallbackBuffers, Consumer<WorldRenderData> consumer) {
        if (Streamproof.obsWrapper != null && Streamproof.obsWrapper.hooked) {
            worldRenderQueue.add(consumer);
        } else {
            consumer.accept(WorldRenderData.with(fallbackMatrix, fallbackBuffers));
        }
    }

    public void releaseDeferredGui(DrawContext g) {
        GuiRenderData data = GuiRenderData.with(g);

        guiRenderQueue.forEach((consumer) -> {
            consumer.accept(data);
        });

        guiRenderQueue.clear();
    }

    public void releaseDeferredWorld(MatrixStack stack, VertexConsumerProvider buffers) {
        WorldRenderData data = WorldRenderData.with(stack, buffers);
        
        worldRenderQueue.forEach((consumer) -> {
            consumer.accept(data);
        });

        worldRenderQueue.clear();
    }

    public void clear() {
        guiRenderQueue.clear();
        worldRenderQueue.clear();
    }
}