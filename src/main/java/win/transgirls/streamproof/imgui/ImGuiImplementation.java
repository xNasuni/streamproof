package win.transgirls.streamproof.imgui;

import imgui.*;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL33;
import win.transgirls.streamproof.Streamproof;
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

        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);

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

        boolean lastBlend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean lastCullFace = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean lastDepthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean lastScissorTest = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);

        int lastActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        int lastSampler = GL11.glGetInteger(GL33.GL_SAMPLER_BINDING);
        int lastTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        int lastUnpackRowLen = GL11.glGetInteger(GL11.GL_UNPACK_ROW_LENGTH);
        int lastUnpackAlignment = GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT);
        int lastUnpackSkipPixels = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_PIXELS);
        int lastUnpackSkipRows = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_ROWS);

        GL33.glBindSampler(0, 0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);

        gl3.renderDrawData(ImGui.getDrawData());

        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, lastUnpackRowLen);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, lastUnpackAlignment);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, lastUnpackSkipPixels);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, lastUnpackSkipRows);

        GL33.glBindSampler(0, lastSampler);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, lastTexture);
        GL13.glActiveTexture(lastActiveTexture);

        if (lastBlend) GL11.glEnable(GL11.GL_BLEND);
        else GL11.glDisable(GL11.GL_BLEND);
        if (lastCullFace) GL11.glEnable(GL11.GL_CULL_FACE);
        else GL11.glDisable(GL11.GL_CULL_FACE);
        if (lastDepthTest) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);
        if (lastScissorTest) GL11.glEnable(GL11.GL_SCISSOR_TEST);
        else GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long pointer = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(pointer);
        }
    }
}