package win.transgirls.streamproof.systems.gl;

import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30C;
import win.transgirls.streamproof.Streamproof;

import static win.transgirls.streamproof.Streamproof.mc;

public class StreamproofFramebuffer {
    public int fbo = -1;
    public int colorTexture = -1;
    public int depthTexture = -1;
    public int width = -1;
    public int height = -1;

    public int lastFbo = -1;

    public boolean bound;
    public boolean dirty;

    private final boolean withDepthTexture;

    public StreamproofFramebuffer(boolean withDepthTexture) {
        this.withDepthTexture = withDepthTexture;
        this.fit();
    }

    public void fit() {
        int targetWidth = mc.getFramebuffer().textureWidth;
        int targetHeight = mc.getFramebuffer().textureHeight;

        if (targetWidth != this.width || targetHeight != this.height || fbo == -1) {
            this.reinit(targetWidth, targetHeight);
        }
    }

    private void reinit(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
        this.save();

        if (this.fbo != -1) {
            GL.deleteFrameBuffers(this.fbo);
            this.fbo = -1;
        }
        if (this.colorTexture != -1) {
            GL.deleteTexture(this.colorTexture);
            this.colorTexture = -1;
        }
        if (this.withDepthTexture && this.depthTexture != -1) {
            GL.deleteTexture(this.depthTexture);
            this.depthTexture = -1;
        }

        this.fbo = GL.genFrameBuffers();
        this.colorTexture = GL.genTexture();
        if (this.withDepthTexture) {
            this.depthTexture = GL.genTexture();
        }

        GL.bindTexture(this.colorTexture);
        GL.setDefaultColorTextureParameters();

        GL.uploadTexture2D(GL11C.GL_TEXTURE_2D, 0, GL11C.GL_RGBA8, width, height, 0, GL11C.GL_RGBA, GL11C.GL_UNSIGNED_BYTE, null);

        if (this.withDepthTexture) {
            GL.bindTexture(this.depthTexture);
            GL.setDefaultDepthTextureParameters();

            GL.uploadTexture2D(GL11C.GL_TEXTURE_2D, 0, GL30C.GL_DEPTH_COMPONENT32, width, height, 0, GL30C.GL_DEPTH_COMPONENT, GL30C.GL_UNSIGNED_INT, null);
        }

        GL.bindFrameBuffer(GL30C.GL_FRAMEBUFFER, this.fbo);
        GL.frameBufferTexture2D(GL30C.GL_FRAMEBUFFER, GL30C.GL_COLOR_ATTACHMENT0, GL11C.GL_TEXTURE_2D, this.colorTexture, 0);
        if (this.withDepthTexture) {
            GL.frameBufferTexture2D(GL30C.GL_FRAMEBUFFER, GL30C.GL_DEPTH_ATTACHMENT, GL11C.GL_TEXTURE_2D, this.depthTexture, 0);
        }

        if (!GL.checkFrameBufferStatus(GL30C.GL_FRAMEBUFFER)) {
            throw new RuntimeException("Streamproof failed to recreate framebuffer!");
        }

        GL.clearColor(0, 0, 0, 0);
        GL.clearColorBuffer();

        this.restore();
        this.dirty = false;
    }

    public void save() {
        if (this.lastFbo != -1) {
            Streamproof.LOGGER.info("we need a fbostack bcz it set from {} to {}", this.lastFbo, GL.getBoundFramebuffer());
        }
        this.lastFbo = GL.getBoundFramebuffer();
    }

    public void restore() {
        GL.bindFrameBuffer(GL30C.GL_FRAMEBUFFER, this.lastFbo);
        this.lastFbo = -1;
    }

    public void bind() {
        this.save();
        GL.bindFrameBuffer(GL30C.GL_DRAW_FRAMEBUFFER, this.fbo);
        this.bound = true;
    }

    public void unbind() {
        this.bound = false;
        this.restore();

        this.dirty = true;
    }

    public void clear() {
        this.bind();
        GL.clearColor(0, 0, 0, 0);
        GL.clearColorBuffer();
        this.unbind();

        this.dirty = false;
    }
}