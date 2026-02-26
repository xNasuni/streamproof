package win.transgirls.streamproof.mixin.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.CustomCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;

import java.util.List;

@Mixin(BatchingRenderCommandQueue.class)
public class RenderQueueMixin {
    @Shadow
    @Final
    private List<OrderedRenderCommandQueueImpl.TextCommand> textCommands;

    @Shadow
    @Final
    private List<OrderedRenderCommandQueueImpl.ItemCommand> itemCommands;

    @Shadow
    @Final
    private CustomCommandRenderer.Commands customCommands;

    @Inject(method = "submitText", at = @At("TAIL"))
    private void wrapSubmitText(MatrixStack matrices, float x, float y, OrderedText text, boolean dropShadow, TextRenderer.TextLayerType layerType, int light, int color, int backgroundColor, int outlineColor, CallbackInfo ci) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderCommands.add(this.textCommands.getLast());
        }
    }

    @Inject(method = "submitItem", at = @At("TAIL"))
    private void wrapSubmitItem(MatrixStack matrices, ItemDisplayContext displayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, ItemRenderState.Glint glintType, CallbackInfo ci) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderCommands.add(this.itemCommands.getLast());
        }
    }

    @Inject(method = "submitCustom(Lnet/minecraft/client/render/command/OrderedRenderCommandQueue$LayeredCustom;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void wrapSubmitLayeredCustom(OrderedRenderCommandQueue.LayeredCustom customRenderer, CallbackInfo ci) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderCommands.add(customRenderer);
        }
    }

    @Inject(method = "submitCustom(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue$Custom;)V", at = @At(value = "TAIL"))
    private void wrapSubmitCustom(MatrixStack matrices, RenderLayer renderLayer, OrderedRenderCommandQueue.Custom customRenderer, CallbackInfo ci) {
        if (StreamproofAPI.isWriting()) {
            try {
                Streamproof.renderCommands.add(this.customCommands.customCommands.get(renderLayer).getLast());
            } catch (Throwable ignored) {
            }
        }
    }
}