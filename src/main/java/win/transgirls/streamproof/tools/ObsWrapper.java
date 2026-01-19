package win.transgirls.streamproof.tools;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.PointerByReference;
import win.transgirls.streamproof.Streamproof;

import static com.sun.jna.platform.win32.WinNT.*;
import static com.sun.jna.platform.win32.Tlhelp32.*;

import win.transgirls.streamproof.types.Kernel32;
import win.transgirls.streamproof.types.MinHook;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static win.transgirls.streamproof.Streamproof.LOGGER;

public class ObsWrapper {
    private static final String obsDll = "(?i)^graphics-hook(32|64)\\.dll$";
    private final long pid;
    private ScheduledExecutorService obsDetector;
    private volatile boolean running = true;

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
        MinHook minhook = Streamproof.minhook;

        Pointer module = Kernel32.INSTANCE.GetModuleHandleA("opengl32.dll");
        Pointer proc = Kernel32.INSTANCE.GetProcAddress(module, "wglSwapBuffers");

        Streamproof.wglSwapBuffersObs = new PointerByReference();

        int hr = minhook.MH_CreateHook(proc, (hDc) -> {
            Function original = Function.getFunction(Streamproof.wglSwapBuffersObs.getValue(), Function.ALT_CONVENTION);

            if (Streamproof.renderSecrets != null) {
                Streamproof.renderSecrets.run();
            }

            return (boolean) original.invoke(Boolean.class, new Object[]{hDc});
        }, Streamproof.wglSwapBuffersObs);

        int hr2 = minhook.MH_EnableHook(proc);

        Streamproof.LOGGER.info("opengl32.dll->wglSwapBuffers hooked after OBS ({}, {}, {})",
                Streamproof.wglSwapBuffersObs.getValue(), hr, hr2);
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