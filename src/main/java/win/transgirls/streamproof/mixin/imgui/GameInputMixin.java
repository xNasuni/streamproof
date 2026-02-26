package win.transgirls.streamproof.mixin.imgui;


import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import win.transgirls.streamproof.input.KeyboardMain;
import win.transgirls.streamproof.input.MouseMain;

@Mixin(value = InputUtil.class, priority = 1001)
public class GameInputMixin {
    @Unique
    private static final int RELEASING = 0;
    @Unique
    private static final int PRESSING = 1;
    @Unique
    private static final int HOLDING = 2;

    @WrapMethod(method = "setKeyboardCallbacks")
    private static void wrapKeyboard(Window window, GLFWKeyCallbackI keyCallback, GLFWCharModsCallbackI charModsCallback, Operation<Void> original) {
        GLFW.glfwSetKeyCallback(window.getHandle(), (_handle, key, scanCode, action, mods) -> {
            if (action == PRESSING) {
                KeyboardMain.keyDown(key);
            }
            if (action == RELEASING) {
                KeyboardMain.keyUp(key);
            }

            if (KeyboardMain.passKeyboardInput) {
                keyCallback.invoke(_handle, key, scanCode, action, mods);
            }
        });
        GLFW.glfwSetCharModsCallback(window.getHandle(), (_handle, codepoint, mods) -> {
            if (KeyboardMain.passKeyboardInput) {
                charModsCallback.invoke(_handle, codepoint, mods);
            }
        });
    }

    @WrapMethod(method = "setMouseCallbacks")
    private static void wrapMouse(Window window, GLFWCursorPosCallbackI cursorPosCallback, GLFWMouseButtonCallbackI mouseButtonCallback, GLFWScrollCallbackI scrollCallback, GLFWDropCallbackI dropCallback, Operation<Void> original) {
        GLFW.glfwSetCursorPosCallback(window.getHandle(), (_handle, x, y) -> {
            if (MouseMain.passMouseInput) {
                cursorPosCallback.invoke(_handle, x, y);
            }
        });
        GLFW.glfwSetMouseButtonCallback(window.getHandle(), (_handle, button, action, modifiers) -> {
            if (MouseMain.passMouseInput) {
                mouseButtonCallback.invoke(_handle, button, action, modifiers);
            }
        });
        GLFW.glfwSetScrollCallback(window.getHandle(), (_handle, offsetX, offsetY) -> {
            if (MouseMain.passMouseInput) {
                scrollCallback.invoke(_handle, offsetX, offsetY);
            }
        });
        GLFW.glfwSetDropCallback(window.getHandle(), (_handle, count, names) -> {
            if (MouseMain.passMouseInput) {
                dropCallback.invoke(_handle, count, names);
            }
        });
    }
}