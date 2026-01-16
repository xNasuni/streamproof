package win.transgirls.streamproof.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
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

    //    @Inject(method = "render", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=guiRendering"))
//    private void injectRender(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
//        Streamproof.renderQueue.release();
//    }
    @Inject(method = "render", at = @At("TAIL"))
    private void injectRender(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
//        if (Streamproof.renderQueue.size() != 0) {
//            ProfilerFiller profilerFiller = Profiler.get();
//
//            profilerFiller.popPush("blit");
//            if (!this.minecraft.getWindow().isMinimized()) {
//                this.minecraft.getMainRenderTarget().blitToScreen();
//            }
//            profilerFiller.pop();
//
//            profilerFiller.popPush("streamproofUpdateDisplay");
//            if (this.minecraft.tracyFrameCapture != null) {
//                this.minecraft.tracyFrameCapture.upload();
//                this.minecraft.tracyFrameCapture.capture(this.minecraft.getMainRenderTarget());
//            }
//
//            this.minecraft.getWindow().updateDisplay(this.minecraft.tracyFrameCapture);
//            profilerFiller.pop();
//
//            this.guiRenderState.reset();
//            int i = (int) this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
//            int j = (int) this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());
//            GuiGraphics guiGraphics = new GuiGraphics(this.minecraft, this.guiRenderState, i, j);
//            profilerFiller.popPush("streamproofGuiRendering");
//            Streamproof.renderQueue.release(guiGraphics);
//            this.guiRenderer.render(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
//            this.guiRenderer.incrementFrameNumber();
//            profilerFiller.pop();
//            this.submitNodeStorage.endFrame();
//            Streamproof.LOGGER.info("streamproofRender");
//            Streamproof.afterRendering = true;
//        }
    }
}