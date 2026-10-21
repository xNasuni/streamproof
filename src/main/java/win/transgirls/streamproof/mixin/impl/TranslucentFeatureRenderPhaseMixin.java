package win.transgirls.streamproof.mixin.impl;

import net.minecraft.client.renderer.feature.phase.TranslucentFeatureRenderPhase;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.tools.StreamproofImpl;

@Mixin(TranslucentFeatureRenderPhase.class)
public class TranslucentFeatureRenderPhaseMixin {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/feature/submit/TranslucentSubmit;)V", at = @At("HEAD"))
    private void tagSubmit(TranslucentSubmit submit, CallbackInfo ci) {
        if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
            impl.tagSubmit(submit);
        }
    }
}
