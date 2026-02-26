package win.transgirls.streamproof.mixin.impl;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.TextureSetup;
import org.jspecify.annotations.Nullable;
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
    private final Set<GuiRenderer.Preparation> streamproof$preparationsToCapture = new HashSet<>();

    @Unique
    private boolean streamproof$currentBatchIsStreamproof = false;

    @Shadow
    @Final
    private List<GuiRenderer.Preparation> preparations;

    @Shadow
    @Final
    private List<GuiRenderer.Draw> draws;

    @Shadow
    @Nullable
    public BufferBuilder buffer;

    @Shadow
    public abstract void endBuffer(BufferBuilder builder, RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRect scissorArea);

    @Shadow
    public abstract BufferBuilder startBuffer(RenderPipeline pipeline);

    @Shadow
    @Nullable
    public RenderPipeline pipeline;

    @Shadow
    @Nullable
    public TextureSetup textureSetup;

    @Shadow
    @Nullable
    public ScreenRect scissorArea;

    @Shadow
    @Final
    private GuiRenderState state;

    @WrapMethod(method = "render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V")
    private void wrapRender(GpuBufferSlice fogBuffer, Operation<Void> original) {
        this.streamproof$preparationsToCapture.clear();
        this.streamproof$drawsToCapture.clear();
        this.streamproof$currentBatchIsStreamproof = false;

        original.call(fogBuffer);
    }

    // (xNasuni):
    // TextGuiElementRenderState transitions into many GlyphGuiElementRenderState for each glyph during preparation
    // it's very important to keep tagging them because the GlyphGuiElementRenderStates are not added during writing
    @WrapOperation(method = "prepareTextElements", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/state/GuiRenderState;forEachTextElement(Ljava/util/function/Consumer;)V"))
    private void proxyPrepareTextElements(GuiRenderState instance, Consumer<TextGuiElementRenderState> textElementStateConsumer, Operation<Void> original) {
        original.call(instance, (Consumer<TextGuiElementRenderState>) (state) -> {
            int sizeBefore = instance.currentLayer.preparedTextElementRenderStates != null
                    ? instance.currentLayer.preparedTextElementRenderStates.size()
                    : 0;

            textElementStateConsumer.accept(state);

            List<SimpleGuiElementRenderState> states = instance.currentLayer.preparedTextElementRenderStates;
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
    // handle more than one anyway just because it is possible this changes in the future
    @WrapMethod(method = "prepareItem")
    private void wrapPrepareItem(ItemGuiElementRenderState state, float u, float v, int pixelsPerItem, int itemAtlasSideLength, Operation<Void> original) {
        GuiRenderState.Layer currentLayer = this.state.currentLayer;

        int sizeBefore = currentLayer.simpleElementRenderStates != null
                ? currentLayer.simpleElementRenderStates.size()
                : 0;

        original.call(state, u, v, pixelsPerItem, itemAtlasSideLength);

        List<SimpleGuiElementRenderState> states = currentLayer.simpleElementRenderStates;
        int sizeAfter = states != null ? states.size() : 0;

        if (sizeAfter > sizeBefore && Streamproof.renderStates.contains(state)) {
            for (int i = sizeBefore; i < sizeAfter; i++) {
                Streamproof.renderStates.add(states.get(i));
            }
        }
    }

    @WrapOperation(method = "prepareSpecialElement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/SpecialGuiElementRenderer;render(Lnet/minecraft/client/gui/render/state/special/SpecialGuiElementRenderState;Lnet/minecraft/client/gui/render/state/GuiRenderState;I)V"))
    private <T extends SpecialGuiElementRenderState> void wrapPrepareSpecialElement(SpecialGuiElementRenderer<T> instance, T elementState, GuiRenderState state, int windowScaleFactor, Operation<Void> original) {
        boolean isStreamproof = Streamproof.renderStates.contains(elementState);
        if (isStreamproof) {
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.writing = true;
            }
        }
        original.call(instance, elementState, state, windowScaleFactor);
        if (isStreamproof) {
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.writing = false;
            }
        }
    }

    @Inject(method = "prepareSimpleElement", at = @At("HEAD"))
    private void splitStreamproofBatches(SimpleGuiElementRenderState state, CallbackInfo ci) {
        boolean elementIsStreamproof = Streamproof.renderStates.contains(state);

        if (elementIsStreamproof != this.streamproof$currentBatchIsStreamproof) {
            if (this.buffer != null) {
                this.endBuffer(this.buffer, this.pipeline, this.textureSetup, this.scissorArea);
            }

            this.streamproof$currentBatchIsStreamproof = elementIsStreamproof;

            this.buffer = this.startBuffer(state.pipeline());
            this.pipeline = state.pipeline();
            this.textureSetup = state.textureSetup();
            this.scissorArea = state.scissorArea();
        }
    }

    @WrapOperation(method = "endBuffer", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean tagFinishedPreparation(List<GuiRenderer.Preparation> instance, @Coerce Object e, Operation<Boolean> original) {
        boolean result = original.call(instance, e);
        if (instance == this.preparations && e instanceof GuiRenderer.Preparation preparation) {
            if (this.streamproof$currentBatchIsStreamproof) {
                this.streamproof$preparationsToCapture.add(preparation);
            }
        }

        return result;
    }

    @WrapOperation(method = "finishPreparation", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean carryTagToDraw(List<GuiRenderer.Draw> instance, @Coerce Object e, Operation<Boolean> original, @Local GuiRenderer.Preparation preparation) {
        boolean result = original.call(instance, e);
        if (instance == this.draws && e instanceof GuiRenderer.Draw draw) {
            if (this.streamproof$preparationsToCapture.contains(preparation)) {
                this.streamproof$drawsToCapture.add(draw);
            }
        }

        return result;
    }

    @WrapOperation(method = "render(Ljava/util/function/Supplier;Lnet/minecraft/client/gl/Framebuffer;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V"))
    private void applyStreamproofPass(GuiRenderer instance, GuiRenderer.Draw draw, RenderPass pass, GpuBuffer indexBuffer, VertexFormat.IndexType indexType, Operation<Void> original) {
        boolean isStreamproof = this.streamproof$drawsToCapture.contains(draw);
        if (isStreamproof) StreamproofAPI.start(RenderTarget.Gui);
        original.call(instance, draw, pass, indexBuffer, indexType);
        if (isStreamproof) StreamproofAPI.stop(RenderTarget.Gui);
    }
}