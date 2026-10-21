package win.transgirls.streamproof.mixin.impl;

import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.tools.StreamproofImpl;

@Mixin(SimpleFeatureRenderPhase.class)
public class SimpleFeatureRenderPhaseMixin {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/feature/submit/SubmitNode;)V", at = @At("HEAD"))
    private void tagSubmit(SubmitNode submit, CallbackInfo ci) {
        if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
            impl.tagSubmit(submit);
        }
    }
}