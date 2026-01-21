package win.transgirls.streamproof.mixin;

import org.lwjgl.glfw.GLFW;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.input.MouseMain;
import win.transgirls.streamproof.visuals.Interface;

import static org.lwjgl.glfw.GLFW.*;

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
