package win.transgirls.streamproof.mixin.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;

import java.util.List;

@Mixin(SubmitNodeCollection.class)
public class RenderQueueMixin {
    @Shadow
    @Final
    private List<SubmitNodeStorage.TextSubmit> textSubmits;

    @Shadow
    @Final
    private List<SubmitNodeStorage.ItemSubmit> itemSubmits;

    @Shadow
    @Final
    private CustomFeatureRenderer.Storage customGeometrySubmits;

    @Inject(method = "submitText", at = @At("TAIL"))
    private void wrapSubmitText(PoseStack poseStack, float x, float y, FormattedCharSequence text, boolean dropShadow, Font.DisplayMode displayMode, int lightCoords, int color, int backgroundColor, int outlineColor, CallbackInfo ci) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderCommands.add(this.textSubmits.getLast());
        }
    }

    @Inject(method = "submitItem", at = @At("TAIL"))
    private void wrapSubmitItem(PoseStack poseStack, ItemDisplayContext displayContext, int lightCoords, int overlayCoords, int outlineColor, int[] tintLayers, List<BakedQuad> quads, ItemStackRenderState.FoilType foilType, CallbackInfo ci) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderCommands.add(this.itemSubmits.getLast());
        }
    }

    @Inject(method = "submitParticleGroup(Lnet/minecraft/client/renderer/SubmitNodeCollector$ParticleGroupRenderer;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void wrapSubmitLayeredCustom(SubmitNodeCollector.ParticleGroupRenderer particleGroupRenderer, CallbackInfo ci) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderCommands.add(particleGroupRenderer);
        }
    }

    @Inject(method = "submitCustomGeometry(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;Lnet/minecraft/client/renderer/SubmitNodeCollector$CustomGeometryRenderer;)V", at = @At(value = "TAIL"))
    private void wrapSubmitCustom(PoseStack poseStack, RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer, CallbackInfo ci) {
        if (StreamproofAPI.isWriting()) {
            try {
                if (!renderType.hasBlending()) {
                    Streamproof.renderCommands.add(this.customGeometrySubmits.solidCustomGeometrySubmits.get(renderType).getLast());
                } else {
                    Streamproof.renderCommands.add(this.customGeometrySubmits.translucentCustomGeometrySubmits.get(renderType).getLast());
                }
            } catch (Throwable ignored) {
            }
        }
    }
}