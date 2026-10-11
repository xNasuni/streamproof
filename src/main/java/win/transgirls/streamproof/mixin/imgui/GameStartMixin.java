package win.transgirls.streamproof.mixin.imgui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.imgui.ImGuiImplementation;

@Mixin(Minecraft.class)
public class GameStartMixin {
    @Shadow
    @Final
    private Window window;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(GameConfig gameConfig, CallbackInfo ci) {
        Streamproof.window = window;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void lateInit(GameConfig gameConfig, CallbackInfo ci) {
        Streamproof.lateInit();
    }
}