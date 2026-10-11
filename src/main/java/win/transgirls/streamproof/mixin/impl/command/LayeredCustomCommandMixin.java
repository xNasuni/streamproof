package win.transgirls.streamproof.mixin.impl.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ParticleFeatureRenderer;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.api.types.RenderTarget;

@Mixin(ParticleFeatureRenderer.class)
public class LayeredCustomCommandMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector$ParticleGroupRenderer;render(Lnet/minecraft/client/renderer/state/level/QuadParticleRenderState$PreparedBuffers;Lnet/minecraft/client/renderer/feature/ParticleFeatureRenderer$ParticleBufferCache;Lcom/mojang/blaze3d/systems/RenderPass;Lnet/minecraft/client/renderer/texture/TextureManager;)V"))
    private void wrapLayeredCustomRender(SubmitNodeCollector.ParticleGroupRenderer instance, net.minecraft.client.renderer.state.level.QuadParticleRenderState.PreparedBuffers preparedBuffers, ParticleFeatureRenderer.ParticleBufferCache particleBufferCache, RenderPass renderPass, TextureManager textureManager, Operation<Void> original, @Local(name = "prepared") QuadParticleRenderState.PreparedBuffers prepared) {
        boolean isStreamproof = Streamproof.renderCommands.contains(prepared);

        if (isStreamproof) StreamproofAPI.start(RenderTarget.Gui);
        original.call(instance, preparedBuffers, particleBufferCache, renderPass, textureManager);
        if (isStreamproof) StreamproofAPI.stop(RenderTarget.Gui);
    }
}