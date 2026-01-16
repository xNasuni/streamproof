package win.transgirls.streamproof;

import com.sun.jna.Function;
import com.sun.jna.ptr.PointerByReference;
import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import win.transgirls.streamproof.types.MinHook;
import win.transgirls.streamproof.tools.ObsWindow;
import win.transgirls.streamproof.tools.RenderQueue;

public class Streamproof implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Streamproof");
    public static final RenderQueue renderQueue = new RenderQueue();
    public static final ObsWindow obsWindow = new ObsWindow(Minecraft.getInstance());
    public static MinHook minhook = null;
    public static boolean afterRendering = false;
    public static PointerByReference wglSwapBuffersOriginal;
    public static Function wglSwapBuffersFunction;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Streamproof loaded successfully ;3");
    }
}