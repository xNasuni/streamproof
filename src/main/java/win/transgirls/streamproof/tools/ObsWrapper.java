package win.transgirls.streamproof.tools;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Tlhelp32.MODULEENTRY32W;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.PointerByReference;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.text.Text;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.types.Kernel32;
import win.transgirls.streamproof.types.MinHook;
import win.transgirls.streamproof.types.ResultToast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sun.jna.platform.win32.Tlhelp32.TH32CS_SNAPMODULE;
import static com.sun.jna.platform.win32.Tlhelp32.TH32CS_SNAPMODULE32;
import static win.transgirls.streamproof.Streamproof.*;

public class ObsWrapper {
    private static final String obsDll = "(?i)^graphics-hook(32|64)\\.dll$";
    private final long pid;

    public volatile boolean running = true;
    public boolean hooked = false;
    private ScheduledExecutorService obsDetector;
    @SuppressWarnings("FieldCanBeLocal")
    private MinHook.GlSwapBuffers hookCallback; // prevent GC of JNA callback

    public ObsWrapper() {
        this.pid = ProcessHandle.current().pid();
        startDetectionThread();
    }

    private void startDetectionThread() {
        obsDetector = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "OBS Detection Thread");
            t.setDaemon(true);
            return t;
        });

        obsDetector.scheduleWithFixedDelay(() -> {
            if (!running) {
                obsDetector.shutdown();
                return;
            }

            if (isOBSHooked()) {
                LOGGER.info("OBS hook detected. Wrapping in 3s...");

                obsDetector.schedule(() -> {
                    LOGGER.info("OBS hook detected. Wrapping now...");
                    initHook();
                    running = false;
                    obsDetector.shutdown();
                }, 3, TimeUnit.SECONDS);
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    private void initHook() {
        Pointer module = Kernel32.INSTANCE.GetModuleHandleA("opengl32.dll");
        Pointer proc = Kernel32.INSTANCE.GetProcAddress(module, "wglSwapBuffers");

        Streamproof.wglSwapBuffersObs = new PointerByReference();

        this.hookCallback = (hDc) -> {
            Function original = Function.getFunction(Streamproof.wglSwapBuffersObs.getValue(), Function.ALT_CONVENTION);
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.composite();
            }

            return (boolean) original.invoke(Boolean.class, new Object[]{hDc});
        };
        int hr = minhook.MH_CreateHook(proc, this.hookCallback, Streamproof.wglSwapBuffersObs);

        int hr2 = minhook.MH_EnableHook(proc);

        if (hr == 0 && hr2 == 0) {
            hooked = true;
            mc.getSoundManager().play(PositionedSoundInstance.ui(Streamproof.successSound, 1.0f));
            mc.getToastManager().add(new ResultToast(Text.literal("OBS was successfully hooked"), true));
            LOGGER.info("opengl32.dll->wglSwapBuffers hooked after OBS ({}, {}, {})",
                    Streamproof.wglSwapBuffersObs.getValue(), hr, hr2);
        } else {
            mc.getSoundManager().play(PositionedSoundInstance.ui(Streamproof.failureSound, 1.0f));
            mc.getToastManager().add(new ResultToast(Text.literal("OBS failed to hook"), false));
            LOGGER.warn("opengl32.dll-wglSwapBuffers hook failed while attempting to create or enable! Create={}, Enable={}", hr, hr2);
        }
    }

    public void stop() {
        if (hooked) {
            Pointer module = Kernel32.INSTANCE.GetModuleHandleA("opengl32.dll");
            Pointer proc = Kernel32.INSTANCE.GetProcAddress(module, "wglSwapBuffers");

            int hr = minhook.MH_RemoveHook(proc);
            if (hr == 0) {
                hooked = false;
                LOGGER.info("opengl32.dll->wglSwapBuffers unhooked ({}, {})", proc, hr);
            } else {
                LOGGER.warn("opengl32.dll->wglSwapBuffers unhook failed while attempting to remove! Remove={}", hr);
            }
        }
    }

    public boolean isOBSHooked() {
        HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(
                new DWORD(TH32CS_SNAPMODULE.intValue() | TH32CS_SNAPMODULE32.intValue()),
                new DWORD(pid)
        );

        if (snapshot == WinBase.INVALID_HANDLE_VALUE) {
            return false;
        }

        MODULEENTRY32W module = new MODULEENTRY32W();
        if (!Kernel32.INSTANCE.Module32FirstW(snapshot, module)) {
            Kernel32.INSTANCE.CloseHandle(snapshot);
            return false;
        }

        do {
            String name = Native.toString(module.szModule);

            if (name.matches(obsDll)) {
                Kernel32.INSTANCE.CloseHandle(snapshot);
                return true;
            }
        } while (Kernel32.INSTANCE.Module32NextW(snapshot, module));

        Kernel32.INSTANCE.CloseHandle(snapshot);
        return false;
    }
}