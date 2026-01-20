package win.transgirls.streamproof.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sun.jna.Function;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.imgui.ImGuiImplementation;
import win.transgirls.streamproof.systems.gl.GL;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
    @Shadow
    @Final
    GuiRenderState guiState;

    @Shadow
    @Final
    private OrderedRenderCommandQueueImpl orderedRenderCommandQueue;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private GuiRenderer guiRenderer;

    @Shadow
    @Final
    private FogRenderer fogRenderer;

    @Shadow
    @Final
    private BufferBuilderStorage buffers;

    @Inject(method = "render", at = @At("TAIL"), remap = false)
    private void injectRender(RenderTickCounter deltaTracker, boolean bl, CallbackInfo ci) {
        if (Streamproof.obsWrapper != null && Streamproof.obsWrapper.hooked) {
            Streamproof.renderSecrets = () -> {
                Profiler profilerFiller = Profilers.get();
                Framebuffer renderTarget = this.client.getFramebuffer();

                this.guiState.clear();

                int i = (int) this.client.mouse.getScaledX(this.client.getWindow());
                int j = (int) this.client.mouse.getScaledY(this.client.getWindow());

                profilerFiller.swap("streamproofGuiRendering");

                DrawContext graphics = new DrawContext(this.client, this.guiState, i, j);
                VertexConsumerProvider.Immediate buffer = this.buffers.getEntityVertexConsumers();

                this.guiState.clear();
                Streamproof.renderQueue.release(graphics, buffer);

                this.guiRenderer.render(this.fogRenderer.getFogBuffer(FogRenderer.FogType.NONE));
                this.guiRenderer.incrementFrame();

                profilerFiller.pop();
                this.orderedRenderCommandQueue.onNextFrame();

                profilerFiller.swap("streamproofBlit");
                if (!this.client.getWindow().hasZeroWidthOrHeight()) {
                    renderTarget.blitToScreen();
                }
                profilerFiller.pop();
            };
        }
    }
}