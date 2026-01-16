package win.transgirls.streamproof.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;

@Mixin(Minecraft.class)
public class SwapMixin {
    @Inject(method = "runTick", at = @At("TAIL"))
    private void injectSwap(boolean bl, CallbackInfo ci) {
        Streamproof.afterRendering = false;
    }
}