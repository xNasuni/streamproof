package win.transgirls.streamproof.systems.gl;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.visuals.Color;

import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL32C.*;
import static win.transgirls.streamproof.Streamproof.LOGGER;

public class Shader {
    private final String name;
    private final int id;
    private final Object2IntMap<String> uniformLocations = new Object2IntOpenHashMap<>();
    private int cachedVao = -1;
    private int cachedVbo = -1;

    public Shader(String label) {
        this.name = label;
        int id;

        try {
            String vertexShaderCode = IOUtils.resourceToString(String.format("/assets/streamproof/shaders/%s", label + ".vert"), StandardCharsets.UTF_8);
            String fragmentShaderCode = IOUtils.resourceToString(String.format("/assets/streamproof/shaders/%s", label + ".frag"), StandardCharsets.UTF_8);

            int vertexShader = GL.compileShader(GL_VERTEX_SHADER, vertexShaderCode, label + ".vert");
            int fragmentShader = GL.compileShader(GL_FRAGMENT_SHADER, fragmentShaderCode, label + ".frag");

            int programId = GL.createProgram();
            if (GL.linkProgram(programId, vertexShader, fragmentShader)) {
                LOGGER.error("Error linking shader {} program: {}", this.name, GL.lastError);
            }

            id = programId;
            LOGGER.info("Loaded shader \"{}\" as {}", this.name, id);

            GL.deleteShader(vertexShader);
            GL.deleteShader(fragmentShader);
        } catch (Throwable e) {
            LOGGER.error("Couldn't load shader {}, default to none.", this.name, e);
            id = -1;
        }

        this.id = id;
    }

    public void bind() {
        if (this.id == -1) {
            return;
        }

        GL.useProgram(this.id);
        GL.enableBlend();
        setDefaults();
    }

    public void draw() {
        if (this.id == -1) {
            return;
        }

        GL.disableDepth();

        if (cachedVao == -1) {
            float[] vertices = {
                    -1.0f, -1.0f,
                    1.0f, -1.0f,
                    -1.0f, 1.0f,

                    1.0f, -1.0f,
                    1.0f, 1.0f,
                    -1.0f, 1.0f
            };

            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
            vertexBuffer.put(vertices).flip();

            cachedVao = GL.genVertexArrays();
            cachedVbo = GL.genBuffers();

            GL.bindVertexArray(cachedVao);
            GL.bindBuffer(GL_ARRAY_BUFFER, cachedVbo);
            GL.bufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

            GL.vertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
            GL.enableVertexAttribArray(0);

            GL.bindVertexArray(0);
            GL.bindBuffer(GL_ARRAY_BUFFER, 0);
        }

        GL.bindVertexArray(cachedVao);
        GL.drawArrays(GL_TRIANGLES, 0, 6);
        GL.bindVertexArray(0);
    }

    private int getLocation(String name) {
        if (uniformLocations.containsKey(name)) return uniformLocations.getInt(name);

        int location = GL.getUniformLocation(this.id, name);
        uniformLocations.put(name, location);
        return location;
    }

    public void set(String name, boolean v) {
        int loc = getLocation(name);
        if (loc != -1) GL.uniformInt(loc, v ? GL_TRUE : GL_FALSE);
    }

    public void set(String name, int v) {
        int loc = getLocation(name);
        if (loc != -1) GL.uniformInt(loc, v);
    }

    public void set(String name, float v) {
        int loc = getLocation(name);
        if (loc != -1) GL.uniformFloat(loc, v);
    }

    public void set(String name, float v1, float v2) {
        int loc = getLocation(name);
        if (loc != -1) GL.uniformFloat2(loc, v1, v2);
    }

    public void set(String name, float v1, float v2, float v3) {
        int loc = getLocation(name);
        if (loc != -1) GL.uniformFloat3(loc, v1, v2, v3);
    }

    public void set(String name, float v1, float v2, float v3, float v4) {
        int loc = getLocation(name);
        if (loc != -1) GL.uniformFloat4(loc, v1, v2, v3, v4);
    }

    public void set(String name, Color color) {
        set(name, color.r() / 255f, color.g() / 255f, color.b() / 255f, color.a() / 255f);
    }

    public void setTexture(String uniformName, int textureId, int unit) {
        int loc = getLocation(uniformName);
        if (loc == -1) return;

        GL.bindTexture(unit, textureId);
        GL.uniformInt(loc, unit);
    }

    public void setDefaults() {
        set("uResolution", Streamproof.window.getFramebufferWidth(), Streamproof.window.getFramebufferHeight());
    }

    public void destroy() {
        if (cachedVbo != -1) GL.deleteBuffer(cachedVbo);
        if (cachedVao != -1) GL.deleteVertexArray(cachedVao);
        GL.deleteProgram(this.id);
    }

    public String getName() {
        return this.name;
    }

    public int getProgram() {
        return this.id;
    }
}