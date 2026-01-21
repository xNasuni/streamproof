package win.transgirls.streamproof.visuals;

import imgui.ImGui;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.input.KeyboardMain;
import win.transgirls.streamproof.input.MouseMain;
import win.transgirls.streamproof.visuals.panels.Backdrop;
import win.transgirls.streamproof.visuals.panels.Dashboard;
import win.transgirls.streamproof.visuals.panels.PanelInterface;
import win.transgirls.streamproof.visuals.panels.PanelType;

import java.util.ArrayList;
import java.util.Map;

import static win.transgirls.streamproof.Streamproof.LOGGER;

public class Interface {
    public static final ArrayList<PanelInterface> panels = new ArrayList<>();
    public static boolean visible = false;
    protected static boolean initialized = false;
    private static boolean lastVisible = false;

    private static void renderPanels(PanelType type) {
        for (PanelInterface panel : panels) {
            if (panel.visible() && panel.getType() == type) {
                LOGGER.debug("Rendering panel: {} (type: {})", panel.getClass().getSimpleName(), type);
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
                MouseMain.unlockCursor();
            } else {
                MouseMain.lockCursor();
            }
            lastVisible = visible;
        }

        renderPanels(PanelType.Overlay);

        if (!visible) {
            return;
        }

        renderPanels(PanelType.Menu);
    }
}