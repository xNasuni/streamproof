package win.transgirls.streamproof.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sun.jna.Function;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
    @Shadow
    @Final
    GuiRenderState guiRenderState;

    @Shadow
    @Final
    private SubmitNodeStorage submitNodeStorage;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private GuiRenderer guiRenderer;

    @Shadow
    @Final
    private FogRenderer fogRenderer;

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow
    @Final
    private LevelRenderState levelRenderState;

    @Inject(method = "render", at = @At("TAIL"))
    private void injectRender(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        Streamproof.renderSecrets = () -> {
            ProfilerFiller profilerFiller = Profiler.get();
            RenderTarget renderTarget = this.minecraft.getMainRenderTarget();

            this.guiRenderState.reset();

            int i = (int) this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
            int j = (int) this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());

            profilerFiller.popPush("streamproofGuiRendering");

            GuiGraphics graphics = new GuiGraphics(this.minecraft, this.guiRenderState, i, j);
            MultiBufferSource.BufferSource buffer = this.renderBuffers.bufferSource();

            this.guiRenderState.reset();
            Streamproof.renderQueue.release(graphics, buffer);
            buffer.endBatch();

            this.guiRenderer.render(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
            this.guiRenderer.incrementFrameNumber();

            profilerFiller.pop();
            this.submitNodeStorage.endFrame();

            profilerFiller.popPush("streamproofBlit");
            if (!this.minecraft.getWindow().isMinimized()) {
                renderTarget.blitToScreen();
            }
            profilerFiller.pop();

            profilerFiller.popPush("streamproofUpdateDisplay");
            if (this.minecraft.tracyFrameCapture != null) {
                this.minecraft.tracyFrameCapture.upload();
                this.minecraft.tracyFrameCapture.capture(renderTarget);
            }
            profilerFiller.pop();
        };
    }
}