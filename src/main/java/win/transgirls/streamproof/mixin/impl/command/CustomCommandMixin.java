package win.transgirls.streamproof.mixin.impl.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.CustomCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.api.types.RenderTarget;
import win.transgirls.streamproof.tools.StreamproofImpl;

import java.util.List;
import java.util.Map;

@Mixin(CustomCommandRenderer.class)
public class CustomCommandMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue$Custom;render(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/VertexConsumer;)V"))
    private void wrapCustomRender(OrderedRenderCommandQueue.Custom instance, MatrixStack.Entry matrixEntry, VertexConsumer vertexConsumer, Operation<Void> original, @Local(argsOnly = true) VertexConsumerProvider.Immediate immediate, @Local Map.Entry<RenderLayer, List<OrderedRenderCommandQueueImpl.CustomCommand>> entry, @Local OrderedRenderCommandQueueImpl.CustomCommand command) {
        boolean isStreamproof = Streamproof.renderCommands.contains(command);
        VertexConsumer consumers = isStreamproof ? immediate.getBuffer(entry.getKey()) : vertexConsumer;

        if (isStreamproof) {
            StreamproofAPI.start(RenderTarget.World);
        }
        original.call(instance, matrixEntry, consumers);
        if (isStreamproof) {
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.forceDraw(immediate);
            }
            StreamproofAPI.stop(RenderTarget.World);
        }
    }
}