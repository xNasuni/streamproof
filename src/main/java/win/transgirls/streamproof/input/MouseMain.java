package win.transgirls.streamproof.input;

import net.minecraft.client.input.SystemKeycodes;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import win.transgirls.streamproof.Streamproof;

import java.util.HashMap;
import java.util.Map;

import static win.transgirls.streamproof.Streamproof.mc;

public class MouseMain {
    public static boolean passMouseInput = true;
    public static boolean glfwSetInputModeBypass = false;
    public static HashMap<Integer, Integer> glfwInputModeSave = new HashMap<>();

    public static void lockCursor() {
        if (mc.currentScreen != null) {
            return;
        }

        glfwSetInputModeBypass = true;
        for (Map.Entry<Integer, Integer> entry : MouseMain.glfwInputModeSave.entrySet()) {
            GLFW.glfwSetInputMode(Streamproof.window.getHandle(), entry.getKey(), entry.getValue());
        }

        if (SystemKeycodes.UPDATE_PRESSED_STATE_ON_MOUSE_GRAB) {
            KeyBinding.updatePressedStates();
        }

        mc.mouse.cursorLocked = true;
        mc.mouse.x = (double) Streamproof.window.getWidth() / 2;
        mc.mouse.y = (double) Streamproof.window.getHeight() / 2;
        GLFW.glfwSetCursorPos(Streamproof.window.getHandle(), mc.mouse.x, mc.mouse.y);
        GLFW.glfwSetInputMode(Streamproof.window.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        glfwSetInputModeBypass = false;
    }

    public static void unlockCursor() {
        glfwSetInputModeBypass = true;
        mc.mouse.cursorLocked = false;
        GLFW.glfwSetInputMode(Streamproof.window.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        glfwSetInputModeBypass = false;
    }
}