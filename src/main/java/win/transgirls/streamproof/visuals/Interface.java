package win.transgirls.streamproof.visuals;

import imgui.ImGui;
import org.lwjgl.glfw.GLFW;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.input.KeyboardMain;
import win.transgirls.streamproof.input.MouseMain;
import win.transgirls.streamproof.systems.gl.GlfwHelper;
import win.transgirls.streamproof.visuals.panels.Backdrop;
import win.transgirls.streamproof.visuals.panels.Dashboard;
import win.transgirls.streamproof.visuals.panels.PanelInterface;
import win.transgirls.streamproof.visuals.panels.PanelType;

import java.util.ArrayList;
import java.util.List;

public class Interface {
    public static final ArrayList<PanelInterface> panels = new ArrayList<>();
    public static boolean visible = false;
    public static int unmaskedCursorMode = GLFW.GLFW_CURSOR_NORMAL;
    protected static boolean initialized = false;
    private static boolean lastVisible = false;

    private static void renderPanels(PanelType type) {
        for (PanelInterface panel : panels) {
            if (panel.visible() && panel.getType() == type) {
                Streamproof.LOGGER.debug("Rendering panel: {} (type: {})", panel.getClass().getSimpleName(), type);
                panel.render(ImGui.getIO());
            }
        }
    }

    public static void init() {
        if (initialized) {
            throw new IllegalStateException("Interface.init() called when already initialized.");
        }

        panels.add(new Backdrop());
        panels.add(new Dashboard());

        for (PanelInterface panel : panels) {
            panel.setup();
        }

        initialized = true;
    }

    public static void render(long handle) {
        if (!initialized) {
            return;
        }

        KeyboardMain.passKeyboardInput = !visible;
        MouseMain.passMouseInput = !visible;

        if (visible != lastVisible) {
            if (visible) {
                unmaskedCursorMode = GlfwHelper.getInputMode(handle, GLFW.GLFW_CURSOR);
                GlfwHelper.setInputMode(handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            } else {
                GlfwHelper.setInputMode(handle, GLFW.GLFW_CURSOR, unmaskedCursorMode);
            }
            lastVisible = visible;
        }

        renderPanels(PanelType.Overlay);

        if (!visible) {
            return;
        }

//        GlfwHelper.setInputMode(handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        renderPanels(PanelType.Menu);
    }
}