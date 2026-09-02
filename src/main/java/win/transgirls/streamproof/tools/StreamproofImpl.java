package win.transgirls.streamproof.tools;

import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameModeSwitcherScreen;
import net.minecraft.client.render.VertexConsumerProvider;
import org.lwjgl.opengl.GL11C;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.types.ComponentCategory;
import win.transgirls.streamproof.api.types.Impl;
import win.transgirls.streamproof.api.types.RenderTarget;
import win.transgirls.streamproof.imgui.ImGuiImplementation;
import win.transgirls.streamproof.systems.gl.GL;
import win.transgirls.streamproof.systems.gl.Shader;
import win.transgirls.streamproof.systems.gl.StreamproofFramebuffer;

import static win.transgirls.streamproof.Streamproof.LOGGER;
import static win.transgirls.streamproof.Streamproof.mc;

public class StreamproofImpl implements Impl {
    public StreamproofFramebuffer worldFb;
    public StreamproofFramebuffer guiFb;
    public boolean writing;
    public Shader overlay;

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

    public void forceDraw(VertexConsumerProvider consumer) {
        if (consumer instanceof VertexConsumerProvider.Immediate immediate) {
            if (!immediate.pending.get(immediate.currentLayer).building) {
                return;
            }
            immediate.draw();
        }
    }

    public void composite() {
        try {
            if (this.overlay != null) {
                if (mc.currentScreen == null || mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof GameModeSwitcherScreen) {
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
}