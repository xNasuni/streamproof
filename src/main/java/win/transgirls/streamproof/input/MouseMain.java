package win.transgirls.streamproof.input;

import net.minecraft.client.input.SystemKeycodes;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import win.transgirls.streamproof.Streamproof;

import java.util.HashMap;
import java.util.Map;

public class MouseMain {
    public static boolean passMouseInput = true;
    public static boolean glfwSetInputModeBypass = false;
    public static HashMap<Integer, Integer> glfwInputModeSave = new HashMap<>();

    public static void lockCursor() {
        if (Streamproof.client.currentScreen != null) {
            return;
        }

        glfwSetInputModeBypass = true;
        for (Map.Entry<Integer, Integer> entry : MouseMain.glfwInputModeSave.entrySet()) {
            GLFW.glfwSetInputMode(Streamproof.window.getHandle(), entry.getKey(), entry.getValue());
        }

        if (SystemKeycodes.UPDATE_PRESSED_STATE_ON_MOUSE_GRAB) {
            KeyBinding.updatePressedStates();
        }

        Streamproof.client.mouse.cursorLocked = true;
        Streamproof.client.mouse.x = (double) Streamproof.window.getWidth() / 2;
        Streamproof.client.mouse.y = (double) Streamproof.window.getHeight() / 2;
        GLFW.glfwSetCursorPos(Streamproof.window.getHandle(), Streamproof.client.mouse.x, Streamproof.client.mouse.y);
        GLFW.glfwSetInputMode(Streamproof.window.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        glfwSetInputModeBypass = false;
    }

    public static void unlockCursor() {
        glfwSetInputModeBypass = true;
        Streamproof.client.mouse.cursorLocked = false;
        GLFW.glfwSetInputMode(Streamproof.window.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        glfwSetInputModeBypass = false;
    }
}