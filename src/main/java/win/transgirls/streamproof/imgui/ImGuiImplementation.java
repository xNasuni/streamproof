package win.transgirls.streamproof.imgui;

import imgui.*;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.systems.gl.GL;
import win.transgirls.streamproof.visuals.Color;
import win.transgirls.streamproof.visuals.Interface;
import win.transgirls.streamproof.visuals.Style;

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
        Color.tick += Math.PI_f / 512;

        ImGuiIO io = ImGui.getIO();
        if (Streamproof.client != null && Streamproof.window != null) {
            io.setDisplaySize(Streamproof.window.getFramebufferWidth(), Streamproof.window.getFramebufferHeight());
            io.setDisplayFramebufferScale(1.0f, 1.0f);
        }

        glfw.newFrame();
        ImGui.newFrame();

        Interface.render(handle);

        ImGui.render();

        GL.saveRenderFlags();
        GL.saveTextureState();
        GL.savePixelStore();

        GL.activeTexture(GL13C.GL_TEXTURE0);

        GL.bindSampler(0, 0);
        GL.enable(GL11C.GL_BLEND);
        GL.blendFunc(GL11C.GL_SRC_ALPHA, GL11C.GL_ONE_MINUS_SRC_ALPHA);
        GL.disable(GL11C.GL_CULL_FACE);
        GL.disable(GL11C.GL_DEPTH_TEST);
        GL.disable(GL11C.GL_SCISSOR_TEST);

        GL.setDefaultPixelStore();

        gl3.renderDrawData(ImGui.getDrawData());

        GL.restorePixelStore();
        GL.restoreTextureState();
        GL.restoreRenderFlags();

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long pointer = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(pointer);
        }
    }
}