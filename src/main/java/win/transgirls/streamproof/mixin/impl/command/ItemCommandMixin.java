package win.transgirls.streamproof.mixin.impl.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.api.types.RenderTarget;

@Mixin(ItemFeatureRenderer.class)
public class ItemCommandMixin {
    @WrapOperation(method = "renderSolid", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer;renderItem(Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/OutlineBufferSource;Lnet/minecraft/client/renderer/SubmitNodeStorage$ItemSubmit;)V"))
    private void wrapItemRenderSolid(ItemFeatureRenderer instance, MultiBufferSource.BufferSource bufferSource, OutlineBufferSource outlineBufferSource, SubmitNodeStorage.ItemSubmit submit, Operation<Void> original, @Local(name = "submit") SubmitNodeStorage.ItemSubmit itemSubmit) {
        boolean isStreamproof = Streamproof.renderCommands.contains(itemSubmit);

        if (isStreamproof) StreamproofAPI.start(RenderTarget.Gui);
        original.call(instance, bufferSource, outlineBufferSource, submit);
        if (isStreamproof) StreamproofAPI.stop(RenderTarget.Gui);
    }

    @WrapOperation(method = "renderTranslucent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer;renderItem(Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/OutlineBufferSource;Lnet/minecraft/client/renderer/SubmitNodeStorage$ItemSubmit;)V"))
    private void wrapItemRenderTranslucent(ItemFeatureRenderer instance, MultiBufferSource.BufferSource bufferSource, OutlineBufferSource outlineBufferSource, SubmitNodeStorage.ItemSubmit submit, Operation<Void> original, @Local(name = "submit") SubmitNodeStorage.ItemSubmit itemSubmit) {
        boolean isStreamproof = Streamproof.renderCommands.contains(itemSubmit);

        if (isStreamproof) StreamproofAPI.start(RenderTarget.Gui);
        original.call(instance, bufferSource, outlineBufferSource, submit);
        if (isStreamproof) StreamproofAPI.stop(RenderTarget.Gui);
    }
}