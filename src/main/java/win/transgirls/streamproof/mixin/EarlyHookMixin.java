package win.transgirls.streamproof.mixin;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.types.Kernel32;
import win.transgirls.streamproof.types.MinHook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(Minecraft.class)
public class EarlyHookMixin {
    @Inject(method = "run", at = @At("HEAD"))
    private void runHook(CallbackInfo ci) {
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

        Streamproof.wglSwapBuffersOriginal = new PointerByReference();

        Pointer module = Kernel32.INSTANCE.GetModuleHandleA("opengl32.dll");
        Pointer proc = Kernel32.INSTANCE.GetProcAddress(module, "wglSwapBuffers");

        minhook.MH_CreateHook(proc, (hDc) -> {
//            if (Streamproof.afterRendering) {
//                return (boolean) Streamproof.wglSwapBuffersFunction.invoke(Boolean.class, new Object[]{hDc});
//            } else {

            Streamproof.renderQueue.release(null);
            Function original = Function.getFunction(Streamproof.wglSwapBuffersOriginal.getValue(), Function.ALT_CONVENTION);
            return (boolean) original.invoke(Boolean.class, new Object[]{hDc});
//            }
        }, Streamproof.wglSwapBuffersOriginal);

        Streamproof.wglSwapBuffersFunction = Function.getFunction(Streamproof.wglSwapBuffersOriginal.getValue(), Function.ALT_CONVENTION);

        minhook.MH_EnableHook(proc);


        Streamproof.LOGGER.info("opengl32.dll->wglSwapBuffers hooked");
    }
}