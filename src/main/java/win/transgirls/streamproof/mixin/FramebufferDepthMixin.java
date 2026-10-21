package win.transgirls.streamproof.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.systems.gl.GL;
import win.transgirls.streamproof.tools.StreamproofImpl;

@Mixin(GameRenderer.class)
public class FramebufferDepthMixin {
    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearDepthTexture(Lcom/mojang/blaze3d/textures/GpuTexture;D)V"))
    private void wrapClearDepthBuffer(CommandEncoder encoder, GpuTexture depthTexture, double clearDepth, Operation<Void> original) {
        if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl && impl.worldFb != null && impl.worldFb.depthTexture != 0) {
            if (depthTexture instanceof GlTexture glDepthTexture) {
                GL.copyDepth(glDepthTexture.glId(), impl.worldFb.depthTexture, glDepthTexture.getWidth(0), glDepthTexture.getHeight(0));
            }
        }

        original.call(encoder, depthTexture, clearDepth);
    }
}