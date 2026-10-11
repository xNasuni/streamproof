package win.transgirls.streamproof.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.tools.StreamproofImpl;

@Mixin(Minecraft.class)
public class FramebufferRuntimeMixin {
    @Inject(method = "runTick", at = @At("HEAD"))
    private void headTick(boolean advanceGameTime, CallbackInfo ci) {
        Streamproof.renderStates.clear();
        Streamproof.renderCommands.clear();
        if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
            if (impl.worldFb != null && impl.worldFb.dirty) {
                impl.worldFb.clear();
            }

            if (impl.guiFb != null && impl.guiFb.dirty) {
                impl.guiFb.clear();
            }
        }
    }
}