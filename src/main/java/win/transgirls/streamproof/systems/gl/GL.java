package win.transgirls.streamproof.systems.gl;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static win.transgirls.streamproof.Streamproof.LOGGER;

public class GL {
    public static String lastError = "null";
    private static int lastUnpackRowLen;
    private static int lastUnpackAlignment;
    private static int lastUnpackSkipPixels;
    private static int lastUnpackSkipRows;
    private static boolean lastBlend;
    private static boolean lastCullFace;
    private static boolean lastDepthTest;
    private static boolean lastScissorTest;
    private static int lastActiveTexture;
    private static int lastSampler;
    private static int lastTexture;
    private static int copySrcFbo = -1;
    private static int copyDstFbo = -1;

    public static boolean checkFrameBufferStatus(int target) {
        int status = GL30C.glCheckFramebufferStatus(target);
        return status == GL30C.GL_FRAMEBUFFER_COMPLETE;
    }

    public static void bindTexture(int texture) {
        GlStateManager._bindTexture(texture);
    }

    public static void bindTexture(int unit, int texture) {
        GlStateManager._activeTexture(GL13C.GL_TEXTURE0 + unit);
        GlStateManager._bindTexture(texture);
    }

    public static void bindSampler(int unit, int sampler) {
        GL33C.glBindSampler(unit, sampler);
    }

    public static void bindFrameBuffer(int target, int framebuffer) {
        GlStateManager._glBindFramebuffer(target, framebuffer);
    }

    public static void frameBufferTexture2D(int target, int attachment, int texTarget, int tex, int level) {
        GlStateManager._glFramebufferTexture2D(target, attachment, texTarget, tex, level);
    }

    public static void activeTexture(int texture) {
        GlStateManager._activeTexture(texture);
    }

    public static void blendFunc(int sfactor, int dfactor) {
        GL11C.glBlendFunc(sfactor, dfactor);
    }

    public static void enableBlend() {
        GlStateManager._enableBlend();
    }

    public static void disableScissor() {
        GlStateManager._disableScissorTest();
    }

    public static void disableBlend() {
        GlStateManager._disableBlend();
    }

    public static void setDefaultPixelStore() {
        GlStateManager._pixelStore(GL11C.GL_UNPACK_ROW_LENGTH, 0);
        GlStateManager._pixelStore(GL11C.GL_UNPACK_ALIGNMENT, 1);
        GlStateManager._pixelStore(GL11C.GL_UNPACK_SKIP_PIXELS, 0);
        GlStateManager._pixelStore(GL11C.GL_UNPACK_SKIP_ROWS, 0);
    }

