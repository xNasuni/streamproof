package win.transgirls.streamproof.mixin.imgui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.imgui.ImGuiImplementation;

@Mixin(MinecraftClient.class)
public class GameStartMixin {
    @Shadow
    @Final
    private Window window;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(RunArgs gameConfig, CallbackInfo ci) {
        Streamproof.window = window;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void lateInit(RunArgs gameConfig, CallbackInfo ci) {
        Streamproof.lateInit();
    }
}