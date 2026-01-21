package win.transgirls.streamproof.tools;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameModeSwitcherScreen;
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

    public void deferGui(boolean deferMe, Object fallbackGraphics, Consumer<GuiRenderData> consumer) {
        if (Streamproof.obsWrapper != null && Streamproof.obsWrapper.hooked && deferMe) {
            if (Streamproof.client.currentScreen == null || Streamproof.client.currentScreen instanceof ChatScreen || Streamproof.client.currentScreen instanceof GameModeSwitcherScreen) {
                guiRenderQueue.add(consumer);
            }
        } else {
            consumer.accept(GuiRenderData.with(fallbackGraphics));
        }
    }

    public void deferWorld(boolean deferMe, MatrixStack fallbackMatrix, VertexConsumerProvider fallbackBuffers, Consumer<WorldRenderData> consumer) {
        if (Streamproof.obsWrapper != null && Streamproof.obsWrapper.hooked && deferMe) {
            if ((Streamproof.client.currentScreen == null || Streamproof.client.currentScreen instanceof ChatScreen || Streamproof.client.currentScreen instanceof GameModeSwitcherScreen)) {
                worldRenderQueue.add(consumer);
            }
        } else {
            consumer.accept(WorldRenderData.with(fallbackMatrix, fallbackBuffers));
        }
    }

    public void releaseDeferredGui(DrawContext graphics) {
        GuiRenderData data = GuiRenderData.with(graphics);

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