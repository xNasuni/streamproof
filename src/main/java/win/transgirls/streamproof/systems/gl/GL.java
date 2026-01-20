package win.transgirls.streamproof.systems.gl;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import win.transgirls.streamproof.Streamproof;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;

import static org.lwjgl.opengl.GL30C.*;
import static win.transgirls.streamproof.Streamproof.LOGGER;

public class GL {
    private static final FloatBuffer MAT = BufferUtils.createFloatBuffer(4 * 4);
    public static int CURRENT_IBO;
    public static String lastError = "null";
    private static int prevIbo;

    private GL() {
    }

    public static int genVertexArray() {
        return GlStateManager._glGenVertexArrays();
    }

    public static int genFramebuffers() {
        return GlStateManager.glGenFramebuffers();
    }

    public static int createShader(int type) {
        return GlStateManager.glCreateShader(type);
    }

    public static int compileShader(int type, String shaderCode, String label) {
        int shaderId = GL.createShader(type);
        GlStateManager.glShaderSource(shaderId, shaderCode);
        GlStateManager.glCompileShader(shaderId);

        if (GlStateManager.glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            lastError = GlStateManager.glGetShaderInfoLog(shaderId, 4096);
            LOGGER.error("Error compiling shader \"{}\": {}", label, lastError);
            return -1;
        }

        return shaderId;
    }

    public static int genTexture() {
        return GlStateManager._genTexture();
    }

    public static int genBuffer() {
        return GlStateManager._glGenBuffers();
    }

    public static void bindBuffer(int target, int buffer) {
        GlStateManager._glBindBuffer(target, buffer);
    }

    public static void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        GlStateManager._vertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public static void drawArrays(int mode, int first, int count) {
        RenderSystem.assertOnRenderThread();
        GL20.glDrawArrays(mode, first, count);
    }

    public static void enableVertexAttribArray(int index) {
        GlStateManager._enableVertexAttribArray(index);
    }

    public static int texture() {
        return GlStateManager._genTexture();
    }

    public static void deleteBuffer(int buffer) {
        GlStateManager._glDeleteBuffers(buffer);
    }

    public static void deleteVertexArray(int vao) {
        RenderSystem.assertOnRenderThread();
        GL30.glDeleteVertexArrays(vao);
    }

    public static void deleteProgram(int program) {
        GlStateManager.glDeleteProgram(program);
    }

    public static void deleteShader(int shader) {
        GlStateManager.glDeleteShader(shader);
    }

    public static void deleteTexture(int id) {
        GlStateManager._deleteTexture(id);
    }

    public static void deleteFramebuffer(int fb) {
        GlStateManager._glDeleteFramebuffers(fb);
    }

    public static void bindVertexArray(int vao) {
        GlStateManager._glBindVertexArray(vao);
    }

    public static void bindVertexBuffer(int vbo) {
        GlStateManager._glBindBuffer(GL_ARRAY_BUFFER, vbo);
    }

    public static void bindIndexBuffer(int ibo) {
        if (ibo != 0) prevIbo = CURRENT_IBO;
        GlStateManager._glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo != 0 ? ibo : prevIbo);
    }

    public static void bindFramebuffer(int fbo) {
        GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    }

    public static void bufferData(int target, ByteBuffer data, int usage) {
        GlStateManager._glBufferData(target, data, usage);
    }

    public static void bufferData(int target, FloatBuffer data, int usage) {
        RenderSystem.assertOnRenderThread();
        GL15.glBufferData(target, data, usage);
    }

    public static void drawElements(int mode, int first, int type) {
        GlStateManager._drawElements(mode, first, type, 0);
    }

    public static int createProgram() {
        return GlStateManager.glCreateProgram();
    }

    public static boolean linkProgram(int program, int vertShader, int fragShader) {
        GlStateManager.glAttachShader(program, vertShader);
        GlStateManager.glAttachShader(program, fragShader);
        GlStateManager.glLinkProgram(program);

        if (GlStateManager.glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            lastError = GlStateManager.glGetProgramInfoLog(program, 4096);
            return true;
        }

        return false;
    }

    public static void useProgram(int program) {
        GlStateManager._glUseProgram(program);
    }

    public static void viewport(int x, int y, int width, int height) {
        GlStateManager._viewport(x, y, width, height);
    }

    public static int getUniformLocation(int program, String name) {
        return GlStateManager._glGetUniformLocation(program, name);
    }

    public static void uniformInt(int location, int v) {
        GlStateManager._glUniform1i(location, v);
    }

    public static void uniformFloat(int location, float v) {
        glUniform1f(location, v);
    }

    public static void uniformFloat2(int location, float v1, float v2) {
        glUniform2f(location, v1, v2);
    }

    public static void uniformFloat3(int location, float v1, float v2, float v3) {
        glUniform3f(location, v1, v2, v3);
    }

    public static void uniformFloat4(int location, float v1, float v2, float v3, float v4) {
        glUniform4f(location, v1, v2, v3, v4);
    }

    public static void uniformFloat3Array(int location, float[] v) {
        glUniform3fv(location, v);
    }

