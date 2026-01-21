package win.transgirls.streamproof.systems.gl;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.*;
import org.lwjgl.system.NativeType;

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

    public static void bindTexture(int texture) {
        GlStateManager._bindTexture(texture);
    }

    public static void bindSampler(int unit, int sampler) {
        GL33C.glBindSampler(unit, sampler);
    }

    public static void activeTexture(int texture) {
        GL13C.glActiveTexture(texture);
    }

    public static void blendFunc(int sfactor, int dfactor) {
        GL11C.glBlendFunc(sfactor, dfactor);
    }

    public static void enableBlend() {
        GlStateManager._enableBlend();
    }

    public static void disableBlend() {
        GlStateManager._disableBlend();
    }

    public static void enable(int target) {
        GL11C.glEnable(target);
    }

    public static void disable(int target) {
        GL11C.glDisable(target);
    }

    public static void setDefaultPixelStore() {
        GL11C.glPixelStorei(GL11C.GL_UNPACK_ROW_LENGTH, 0);
        GL11C.glPixelStorei(GL11C.GL_UNPACK_ALIGNMENT, 1);
        GL11C.glPixelStorei(GL11C.GL_UNPACK_SKIP_PIXELS, 0);
        GL11C.glPixelStorei(GL11C.GL_UNPACK_SKIP_ROWS, 0);
    }

    public static void setDefaultTextureParameters() {
        GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_LINEAR);
        GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_LINEAR);
        GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_S, GL12C.GL_CLAMP_TO_EDGE);
        GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_T, GL12C.GL_CLAMP_TO_EDGE);
    }

    public static void uploadTexture2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        GL11C.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
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
        lastBlend = GL11C.glIsEnabled(GL11C.GL_BLEND);
        lastCullFace = GL11C.glIsEnabled(GL11C.GL_CULL_FACE);
        lastDepthTest = GL11C.glIsEnabled(GL11C.GL_DEPTH_TEST);
        lastScissorTest = GL11C.glIsEnabled(GL11C.GL_SCISSOR_TEST);
    }

    public static void restoreRenderFlags() {
        if (lastBlend) GL11C.glEnable(GL11C.GL_BLEND);
        else GL11C.glDisable(GL11C.GL_BLEND);
        if (lastCullFace) GL11C.glEnable(GL11C.GL_CULL_FACE);
        else GL11C.glDisable(GL11C.GL_CULL_FACE);
        if (lastDepthTest) GL11C.glEnable(GL11C.GL_DEPTH_TEST);
        else GL11C.glDisable(GL11C.GL_DEPTH_TEST);
        if (lastScissorTest) GL11C.glEnable(GL11C.GL_SCISSOR_TEST);
        else GL11C.glDisable(GL11C.GL_SCISSOR_TEST);
    }

    public static void savePixelStore() {
        lastUnpackRowLen = GL11C.glGetInteger(GL11C.GL_UNPACK_ROW_LENGTH);
        lastUnpackAlignment = GL11C.glGetInteger(GL11C.GL_UNPACK_ALIGNMENT);
        lastUnpackSkipPixels = GL11C.glGetInteger(GL11C.GL_UNPACK_SKIP_PIXELS);
        lastUnpackSkipRows = GL11C.glGetInteger(GL11C.GL_UNPACK_SKIP_ROWS);
    }

    public static void restorePixelStore() {
        GL11C.glPixelStorei(GL11C.GL_UNPACK_ROW_LENGTH, lastUnpackRowLen);
        GL11C.glPixelStorei(GL11C.GL_UNPACK_ALIGNMENT, lastUnpackAlignment);
        GL11C.glPixelStorei(GL11C.GL_UNPACK_SKIP_PIXELS, lastUnpackSkipPixels);
        GL11C.glPixelStorei(GL11C.GL_UNPACK_SKIP_ROWS, lastUnpackSkipRows);
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

    public static int genTexture() {
        return GlStateManager._genTexture();
    }

    public static int genVertexArray() {
        return GlStateManager._glGenVertexArrays();
    }

    public static int genBuffer() {
        return GlStateManager._glGenBuffers();
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
}