package win.transgirls.streamproof.mixin.impl;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.client.gui.render.GuiItemAtlas;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.api.types.RenderTarget;
import win.transgirls.streamproof.tools.StreamproofImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {
    @Unique
    private final Set<GuiRenderer.Draw> streamproof$drawsToCapture = new HashSet<>();
    @Unique
    private boolean streamproof$currentBatchIsStreamproof = false;

    @Shadow
    @Final
    private List<GuiRenderer.Draw> draws;

    @Shadow
    private StagedVertexBuffer.Draw previousDraw;

    @Shadow
    @Final
    private GuiRenderState renderState;

    @WrapMethod(method = "render()V")
    private void wrapRender(Operation<Void> original) {
        this.streamproof$drawsToCapture.clear();
        this.streamproof$currentBatchIsStreamproof = false;

        original.call();
    }

    // (xNasuni):
    // TextGuiElementRenderState transitions into many GlyphGuiElementRenderState for each glyph during preparation
    // it's very important to keep tagging them because the GlyphGuiElementRenderStates are not added during writing
    @WrapOperation(method = "prepareText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/state/gui/GuiRenderState;forEachText(Ljava/util/function/Consumer;)V"))
    private void proxyPrepareTextElements(GuiRenderState instance, Consumer<GuiTextRenderState> consumer, Operation<Void> original) {
        original.call(instance, (Consumer<GuiTextRenderState>) (state) -> {
            int sizeBefore = instance.current.glyphStates != null
                    ? instance.current.glyphStates.size()
                    : 0;

            consumer.accept(state);

            List<GuiElementRenderState> states = instance.current.glyphStates;
            int sizeAfter = states != null ? states.size() : 0;

            if (sizeAfter > sizeBefore && Streamproof.renderStates.contains(state)) {
                for (int i = sizeBefore; i < sizeAfter; i++) {
                    Streamproof.renderStates.add(states.get(i));
                }
            }
        });
    }

    // (xNasuni):
    // ItemGuiElementRenderState transitions into one SimpleGuiElementRenderState, but we should
    // handle more than one anyway just because it is possible this maybe changes in the future
    @WrapMethod(method = "submitBlitFromItemAtlas")
    private void wrapPrepareItem(GuiItemRenderState itemState, GuiItemAtlas.SlotView slotView, Operation<Void> original) {
        GuiRenderState.Node currentLayer = this.renderState.current;

        int sizeBefore = currentLayer.elementStates != null
                ? currentLayer.elementStates.size()
                : 0;

        original.call(itemState, slotView);

        List<GuiElementRenderState> states = currentLayer.elementStates;
        int sizeAfter = states != null ? states.size() : 0;

        if (sizeAfter > sizeBefore && Streamproof.renderStates.contains(itemState)) {
            for (int i = sizeBefore; i < sizeAfter; i++) {
                Streamproof.renderStates.add(states.get(i));
            }
        }
    }

    @WrapOperation(method = "preparePictureInPictureState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/pip/PictureInPictureRenderer;prepare(Lnet/minecraft/client/renderer/state/gui/pip/PictureInPictureRenderState;Lnet/minecraft/client/renderer/state/gui/GuiRenderState;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;I)V"))
    private <T extends PictureInPictureRenderState> void wrapPrepareSpecialElement(PictureInPictureRenderer<T> instance, T renderState, GuiRenderState guiRenderState, FeatureRenderDispatcher featureRenderDispatcher, int guiScale, Operation<Void> original) {
        boolean isStreamproof = Streamproof.renderStates.contains(renderState);
        if (isStreamproof) {
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.writing = true;
            }
        }
        original.call(instance, renderState, guiRenderState, featureRenderDispatcher, guiScale);
        if (isStreamproof) {
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.writing = false;
            }
        }
    }

    @Inject(method = "addElementToMesh", at = @At("HEAD"))
    private void splitStreamproofBatches(GuiElementRenderState elementState, CallbackInfo ci) {
        boolean elementIsStreamproof = Streamproof.renderStates.contains(elementState);

        if (elementIsStreamproof != this.streamproof$currentBatchIsStreamproof) {
            this.streamproof$currentBatchIsStreamproof = elementIsStreamproof;
            this.previousDraw = null;
        }
    }

    @WrapOperation(method = "addElementToMesh", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean tagDraw(List<GuiRenderer.Draw> instance, @Coerce Object e, Operation<Boolean> original) {
        boolean result = original.call(instance, e);
        if (instance == this.draws && e instanceof GuiRenderer.Draw draw && this.streamproof$currentBatchIsStreamproof) {
            this.streamproof$drawsToCapture.add(draw);
        }

        return result;
    }

    @WrapOperation(method = "executeDrawRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;executeDraw(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;)V"))
    private void applyStreamproofPass(GuiRenderer instance, GuiRenderer.Draw draw, RenderPass renderPass, Operation<Void> original) {
        boolean isStreamproof = this.streamproof$drawsToCapture.contains(draw);
        if (isStreamproof) StreamproofAPI.start(RenderTarget.Gui);
        original.call(instance, draw, renderPass);
        if (isStreamproof) StreamproofAPI.stop(RenderTarget.Gui);
    }
}