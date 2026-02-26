package win.transgirls.streamproof.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.tools.StreamproofImpl;

@Mixin(MinecraftClient.class)
public class FramebufferRuntimeMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void headTick(boolean bl, CallbackInfo ci) {
        Streamproof.renderStates.clear();
        if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
            if (impl.worldFb != null && impl.worldFb.dirty) {
                impl.worldFb.clear();
            }

            if (impl.guiFb != null && impl.guiFb.dirty) {
                impl.guiFb.clear();
            }
        }
    }

    @Inject(method = "onResolutionChanged", at = @At("TAIL"))
    private void tailResized(CallbackInfo ci) {
        if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
            if (impl.worldFb != null) {
                impl.worldFb.fit();
            }

            if (impl.guiFb != null) {
                impl.guiFb.fit();
            }
        }
    }
}