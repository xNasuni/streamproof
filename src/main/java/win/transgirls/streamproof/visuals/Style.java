package win.transgirls.streamproof.visuals;


import com.mojang.blaze3d.opengl.GlStateManager;
import imgui.*;
import imgui.flag.ImGuiCol;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.systems.gl.GL;
import win.transgirls.streamproof.systems.gl.Shader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static win.transgirls.streamproof.Streamproof.LOGGER;
import static win.transgirls.streamproof.visuals.Color.rgba;

public class Style {
    public static Map<Integer, ImFont> fonts = new HashMap<>();
    public static int iconTexture = -1;
    public static Shader backdropShader = new Shader("backdrop");


    public static void setup() {
        final ImGuiIO data = ImGui.getIO();
        {
            ImGui.getStyle().setFrameRounding(6f);
            ImGui.getStyle().setWindowRounding(6f);
            ImGui.getStyle().setWindowBorderSize(1f);
            ImGui.getStyle().setPopupRounding(6f);
            ImGui.getStyle().setScrollbarRounding(6f);
            ImGui.getStyle().setGrabRounding(12f);
            ImGui.getStyle().setChildRounding(6f);
            ImGui.getStyle().setTabRounding(6f);
            ImGui.getStyle().setGrabMinSize(15f);
            ImGui.getStyle().setChildBorderSize(0f);
            ImGui.getStyle().setPopupBorderSize(0f);
            ImGui.getStyle().setFrameBorderSize(0f);
            ImGui.getStyle().setTabBorderSize(0f);
            ImGui.getStyle().setScrollbarSize(14f);
            ImGui.getStyle().setWindowPadding(6f, 6f);

            ImGui.pushStyleColor(ImGuiCol.WindowBg, rgba(15, 15, 15, 217));
            ImGui.pushStyleColor(ImGuiCol.TitleBgActive, rgba(15, 15, 15, 255));
            ImGui.pushStyleColor(ImGuiCol.Border, rgba(255, 255, 255, 127));
            ImGui.pushStyleColor(ImGuiCol.CheckMark, rgba(255, 168, 240, 255));
            ImGui.pushStyleColor(ImGuiCol.Button, rgba(28, 28, 28, 255));
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, rgba(35, 35, 35, 255));
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, rgba(62, 62, 62, 255));
            ImGui.pushStyleColor(ImGuiCol.DockingPreview, rgba(250, 168, 240, 239));
            ImGui.pushStyleColor(ImGuiCol.SliderGrab, rgba(255, 168, 240, 255));
            ImGui.pushStyleColor(ImGuiCol.SliderGrabActive, rgba(255, 196, 245, 255));
            ImGui.pushStyleColor(ImGuiCol.FrameBg, rgba(110, 110, 110, 99));
            ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, rgba(50, 50, 52, 121));
            ImGui.pushStyleColor(ImGuiCol.FrameBgActive, rgba(50, 50, 52, 195));
            ImGui.pushStyleColor(ImGuiCol.ScrollbarGrab, rgba(35, 35, 35, 182));
            ImGui.pushStyleColor(ImGuiCol.ScrollbarGrabHovered, rgba(35, 35, 35, 182));
            ImGui.pushStyleColor(ImGuiCol.ScrollbarGrabActive, rgba(35, 35, 35, 255));
            ImGui.pushStyleColor(ImGuiCol.Header, rgba(240, 196, 255, 108));
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, rgba(240, 196, 255, 179));
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, rgba(240, 196, 255, 255));
            ImGui.pushStyleColor(ImGuiCol.ResizeGrip, rgba(0, 0, 0, 0));
            ImGui.pushStyleColor(ImGuiCol.ResizeGripHovered, rgba(255, 168, 240, 100));
            ImGui.pushStyleColor(ImGuiCol.ResizeGripActive, rgba(255, 168, 240, 182));
            ImGui.pushStyleColor(ImGuiCol.TextSelectedBg, rgba(252, 135, 255, 89));
            ImGui.pushStyleColor(ImGuiCol.PopupBg, rgba(16, 16, 16, 255));
        }

        try (InputStream fontStream = Streamproof.class.getResourceAsStream("/assets/streamproof/fonts/inter.ttf")) {
            if (fontStream == null) {
                throw new IOException("/assets/streamproof/fonts/inter.ttf not found.");
            }

            byte[] fontBytes = fontStream.readAllBytes();

            for (int i = 12; i < 24; i++) {
                ImFontConfig conf = new ImFontConfig();
                conf.setName(String.format("Inter %dpx", i));

                ImFont font = data.getFonts().addFontFromMemoryTTF(fontBytes, (float) i, conf);
                fonts.put(i, font);

                if (i == 18) {
                    data.setFontDefault(font);
                }
            }
        } catch (Throwable e) {
            LOGGER.error("Couldn't load font, using fallback.", e);
        }

        try {
            iconTexture = Style.loadTexture("icon.png");
        } catch (Throwable e) {
            LOGGER.error("Couldn't load icon texture, default to none.", e);
        }
    }

    public static ImFont getFont(int size) {
        return fonts.getOrDefault(size, ImGui.getFont());
    }

    public static int loadTexture(String path) {
        int width, height;
        int textureId;

        try (InputStream stream = Streamproof.class.getResourceAsStream(String.format("/assets/streamproof/%s", path))) {
            if (stream == null) {
                throw new RuntimeException(String.format("Couldn't find file: %s", path));
            }

            byte[] imageData = stream.readAllBytes();

            ByteBuffer nativeBuffer = MemoryUtil.memAlloc(imageData.length);
            nativeBuffer.put(imageData).flip();

            IntBuffer widthBuffer = MemoryUtil.memAllocInt(1);
            IntBuffer heightBuffer = MemoryUtil.memAllocInt(1);
            IntBuffer channelsBuffer = MemoryUtil.memAllocInt(1);

            ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(
                    nativeBuffer, widthBuffer, heightBuffer, channelsBuffer, 4);

            if (imageBuffer == null) {
                MemoryUtil.memFree(nativeBuffer);
                MemoryUtil.memFree(widthBuffer);
                MemoryUtil.memFree(heightBuffer);
                MemoryUtil.memFree(channelsBuffer);
                throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
            }

            width = widthBuffer.get(0);
            height = heightBuffer.get(0);

            textureId = GL.genTexture();

            GL.bindTexture(textureId);
            GL.setDefaultPixelStore();
            GL.setDefaultTextureParameters();

            GL.uploadTexture2D(GL11C.GL_TEXTURE_2D, 0, GL11C.GL_RGBA, width, height, 0, GL11C.GL_RGBA, GL11C.GL_UNSIGNED_BYTE, imageBuffer);

            STBImage.stbi_image_free(imageBuffer);
            MemoryUtil.memFree(nativeBuffer);
            MemoryUtil.memFree(widthBuffer);
            MemoryUtil.memFree(heightBuffer);
            MemoryUtil.memFree(channelsBuffer);
        } catch (Throwable e) {
            LOGGER.error("Failed to load texture from path {}: {}", path, e);
            textureId = -1;
        }

        return textureId;
    }
}