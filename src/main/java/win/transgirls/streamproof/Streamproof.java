package win.transgirls.streamproof;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import win.transgirls.streamproof.types.MinHook;
import win.transgirls.streamproof.tools.ObsWrapper;
import win.transgirls.streamproof.tools.RenderQueue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Streamproof implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Streamproof");

    public static final RenderQueue renderQueue = new RenderQueue();
    public static ObsWrapper obsWrapper;

    public static PointerByReference wglSwapBuffersObs;

    public static MinHook minhook = null;
    public static Runnable renderSecrets;

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

        File nativeDir = new File(Minecraft.getInstance().gameDirectory, "native");
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

        Streamproof.LOGGER.info("MinHook.{}.dll loaded", arch64 ? "x64" : "x86");
        if (minhook.MH_Initialize() != 0) {
            throw new RuntimeException("MinHook failed to initialize");
        }

        Streamproof.obsWrapper = new ObsWrapper();
        LOGGER.info("Streamproof loaded successfully ;3");
    }
}