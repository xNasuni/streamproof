package win.transgirls.streamproof.mixin.impl.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.api.types.RenderTarget;
import win.transgirls.streamproof.tools.StreamproofImpl;

import java.util.List;
import java.util.Map;

@Mixin(CustomFeatureRenderer.class)
public class CustomCommandMixin {
    @WrapOperation(method = "renderSolid", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector$CustomGeometryRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"))
    private void wrapCustomRenderSolid(SubmitNodeCollector.CustomGeometryRenderer instance, PoseStack.Pose matrixEntry, VertexConsumer vertexConsumer, Operation<Void> original, @Local(argsOnly = true) MultiBufferSource.BufferSource bufferSource, @Local(name = "entry") Map.Entry<RenderType, List<SubmitNodeStorage.CustomGeometrySubmit>> entry, @Local(name = "customGeometrySubmit") SubmitNodeStorage.CustomGeometrySubmit customGeometrySubmit) {
        boolean isStreamproof = Streamproof.renderCommands.contains(customGeometrySubmit);

        if (isStreamproof) {
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.forceDraw(bufferSource);
            }
            StreamproofAPI.start(RenderTarget.World);
        }
        original.call(instance, matrixEntry, bufferSource.getBuffer(entry.getKey()));
        if (isStreamproof) {
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.forceDraw(bufferSource);
            }
            StreamproofAPI.stop(RenderTarget.World);
        }
    }

    @WrapOperation(method = "renderTranslucent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector$CustomGeometryRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"))
    private void wrapCustomRenderTranslucent(SubmitNodeCollector.CustomGeometryRenderer instance, PoseStack.Pose matrixEntry, VertexConsumer vertexConsumer, Operation<Void> original, @Local(argsOnly = true) MultiBufferSource.BufferSource bufferSource, @Local(name = "entry") Map.Entry<RenderType, List<SubmitNodeStorage.CustomGeometrySubmit>> entry, @Local(name = "customGeometrySubmit") SubmitNodeStorage.CustomGeometrySubmit customGeometrySubmit) {
        boolean isStreamproof = Streamproof.renderCommands.contains(customGeometrySubmit);

        if (isStreamproof) {
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.forceDraw(bufferSource);
            }
            StreamproofAPI.start(RenderTarget.World);
        }
        original.call(instance, matrixEntry, bufferSource.getBuffer(entry.getKey()));
        if (isStreamproof) {
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.forceDraw(bufferSource);
            }
            StreamproofAPI.stop(RenderTarget.World);
        }
    }
}