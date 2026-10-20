package win.transgirls.streamproof.mixin.imgui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.GpuSurface;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.imgui.ImGuiImplementation;

@Mixin(Minecraft.class)
public class ImGuiDrawMixin {
    @WrapOperation(method = "renderFrame", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuSurface;present()V"))
    private void wrapPresent(GpuSurface instance, Operation<Void> original) {
        if (!Streamproof.swapBufferHook.hooked || !Streamproof.settings.isStreamproof("STREAMPROOF_IMGUI_WINDOW")) {
            if (ImGuiImplementation.initialized) {
                ImGuiImplementation.draw();
            }
        }

        original.call(instance);
    }
}