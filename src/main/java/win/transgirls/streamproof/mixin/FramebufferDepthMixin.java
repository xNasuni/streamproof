package win.transgirls.streamproof.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.GlTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.systems.gl.GL;
import win.transgirls.streamproof.tools.StreamproofImpl;

@Mixin(GameRenderer.class)
public class FramebufferDepthMixin {
    @WrapOperation(method = "renderWorld", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearDepthTexture(Lcom/mojang/blaze3d/textures/GpuTexture;D)V"))
    private void wrapClearDepthBuffer(CommandEncoder encoder, GpuTexture _depthTexture, double v, Operation<Void> original) {
        if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl && impl.worldFb != null && impl.worldFb.depthTexture != 0) {
            if (_depthTexture instanceof GlTexture depthTexture) {
                GL.copyDepth(depthTexture.getGlId(), impl.worldFb.depthTexture, depthTexture.getWidth(0), depthTexture.getHeight(0));
            }
        }

        original.call(encoder, _depthTexture, v);
    }
}