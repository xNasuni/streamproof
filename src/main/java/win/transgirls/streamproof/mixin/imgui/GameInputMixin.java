package win.transgirls.streamproof.mixin.imgui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import win.transgirls.streamproof.input.KeyboardMain;
import win.transgirls.streamproof.input.MouseMain;

@Mixin(value = InputConstants.class, priority = 1001)
public class GameInputMixin {
    @Unique
    private static final int RELEASING = 0;
    @Unique
    private static final int PRESSING = 1;
    @Unique
    private static final int HOLDING = 2;

    @WrapMethod(method = "setupKeyboardCallbacks")
    private static void wrapKeyboard(Window window, GLFWKeyCallbackI keyPressCallback, GLFWCharCallbackI charTypedCallback, GLFWPreeditCallbackI preeditCallback, GLFWIMEStatusCallbackI imeStatusCallback, Operation<Void> original) {
        GLFW.glfwSetKeyCallback(window.handle(), (_handle, key, scanCode, action, mods) -> {
            if (action == PRESSING) {
                KeyboardMain.keyDown(key);
            }
            if (action == RELEASING) {
                KeyboardMain.keyUp(key);
            }

            if (KeyboardMain.passKeyboardInput) {
                keyPressCallback.invoke(_handle, key, scanCode, action, mods);
            }
        });
        GLFW.glfwSetCharCallback(window.handle(), (_handle, codepoint) -> {
            if (KeyboardMain.passKeyboardInput) {
                charTypedCallback.invoke(_handle, codepoint);
            }
        });
        GLFW.glfwSetPreeditCallback(window.handle(), (_handle, preedit_count, preedit_string, block_count, block_sizes, focused_block, caret) -> {
            if (KeyboardMain.passKeyboardInput) {
                preeditCallback.invoke(_handle, preedit_count, preedit_string, block_count, block_sizes, focused_block, caret);
            }
        });

        GLFW.glfwSetIMEStatusCallback(window.handle(), (_handle) -> {
            if (KeyboardMain.passKeyboardInput) {
                imeStatusCallback.invoke(_handle);
            }
        });
    }

    @WrapMethod(method = "setupMouseCallbacks")
    private static void wrapMouse(Window window, GLFWCursorPosCallbackI cursorPosCallback, GLFWMouseButtonCallbackI mouseButtonCallback, GLFWScrollCallbackI scrollCallback, GLFWDropCallbackI dropCallback, Operation<Void> original) {
        GLFW.glfwSetCursorPosCallback(window.handle(), (_handle, x, y) -> {
            if (MouseMain.passMouseInput) {
                cursorPosCallback.invoke(_handle, x, y);
            }
        });
        GLFW.glfwSetMouseButtonCallback(window.handle(), (_handle, button, action, modifiers) -> {
            if (MouseMain.passMouseInput) {
                mouseButtonCallback.invoke(_handle, button, action, modifiers);
            }
        });
        GLFW.glfwSetScrollCallback(window.handle(), (_handle, offsetX, offsetY) -> {
            if (MouseMain.passMouseInput) {
                scrollCallback.invoke(_handle, offsetX, offsetY);
            }
        });
        GLFW.glfwSetDropCallback(window.handle(), (_handle, count, names) -> {
            if (MouseMain.passMouseInput) {
                dropCallback.invoke(_handle, count, names);
            }
        });
    }
}