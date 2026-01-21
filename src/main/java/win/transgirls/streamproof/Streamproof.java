package win.transgirls.streamproof;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import win.transgirls.streamproof.imgui.ImGuiImplementation;
import win.transgirls.streamproof.input.KeyAction;
import win.transgirls.streamproof.input.KeyboardMain;
import win.transgirls.streamproof.input.MinecraftKeybind;
import win.transgirls.streamproof.tools.StreamproofSettings;
import win.transgirls.streamproof.types.MinHook;
import win.transgirls.streamproof.tools.ObsWrapper;
import win.transgirls.streamproof.tools.RenderQueue;
import win.transgirls.streamproof.visuals.Interface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Streamproof implements ClientModInitializer {
    public static final String id = "streamproof";
    public static final Logger LOGGER = LogManager.getLogger("Streamproof");

    public static final SoundEvent successSound =
            SoundEvent.of(Identifier.of("streamproof", "success"));
    public static final SoundEvent failureSound =
            SoundEvent.of(Identifier.of("streamproof", "failure"));

    public static final RenderQueue renderQueue = new RenderQueue();
    public static final StreamproofSettings settings = new StreamproofSettings();

    public static ObsWrapper obsWrapper;
    public static PointerByReference wglSwapBuffersObs;
    public static MinHook minhook = null;
    public static MinecraftClient client;
    public static Window window;

    public static Runnable renderGuiSecrets;
    public static Runnable renderWorldSecrets;

    public static GpuBufferSlice lastProjectionSlice;
    public static GlTexture secretDepthTex;
    public static GpuTextureView secretDepthView;

    private static KeyBinding.Category mainCategory;
    private static KeyBinding toggleConfig;
    private static KeyBinding closeConfig;

    public static void lateInit() {
        ImGuiImplementation.create(window.getHandle());

        Interface.init();
    }

    @Override
    public void onInitializeClient() {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            throw new RuntimeException("Streamproof only supports Windows");
        }

        String arch = System.getProperty("os.arch").toLowerCase();
        boolean arch64 = arch.contains("64") || arch.equals("amd64") || arch.equals("x86_64");

        if (arch.contains("aarch") || arch.contains("arm")) {
            throw new RuntimeException("Streamproof only supports x86 or x64 Windows");
        }

        InputStream minhookFile = Streamproof.class.getResourceAsStream(arch64 ? "/libs/MinHook.x64.dll" : "/libs/MinHook.x86.dll");
        if (minhookFile == null) {
            throw new RuntimeException("Couldn't find MinHook Dll");
        }

        File nativeDir = new File(MinecraftClient.getInstance().runDirectory, "native");
        nativeDir.mkdirs();
        File dllFile = new File(nativeDir, "MinHook.dll");

        try (FileOutputStream fos = new FileOutputStream(dllFile)) {
            minhookFile.transferTo(fos);
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract MinHook DLL", e);
        }

        System.setProperty("jna.library.path", nativeDir.getAbsolutePath());

        MinHook minhook = Native.load("MinHook", MinHook.class);
        Streamproof.minhook = minhook;

        LOGGER.info("MinHook.{}.dll loaded", arch64 ? "x64" : "x86");
        if (minhook.MH_Initialize() != 0) {
            throw new RuntimeException("MinHook failed to initialize");
        }

        mainCategory = KeyBinding.Category.create(Identifier.of("streamproof", "main"));

        toggleConfig = new KeyBinding(
                "key.streamproof.config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                mainCategory
        );

        closeConfig = new KeyBinding(
                "key.streamproof.close",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_ESCAPE,
                mainCategory
        );

        KeyBindingHelper.registerKeyBinding(toggleConfig);
        KeyBindingHelper.registerKeyBinding(closeConfig);

        Streamproof.client = MinecraftClient.getInstance();
        Streamproof.obsWrapper = new ObsWrapper();
        LOGGER.info("Streamproof loaded successfully ;3");

        KeyboardMain.on(new MinecraftKeybind(toggleConfig, (action) -> {
            if (action == KeyAction.PRESSING) {
                Interface.visible = !Interface.visible;
            }
        }));

        KeyboardMain.on(new MinecraftKeybind(closeConfig, (action) -> {
            if (action == KeyAction.PRESSING) {
                Interface.visible = false;
            }
        }));

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            Streamproof.obsWrapper.stop();
            Streamproof.settings.stop();
        });
    }
}