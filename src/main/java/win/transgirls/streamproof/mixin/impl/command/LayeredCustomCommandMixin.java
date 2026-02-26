package win.transgirls.streamproof.mixin.impl.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.render.command.LayeredCustomCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.texture.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.api.types.RenderTarget;

@Mixin(LayeredCustomCommandRenderer.class)
public class LayeredCustomCommandMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue$LayeredCustom;render(Lnet/minecraft/client/particle/BillboardParticleSubmittable$Buffers;Lnet/minecraft/client/render/command/LayeredCustomCommandRenderer$VerticesCache;Lcom/mojang/blaze3d/systems/RenderPass;Lnet/minecraft/client/texture/TextureManager;Z)V"))
    private void wrapLayeredCustomRender(OrderedRenderCommandQueue.LayeredCustom instance, BillboardParticleSubmittable.Buffers buffers, LayeredCustomCommandRenderer.VerticesCache verticesCache, RenderPass renderPass, TextureManager textureManager, boolean b, Operation<Void> original, @Local OrderedRenderCommandQueueImpl.LayeredCustom command) {
        boolean isStreamproof = Streamproof.renderCommands.contains(command);

        if (isStreamproof) StreamproofAPI.start(RenderTarget.Gui);
        original.call(instance, buffers, verticesCache, renderPass, textureManager, b);
        if (isStreamproof) StreamproofAPI.stop(RenderTarget.Gui);
    }
}