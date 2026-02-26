package win.transgirls.streamproof.mixin.imgui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.input.MouseMain;

@Mixin(GLFW.class)
public class GlfwInputMixin {
    @WrapMethod(method = "glfwSetInputMode", remap = false)
    private static void modeWrap(long window, int mode, int value, Operation<Void> original) {
        if (MouseMain.glfwSetInputModeBypass) {
            original.call(window, mode, value);
            return;
        }

        MouseMain.glfwInputModeSave.put(mode, value);
        original.call(window, mode, value);
    }
}