package win.transgirls.streamproof.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.opengl.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.tools.StreamproofImpl;

@Mixin(GlStateManager.class)
public class FramebufferBindMixin {
    @WrapMethod(method = "_glBindFramebuffer")
    private static void wrapBindFramebuffer(int target, int framebuffer, Operation<Void> original) {
        if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
            boolean streamproofFboBound = false;
            if (impl.worldFb != null && impl.worldFb.bound) {
                impl.worldFb.lastFbo = framebuffer;
                streamproofFboBound = true;
            }

            if (impl.guiFb != null && impl.guiFb.bound) {
                impl.guiFb.lastFbo = framebuffer;
                streamproofFboBound = true;
            }

            if (streamproofFboBound) {
                return;
            }
        }

        original.call(target, framebuffer);
    }
}