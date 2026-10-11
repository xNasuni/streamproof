package win.transgirls.streamproof.input;

import org.lwjgl.glfw.GLFW;
import win.transgirls.streamproof.Streamproof;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.InputQuirks;

import static win.transgirls.streamproof.Streamproof.mc;

public class MouseMain {
    public static boolean passMouseInput = true;
    public static boolean glfwSetInputModeBypass = false;
    public static HashMap<Integer, Integer> glfwInputModeSave = new HashMap<>();

    public static void lockCursor() {
        if (mc.screen != null) {
            return;
        }

        glfwSetInputModeBypass = true;
        for (Map.Entry<Integer, Integer> entry : MouseMain.glfwInputModeSave.entrySet()) {
            GLFW.glfwSetInputMode(Streamproof.window.handle(), entry.getKey(), entry.getValue());
        }

        if (InputQuirks.RESTORE_KEY_STATE_AFTER_MOUSE_GRAB) {
            KeyMapping.setAll();
        }

        mc.mouseHandler.mouseGrabbed = true;
        mc.mouseHandler.xpos = (double) Streamproof.window.getScreenWidth() / 2;
        mc.mouseHandler.ypos = (double) Streamproof.window.getScreenHeight() / 2;
        GLFW.glfwSetCursorPos(Streamproof.window.handle(), mc.mouseHandler.xpos, mc.mouseHandler.ypos);
        GLFW.glfwSetInputMode(Streamproof.window.handle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        glfwSetInputModeBypass = false;
    }

    public static void unlockCursor() {
        glfwSetInputModeBypass = true;
        mc.mouseHandler.mouseGrabbed = false;
        GLFW.glfwSetInputMode(Streamproof.window.handle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        glfwSetInputModeBypass = false;
    }
}