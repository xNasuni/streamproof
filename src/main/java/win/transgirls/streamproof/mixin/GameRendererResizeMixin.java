package win.transgirls.streamproof.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.tools.StreamproofImpl;

@Mixin(GameRenderer.class)
public class GameRendererResizeMixin {
    @Inject(method = "resize", at = @At("TAIL"))
    private void tailResize(int width, int height, CallbackInfo ci) {
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
