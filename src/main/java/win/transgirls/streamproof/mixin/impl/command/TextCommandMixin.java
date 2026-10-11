package win.transgirls.streamproof.mixin.impl.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.TextFeatureRenderer;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.api.types.RenderTarget;

@Mixin(TextFeatureRenderer.class)
public class TextCommandMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V"))
    private void wrapTextRender(Font instance, FormattedCharSequence text, float x, float y, int color, boolean shadow, Matrix4f matrix, MultiBufferSource vertexConsumers, Font.DisplayMode layerType, int backgroundColor, int light, Operation<Void> original, @Local SubmitNodeStorage.TextSubmit command) {
        boolean isStreamproof = Streamproof.renderCommands.contains(command);

        if (isStreamproof) StreamproofAPI.start(RenderTarget.Gui);
        original.call(instance, text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light);
        if (isStreamproof) StreamproofAPI.stop(RenderTarget.Gui);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch8xOutline(Lnet/minecraft/util/FormattedCharSequence;FFIILorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void wrapTextOutlineRender(Font instance, FormattedCharSequence text, float x, float y, int color, int outlineColor, Matrix4f matrix, MultiBufferSource vertexConsumers, int light, Operation<Void> original, @Local SubmitNodeStorage.TextSubmit command) {
        boolean isStreamproof = Streamproof.renderCommands.contains(command);

        if (isStreamproof) StreamproofAPI.start(RenderTarget.Gui);
        original.call(instance, text, x, y, color, outlineColor, matrix, vertexConsumers, light);
        if (isStreamproof) StreamproofAPI.stop(RenderTarget.Gui);
    }
}