    public static void setDefaultColorTextureParameters() {
        GlStateManager._texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_LINEAR);
        GlStateManager._texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_LINEAR);
        GlStateManager._texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_S, GL12C.GL_CLAMP_TO_EDGE);
        GlStateManager._texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_T, GL12C.GL_CLAMP_TO_EDGE);
    }

    public static void setDefaultDepthTextureParameters() {
        GlStateManager._texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_NEAREST);
        GlStateManager._texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_NEAREST);
        GlStateManager._texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_S, GL12C.GL_CLAMP_TO_EDGE);
        GlStateManager._texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_T, GL12C.GL_CLAMP_TO_EDGE);
    }

    public static void uploadTexture2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        GL11C.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
    }

    public static void debugTextureDifference(int srcTex, int dstTex) {
        int[] srcInfo = getTextureInfo(srcTex);
        int[] dstInfo = getTextureInfo(dstTex);

        LOGGER.info("--- Texture Comparison ---");
        LOGGER.info("Source (MC): {}x{} | Format: 0x{}", srcInfo[0], srcInfo[1], Integer.toHexString(srcInfo[2]));
        LOGGER.info("Dest (Mine): {}x{} | Format: 0x{}", dstInfo[0], dstInfo[1], Integer.toHexString(dstInfo[2]));

        if (srcInfo[2] != dstInfo[2]) {
            LOGGER.error("CRITICAL: Internal Format Mismatch! Blit will fail.");
        }
        if (srcInfo[0] != dstInfo[0] || srcInfo[1] != dstInfo[1]) {
            LOGGER.warn("Size mismatch: Blit will scale the image.");
        }
    }

    private static int[] getTextureInfo(int tex) {
        GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, tex);
        int w = GL11C.glGetTexLevelParameteri(GL11C.GL_TEXTURE_2D, 0, GL11C.GL_TEXTURE_WIDTH);
        int h = GL11C.glGetTexLevelParameteri(GL11C.GL_TEXTURE_2D, 0, GL11C.GL_TEXTURE_HEIGHT);
        int format = GL11C.glGetTexLevelParameteri(GL11C.GL_TEXTURE_2D, 0, GL11C.GL_TEXTURE_INTERNAL_FORMAT);
        return new int[]{w, h, format};
    }

    public static void copyDepth(int srcTex, int dstTex, int width, int height) {
        if (copySrcFbo == -1) copySrcFbo = GL.genFrameBuffers();
        if (copyDstFbo == -1) copyDstFbo = GL.genFrameBuffers();

        int oReadFbo = GlStateManager.getFrameBuffer(GL30C.GL_READ_FRAMEBUFFER);
        int oWriteFbo = GlStateManager.getFrameBuffer(GL30C.GL_DRAW_FRAMEBUFFER);

        GL.bindFrameBuffer(GL30C.GL_READ_FRAMEBUFFER, copySrcFbo);
        GL.frameBufferTexture2D(GL30C.GL_READ_FRAMEBUFFER, GL30C.GL_DEPTH_ATTACHMENT, GL11C.GL_TEXTURE_2D, srcTex, 0);

        GL.bindFrameBuffer(GL30C.GL_DRAW_FRAMEBUFFER, copyDstFbo);
        GL.frameBufferTexture2D(GL30C.GL_DRAW_FRAMEBUFFER, GL30C.GL_DEPTH_ATTACHMENT, GL11C.GL_TEXTURE_2D, dstTex, 0);

        GlStateManager._glBlitFrameBuffer(0, 0, width, height, 0, 0, width, height, GL11C.GL_DEPTH_BUFFER_BIT, GL11C.GL_NEAREST);

        GlStateManager._glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, oReadFbo);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, oWriteFbo);
    }

    public static void copyDepthFromScreen(int fbo, int width, int height) {
        GlStateManager._glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, 0);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, fbo);

        GlStateManager._glBlitFrameBuffer(
                0, 0, width, height,
                0, 0, width, height,
                GL11C.GL_DEPTH_BUFFER_BIT,
                GL11C.GL_NEAREST
        );

        GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER, 0);
    }

    public static void saveTextureState() {
        lastActiveTexture = GL11C.glGetInteger(GL13C.GL_ACTIVE_TEXTURE);
        lastSampler = GL11C.glGetInteger(GL33C.GL_SAMPLER_BINDING);
        lastTexture = GL11C.glGetInteger(GL11C.GL_TEXTURE_BINDING_2D);
    }

    public static void restoreTextureState() {
        GL33C.glBindSampler(0, lastSampler);
        GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, lastTexture);
        GL13C.glActiveTexture(lastActiveTexture);
    }

    public static void saveRenderFlags() {
        lastBlend = GlStateManager.BLEND.capState.state;
        lastCullFace = GlStateManager.CULL.capState.state;
        lastDepthTest = GlStateManager.DEPTH.capState.state;
        lastScissorTest = GlStateManager.SCISSOR.capState.state;
    }

    public static void restoreRenderFlags() {
        if (lastBlend) GlStateManager._enableBlend();
        else GlStateManager._disableBlend();
        if (lastCullFace) GlStateManager._enableCull();
        else GlStateManager._disableCull();
        if (lastDepthTest) GlStateManager._enableDepthTest();
        else GlStateManager._disableDepthTest();
        if (lastScissorTest) GlStateManager._enableScissorTest();
        else GlStateManager._disableScissorTest();
    }

    public static void savePixelStore() {
        lastUnpackRowLen = GlStateManager._getInteger(GL11C.GL_UNPACK_ROW_LENGTH);
        lastUnpackAlignment = GlStateManager._getInteger(GL11C.GL_UNPACK_ALIGNMENT);
        lastUnpackSkipPixels = GlStateManager._getInteger(GL11C.GL_UNPACK_SKIP_PIXELS);
        lastUnpackSkipRows = GlStateManager._getInteger(GL11C.GL_UNPACK_SKIP_ROWS);
    }

    public static void restorePixelStore() {
        GlStateManager._pixelStore(GL11C.GL_UNPACK_ROW_LENGTH, lastUnpackRowLen);
        GlStateManager._pixelStore(GL11C.GL_UNPACK_ALIGNMENT, lastUnpackAlignment);
        GlStateManager._pixelStore(GL11C.GL_UNPACK_SKIP_PIXELS, lastUnpackSkipPixels);
        GlStateManager._pixelStore(GL11C.GL_UNPACK_SKIP_ROWS, lastUnpackSkipRows);
    }

    public static void depthMask(boolean mask) {
        GlStateManager._depthMask(mask);
    }

    public static void depthFunc(int func) {
        GlStateManager._depthFunc(func);
    }

    public static void enableDepth() {
        GlStateManager._enableDepthTest();
    }

    public static void disableDepth() {
        GlStateManager._disableDepthTest();
    }

    public static void disableCull() {
        GlStateManager._disableCull();
    }

    public static int genTexture() {
        return GlStateManager._genTexture();
    }

    public static int genVertexArrays() {
        return GlStateManager._glGenVertexArrays();
    }

    public static int genBuffers() {
        return GlStateManager._glGenBuffers();
    }

    public static int genFrameBuffers() {
        return GlStateManager.glGenFramebuffers();
    }

    public static int getBoundFramebuffer() {
        return GlStateManager.getFrameBuffer(GL30C.GL_DRAW_FRAMEBUFFER);
    }

    public static void bindVertexArray(int array) {
        GlStateManager._glBindVertexArray(array);
    }

    public static void bindBuffer(int target, int buffer) {
        GlStateManager._glBindBuffer(target, buffer);
    }

    public static void bufferData(int target, FloatBuffer size, int usage) {
        RenderSystem.assertOnRenderThread();
        GL15C.glBufferData(target, size, usage);
    }

    public static void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        GlStateManager._vertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public static void enableVertexAttribArray(int index) {
        GlStateManager._enableVertexAttribArray(index);
    }

    public static void drawArrays(int mode, int first, int count) {
        GlStateManager._drawArrays(mode, first, count);
    }

    public static int getUniformLocation(int program, String name) {
        return GlStateManager._glGetUniformLocation(program, name);
    }

    public static void uniformInt(int location, int v) {
        GlStateManager._glUniform1i(location, v);
    }

    public static void uniformFloat(int location, float v) {
        GL20C.glUniform1f(location, v);
    }

    public static void uniformFloat2(int location, float v1, float v2) {
        GL20C.glUniform2f(location, v1, v2);
    }

    public static void uniformFloat3(int location, float v1, float v2, float v3) {
        GL20C.glUniform3f(location, v1, v2, v3);
    }

    public static void uniformFloat4(int location, float v1, float v2, float v3, float v4) {
        GL20C.glUniform4f(location, v1, v2, v3, v4);
    }

    public static void deleteBuffer(int buffer) {
        GlStateManager._glDeleteBuffers(buffer);
    }

    public static void deleteFrameBuffers(int fbo) {
        GlStateManager._glDeleteFramebuffers(fbo);
    }

    public static void deleteTexture(int tex) {
        GlStateManager._deleteTexture(tex);
    }

    public static void deleteVertexArray(int vao) {
        RenderSystem.assertOnRenderThread();
        GL30C.glDeleteVertexArrays(vao);
    }

    public static void deleteProgram(int program) {
        GlStateManager.glDeleteProgram(program);
    }

    public static void deleteShader(int shader) {
        GlStateManager.glDeleteShader(shader);
    }

    public static int createProgram() {
        return GlStateManager.glCreateProgram();
    }

    public static int compileShader(int type, String shaderCode, String label) {
        int shaderId = GL.createShader(type);
        GlStateManager.glShaderSource(shaderId, shaderCode);
        GlStateManager.glCompileShader(shaderId);

        if (GlStateManager.glGetShaderi(shaderId, GL20C.GL_COMPILE_STATUS) == GL11C.GL_FALSE) {
            lastError = GlStateManager.glGetShaderInfoLog(shaderId, 4096);
            LOGGER.error("Error compiling shader \"{}\": {}", label, lastError);
            return -1;
        }

        return shaderId;
    }

    public static boolean linkProgram(int program, int vertShader, int fragShader) {
        GlStateManager.glAttachShader(program, vertShader);
        GlStateManager.glAttachShader(program, fragShader);
        GlStateManager.glLinkProgram(program);

        if (GlStateManager.glGetProgrami(program, GL20C.GL_LINK_STATUS) == GL20C.GL_FALSE) {
            lastError = GlStateManager.glGetProgramInfoLog(program, 4096);
            return true;
        }

        return false;
    }

    public static int createShader(int type) {
        return GlStateManager.glCreateShader(type);
    }

    public static void useProgram(int program) {
        GlStateManager._glUseProgram(program);
    }

    public static void clearColor(float r, float g, float b, float a) {
        GL11C.glClearColor(r, g, b, a);
    }

    public static void clearColorBuffer() {
        GlStateManager._clear(GL11C.GL_COLOR_BUFFER_BIT);
    }
}