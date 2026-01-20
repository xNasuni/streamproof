package win.transgirls.streamproof.systems.gl;

import org.lwjgl.glfw.GLFW;

public class GlfwHelper {
    public static boolean glfwSetInputModeBypass = false;

    public static void setInputMode(long handle, int mode, int value) {
        glfwSetInputModeBypass = true;
        GLFW.glfwSetInputMode(handle, mode, value);
        glfwSetInputModeBypass = false;
    }

    public static int getInputMode(long handle, int mode) {
        return GLFW.glfwGetInputMode(handle, mode);
    }
}