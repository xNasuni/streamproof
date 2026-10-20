package win.transgirls.streamproof.tools;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.system.MemoryUtil;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.types.ComponentCategory;
import win.transgirls.streamproof.api.types.Impl;
import win.transgirls.streamproof.api.types.RenderTarget;
import win.transgirls.streamproof.imgui.ImGuiImplementation;
import win.transgirls.streamproof.systems.gl.GL;
import win.transgirls.streamproof.systems.gl.Shader;
import win.transgirls.streamproof.systems.gl.StreamproofFramebuffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.renderer.MultiBufferSource;

import static win.transgirls.streamproof.Streamproof.LOGGER;
import static win.transgirls.streamproof.Streamproof.mc;

public class StreamproofImpl implements Impl {
    public StreamproofFramebuffer worldFb;
    public StreamproofFramebuffer guiFb;
    public boolean writing;
    public Shader overlay;

    public ArrayList<Consumer<NativeImage>> screenshotRunners = new ArrayList<>();

    public StreamproofImpl() {
        this.writing = false;
    }

    public void init() {
        this.worldFb = new StreamproofFramebuffer(true);
        this.guiFb = new StreamproofFramebuffer(false);

        mc.execute(() -> {
            overlay = new Shader("overlay");
        });
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean isWriting() {
        return this.writing;
    }

    @Override
    public boolean isReady() {
        return Streamproof.swapBufferHook != null;
    }

    @Override
    public boolean isStreamproof(String id) {
        return Streamproof.swapBufferHook.hooked && Streamproof.settings.isStreamproof(id);
    }

    @Override
    public RenderTarget getRenderTarget(String id) {
        return Streamproof.settings.getComponent(id).category == ComponentCategory.World ? RenderTarget.World : RenderTarget.Gui;
    }

    @Override
    public void add(String id, String label, ComponentCategory category, boolean defaultStreamproof) {
        try {
            Streamproof.settings.load(id, label, defaultStreamproof, category);
        } catch (Throwable e) {
            LOGGER.error("Streamproof failed to load id {}", id);
        }
    }

    @Override
    public void begin(String label) {
        if (this.isStreamproof(label)) {
            this.writing = true;
        }
    }

    @Override
    public void end(String label) {
        if (this.isStreamproof(label)) {
            this.writing = false;
        }
    }

    @Override
    public void _startFbo(RenderTarget target) {
        if (target == RenderTarget.World) {
            this.worldFb.bind();
        }

        if (target == RenderTarget.Gui) {
            this.guiFb.bind();
        }
    }

    @Override
    public void _stopFbo(RenderTarget target) {
        if (target == RenderTarget.World) {
            this.worldFb.unbind();
        }

        if (target == RenderTarget.Gui) {
            this.guiFb.unbind();
        }
    }

    public void forceDraw(MultiBufferSource consumer) {
        if (Streamproof.immediatelyFastLoaded && ImmediatelyFastCompat.forceDraw(consumer)) {
            return;
        }

        if (consumer instanceof MultiBufferSource.BufferSource immediate) {
            immediate.endBatch();
        }
    }

    public void composite() {
        try {
            if (this.overlay != null) {
                if (mc.gui.screen() == null || mc.gui.screen() instanceof ChatScreen || mc.gui.screen() instanceof GameModeSwitcherScreen) {
                    GL.saveRenderFlags();

                    if (this.worldFb.colorTexture != -1) {
                        GL.disableBlend();

                        GL.enableDepth();
                        overlay.bind();
                        GL.bindSampler(0, 0);
                        overlay.setTexture("Sampler0", this.worldFb.colorTexture, 0);
                        overlay.draw();

                        GL.bindTexture(0, 0);
                        GL.useProgram(0);
                    }

                    if (this.guiFb.colorTexture != -1) {
                        GL.enableBlend();
                        GlStateManager._blendFuncSeparate(
                                GL11C.GL_ONE, GL11C.GL_ONE_MINUS_SRC_ALPHA,
                                GL11C.GL_ONE, GL11C.GL_ONE_MINUS_SRC_ALPHA
                        );

                        GL.disableDepth();
                        overlay.bind();
                        GL.bindSampler(0, 0);
                        overlay.setTexture("Sampler0", this.guiFb.colorTexture, 0);
                        overlay.draw();

                        GL.bindTexture(0, 0);
                        GL.useProgram(0);
                    }

                    GL.restoreRenderFlags();
                }

                if (!screenshotRunners.isEmpty()) {
                    List<Consumer<NativeImage>> runners = new ArrayList<>(screenshotRunners);
                    screenshotRunners.clear();

                    captureImage(image -> {
                        for (Consumer<NativeImage> runner : runners) {
                            try {
                                runner.accept(image);
                            } catch (Throwable e) {
                                LOGGER.error(e);
                            }
                        }
                    });
                }
            }
        } catch (Throwable e) {
            LOGGER.error("Streamproof failed to bind overlay", e);
        }

        try {
            if (Streamproof.settings.isStreamproof("STREAMPROOF_IMGUI_WINDOW") && ImGuiImplementation.initialized) {
                ImGuiImplementation.draw();
            }
        } catch (Throwable e) {
            LOGGER.error("Streamproof failed to render imgui", e);
        }
    }

    public static void captureImage(Consumer<NativeImage> callback) {
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();
        int previousRead = GL.getInteger(GL30C.GL_READ_FRAMEBUFFER_BINDING);
        int previousAlignment = GL.getInteger(GL11C.GL_PACK_ALIGNMENT);
        ByteBuffer pixels = MemoryUtil.memAlloc(width * height * 4);

        try {
            GL.bindFrameBuffer(GL30C.GL_READ_FRAMEBUFFER, 0);
            GL.pixelStorei(GL11C.GL_PACK_ALIGNMENT, 1);
            GL.readPixels(0, 0, width, height, GL11C.GL_RGBA, GL11C.GL_UNSIGNED_BYTE, pixels);

            NativeImage image = new NativeImage(width, height, false);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    image.setPixelABGR(x, height - y - 1, pixels.getInt((x + y * width) * 4) | 0xFF000000);
                }
            }

            callback.accept(image);
        } finally {
            GL.pixelStorei(GL11C.GL_PACK_ALIGNMENT, previousAlignment);
            GL.bindFrameBuffer(GL30C.GL_READ_FRAMEBUFFER, previousRead);
            MemoryUtil.memFree(pixels);
        }
    }
}
