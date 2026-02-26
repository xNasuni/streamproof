package win.transgirls.streamproof;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import win.transgirls.crossfabric.multiversion.VersionedKeybind;
import win.transgirls.crossfabric.multiversion.VersionedKeybindCategory;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.api.types.ComponentCategory;
import win.transgirls.streamproof.api.types.Impl;
import win.transgirls.streamproof.imgui.ImGuiImplementation;
import win.transgirls.streamproof.input.KeyAction;
import win.transgirls.streamproof.input.KeyboardMain;
import win.transgirls.streamproof.input.MinecraftKeybind;
import win.transgirls.streamproof.systems.gl.Shader;
import win.transgirls.streamproof.tools.ObsWrapper;
import win.transgirls.streamproof.tools.StreamproofImpl;
import win.transgirls.streamproof.tools.StreamproofSettings;
import win.transgirls.streamproof.types.MinHook;
import win.transgirls.streamproof.visuals.Interface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Streamproof implements ClientModInitializer {
    @SuppressWarnings("unused")
    // never change this or die (streamproof api reflection uses it)
    public static Impl _global_impl = new StreamproofImpl();

    public static final String id = "streamproof";
    public static final Logger LOGGER = LogManager.getLogger("Streamproof");

    public static final SoundEvent successSound =
            SoundEvent.of(Identifier.of("streamproof", "success"));
    public static final SoundEvent failureSound =
            SoundEvent.of(Identifier.of("streamproof", "failure"));

    public static final StreamproofSettings settings = new StreamproofSettings();
    public static ObsWrapper obsWrapper;
    public static PointerByReference wglSwapBuffersObs;
    public static MinHook minhook = null;
    public static MinecraftClient mc;
    public static Window window;

    public static Set<GuiElementRenderState> renderStates = new HashSet<>();
    public static Shader overlayShader;

    private static boolean anyClassLoaded(List<String> classList) {
        if (classList == null || classList.isEmpty()) return true;

        for (String className : classList) {
            try {
                Class.forName(className, false, Streamproof.class.getClassLoader());
                return true;
            } catch (ClassNotFoundException ignored) {
            }
        }

        return false;
    }

    public static void lateInit() {
        ImGuiImplementation.create(window.getHandle());
        Interface.init();

        overlayShader = new Shader("overlay");
        if (_global_impl instanceof StreamproofImpl impl) {
            impl.init();
        }

        StreamproofAPI.add("CHAT_MESSAGES_OVERLAY", "Chat Messages", ComponentCategory.Gui, true);
        StreamproofAPI.add("CHAT_INPUT_OVERLAY", "Chat Input", ComponentCategory.Gui, true);
        StreamproofAPI.add("F3_OVERLAY", "F3", ComponentCategory.Gui, true);
        StreamproofAPI.add("DEBUG_HITBOXES", "Hitboxes", ComponentCategory.World, true);
        StreamproofAPI.add("STREAMPROOF_IMGUI_WINDOW", "ImGui Window", ComponentCategory.Hidden, true);

        if (anyClassLoaded(List.of("dlovin.inventoryhud.gui.InventoryHUDGui"))) {
            StreamproofAPI.add("INVENTORY_HUD_ARMOR", "Inventory Hud Armor", ComponentCategory.Gui, true);
            StreamproofAPI.add("INVENTORY_HUD_POTION", "Inventory Hud Potions", ComponentCategory.Gui, true);
            StreamproofAPI.add("INVENTORY_HUD_INVENTORY", "Inventory Hud Inventory", ComponentCategory.Gui, true);
        }

        if (anyClassLoaded(List.of("xaero.common.minimap.render.MinimapRenderer"))) {
            StreamproofAPI.add("XAEROS_MINIMAP_MINIMAP", "Xaero's Minimap Map", ComponentCategory.Gui, true);
        }

        if (anyClassLoaded(List.of("xaero.hud.minimap.waypoint.render.world.WaypointWorldRenderer"))) {
            StreamproofAPI.add("XAEROS_MINIMAP_WAYPOINTS", "Xaero's Minimap Waypoints", ComponentCategory.Gui, true);
        }
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

        VersionedKeybindCategory main = new VersionedKeybindCategory("streamproof", "main");

        KeyBinding toggleConfig = VersionedKeybind.create(
                "key.streamproof.config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                main
        );

        KeyBinding closeConfig = VersionedKeybind.create(
                "key.streamproof.close",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_ESCAPE,
                main
        );

        KeyBindingHelper.registerKeyBinding(toggleConfig);
        KeyBindingHelper.registerKeyBinding(closeConfig);

        Streamproof.mc = MinecraftClient.getInstance();
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