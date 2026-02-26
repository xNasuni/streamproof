package win.transgirls.streamproof.mixin.imgui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.imgui.ImGuiImplementation;

@Mixin(RenderSystem.class)
public class ImGuiDrawMixin {
    @WrapMethod(method = "flipFrame")
    private static void wrapFlip(Window window, TracyFrameCapturer capturer, Operation<Void> original) {
        if (!Streamproof.obsWrapper.hooked || !Streamproof.settings.isStreamproof("STREAMPROOF_IMGUI_WINDOW")) {
            if (ImGuiImplementation.initialized) {
                ImGuiImplementation.draw();
            }
        }

        original.call(window, capturer);
    }
}