//    public static void uniformMatrix(int location, Matrix4f v) {
//        v.get(MAT);
//        GlStateManager._glUniformMatrix4(location, false, MAT);
//    }

    // Textures

    public static void pixelStore(int name, int param) {
        GlStateManager._pixelStore(name, param);
    }

    public static void textureParam(int target, int name, int param) {
        GlStateManager._texParameter(target, name, param);
    }

    public static void textureImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    public static void defaultPixelStore() {
        pixelStore(GL_UNPACK_SWAP_BYTES, GL_FALSE);
        pixelStore(GL_UNPACK_LSB_FIRST, GL_FALSE);
        pixelStore(GL_UNPACK_ROW_LENGTH, 0);
        pixelStore(GL_UNPACK_IMAGE_HEIGHT, 0);
        pixelStore(GL_UNPACK_SKIP_ROWS, 0);
        pixelStore(GL_UNPACK_SKIP_PIXELS, 0);
        pixelStore(GL_UNPACK_SKIP_IMAGES, 0);
        pixelStore(GL_UNPACK_ALIGNMENT, 4);
    }

    public static void generateMipmap(int target) {
        glGenerateMipmap(target);
    }

    // Framebuffers

    public static void framebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
        GlStateManager._glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
    }

    public static void clear(int mask) {
//        GlStateManager._clearColor(0, 0, 0, 1);
        GlStateManager._clear(mask);
    }

    // State

    public static void saveState() {
//        depthSaved = DEPTH.meteor$get();
//        blendSaved = BLEND.meteor$get();
//        cullSaved = CULL.meteor$get();
//        scissorSaved = SCISSOR.meteor$get();
    }

    public static void restoreState() {
//        DEPTH.meteor$set(depthSaved);
//        BLEND.meteor$set(blendSaved);
//        CULL.meteor$set(cullSaved);
//        SCISSOR.meteor$set(scissorSaved);

        disableLineSmooth();
    }

    public static void enableDepth() {
        GlStateManager._enableDepthTest();
    }

    public static void disableDepth() {
        GlStateManager._disableDepthTest();
    }

    public static void enableBlend() {
        GlStateManager._enableBlend();
        GlStateManager._blendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void disableBlend() {
        GlStateManager._disableBlend();
    }

    public static void enableCull() {
        GlStateManager._enableCull();
    }

    public static void disableCull() {
        GlStateManager._disableCull();
    }

    public static void enableScissorTest() {
        GlStateManager._enableScissorTest();
    }

    public static void disableScissorTest() {
        GlStateManager._disableScissorTest();
    }

    public static void enableLineSmooth() {
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);
    }

    public static void disableLineSmooth() {
        glDisable(GL_LINE_SMOOTH);
    }

    public static void bindTexture(Identifier id) {
        AbstractTexture texture = Streamproof.client.getTextureManager().getTexture(id);
//        bindTexture(texture., 0);
    }

    public static void bindTexture(int i, int slot) {
        GlStateManager._activeTexture(GL_TEXTURE0 + slot);
        GlStateManager._bindTexture(i);
    }

    public static void bindTexture(int i) {
        bindTexture(i, 0);
    }

    public static void resetTextureSlot() {
        GlStateManager._activeTexture(GL_TEXTURE0);
    }
}