package win.transgirls.streamproof.imgui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Math;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.systems.gl.GL;
import win.transgirls.streamproof.visuals.Color;
import win.transgirls.streamproof.visuals.Interface;
import win.transgirls.streamproof.visuals.Style;

import static win.transgirls.streamproof.Streamproof.mc;

public class ImGuiImplementation {
    private final static ImGuiImplGlfw glfw = new ImGuiImplGlfw();
    private final static ImGuiImplGl3 gl3 = new ImGuiImplGl3();
    public static boolean initialized = false;
    private static long handle = 0;

    public static void create(final long handle) {
        ImGui.createContext();
        ImPlot.createContext();
        Style.setup();

        final ImGuiIO data = ImGui.getIO();
        data.setIniFilename(Streamproof.id + ".ini");
        data.setFontGlobalScale(1F);

        glfw.init(handle, true);

        GL.setDefaultPixelStore();

        gl3.init("#version 330");

        ImGuiImplementation.handle = handle;
        initialized = true;
    }

    public static void draw() {
        GL.saveRenderFlags();
        Color.tick += Math.PI_f / 512;

        ImGuiIO io = ImGui.getIO();
        if (mc != null && mc.getWindow() != null) {
            io.setDisplaySize(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
            io.setDisplayFramebufferScale(1.0f, 1.0f);
        }

        glfw.newFrame();
        ImGui.newFrame();

        Interface.render(handle);

        ImGui.render();

        GL.activeTexture(GL13C.GL_TEXTURE0);

        GL.bindSampler(0, 0);
        GL.enableBlend();
        GL.blendFunc(GL11C.GL_SRC_ALPHA, GL11C.GL_ONE_MINUS_SRC_ALPHA);
        GL.disableCull();
        GL.disableDepth();
        GL.disableScissor();

        GL.setDefaultPixelStore();

        gl3.renderDrawData(ImGui.getDrawData());

        GL.restoreRenderFlags();
    }
}