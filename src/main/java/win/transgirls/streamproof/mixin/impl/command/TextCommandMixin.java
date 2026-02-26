package win.transgirls.streamproof.mixin.impl.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.command.TextCommandRenderer;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.api.types.RenderTarget;

@Mixin(TextCommandRenderer.class)
public class TextCommandMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)V"))
    private void wrapTextRender(TextRenderer instance, OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextRenderer.TextLayerType layerType, int backgroundColor, int light, Operation<Void> original, @Local OrderedRenderCommandQueueImpl.TextCommand command) {
        boolean isStreamproof = Streamproof.renderCommands.contains(command);

        if (isStreamproof) StreamproofAPI.start(RenderTarget.Gui);
        original.call(instance, text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light);
        if (isStreamproof) StreamproofAPI.stop(RenderTarget.Gui);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithOutline(Lnet/minecraft/text/OrderedText;FFIILorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void wrapTextOutlineRender(TextRenderer instance, OrderedText text, float x, float y, int color, int outlineColor, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original, @Local OrderedRenderCommandQueueImpl.TextCommand command) {
        boolean isStreamproof = Streamproof.renderCommands.contains(command);

        if (isStreamproof) StreamproofAPI.start(RenderTarget.Gui);
        original.call(instance, text, x, y, color, outlineColor, matrix, vertexConsumers, light);
        if (isStreamproof) StreamproofAPI.stop(RenderTarget.Gui);
    }
}