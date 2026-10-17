package win.transgirls.streamproof;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.gui.ScreenArea;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.api.types.ComponentCategory;
import win.transgirls.streamproof.api.types.Impl;
import win.transgirls.streamproof.imgui.ImGuiImplementation;
import win.transgirls.streamproof.input.KeyAction;
import win.transgirls.streamproof.input.KeyboardMain;
import win.transgirls.streamproof.input.MinecraftKeybind;
import win.transgirls.streamproof.systems.gl.Shader;
import win.transgirls.streamproof.tools.StreamproofImpl;
import win.transgirls.streamproof.tools.StreamproofSettings;
import win.transgirls.streamproof.tools.SwapBufferHook;
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
            SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("streamproof", "success"));
    public static final SoundEvent failureSound =
            SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("streamproof", "failure"));

    public static final boolean immediatelyFastLoaded = FabricLoader.getInstance().isModLoaded("immediatelyfast");

    public static final StreamproofSettings settings = new StreamproofSettings();
    public static SwapBufferHook swapBufferHook;
    public static PointerByReference wglSwapBuffersObs;
    public static MinHook minhook = null;
    public static Minecraft mc;
    public static Window window;

    public static Set<ScreenArea> renderStates = new HashSet<>();
    public static Set<Object> renderCommands = new HashSet<>();
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
        ImGuiImplementation.create(window.handle());
        Interface.init();

        Streamproof.swapBufferHook = new SwapBufferHook();

        overlayShader = new Shader("overlay");
        if (_global_impl instanceof StreamproofImpl impl) {
            impl.init();
        }

        StreamproofAPI.add("CHAT_MESSAGES_OVERLAY", "Chat Messages", ComponentCategory.Gui, true);
        StreamproofAPI.add("CHAT_INPUT_OVERLAY", "Chat Input", ComponentCategory.Gui, true);
        StreamproofAPI.add("F3_OVERLAY", "F3", ComponentCategory.Gui, true);
        StreamproofAPI.add("SCOREBOARD", "Scoreboard", ComponentCategory.Gui, false);
        StreamproofAPI.add("PLAYERLIST", "Player List", ComponentCategory.Gui, false);
        StreamproofAPI.add("SUBTITLES", "Subtitles", ComponentCategory.Gui, false);

        StreamproofAPI.add("DEBUG_HITBOXES", "Hitboxes", ComponentCategory.World, true);
        StreamproofAPI.add("MAPS", "Maps", ComponentCategory.World, false);

        StreamproofAPI.add("STREAMPROOF_ENABLED", "Enabled", ComponentCategory.Hidden, true);
        StreamproofAPI.add("STREAMPROOF_SCREENSHOTS", "Streamproof Screenshots", ComponentCategory.Hidden, true);
        StreamproofAPI.add("STREAMPROOF_IMGUI_WINDOW", "ImGui Window", ComponentCategory.Hidden, true);

        if (anyClassLoaded(List.of("dlovin.inventoryhud.gui.InventoryGui"))) {
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

        if (anyClassLoaded(List.of("ru.berdinskiybear.armorhud.ArmorHudMod"))) {
            StreamproofAPI.add("UKUS_ARMOR_HUD", "Uku's Armor Hud", ComponentCategory.Gui, true);
        }

        if (anyClassLoaded(List.of("squeek.appleskin.client.HUDOverlayHandler"))) {
            StreamproofAPI.add("APPLE_SKIN_OVERLAY", "Apple Skin's Overlay", ComponentCategory.Gui, true);
        }

        if (anyClassLoaded(List.of("fi.dy.masa.litematica.render.LitematicaRenderer"))) {
            StreamproofAPI.add("LITEMATICA_WORLD_OVERLAY", "Litematica's World Overlay", ComponentCategory.World, true);
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

        File nativeDir = new File(System.getProperty("java.io.tmpdir"));
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

        KeyMapping.Category main = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("streamproof", "main"));

        KeyMapping toggleConfig = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.streamproof.config",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_RIGHT_SHIFT,
                        main
                )
        );

        KeyMapping closeConfig = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.streamproof.close",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_ESCAPE,
                        main
                )
        );

        Streamproof.mc = Minecraft.getInstance();
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

        ClientLifecycleEvents.CLIENT_STOPPING.register(_ -> {
            Streamproof.swapBufferHook.stop();
            Streamproof.settings.stop();
        });
    }
}