package win.transgirls.streamproof.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.imgui.ImGuiImplementation;

@Mixin(MinecraftClient.class)
public class GameTickMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void headTick(boolean bl, CallbackInfo ci) {
        Streamproof.renderQueue.clear();
        Streamproof.renderGuiSecrets = null;
        Streamproof.renderWorldSecrets = null;
    }
}