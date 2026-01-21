package win.transgirls.streamproof.tools;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.Tlhelp32.MODULEENTRY32W;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.PointerByReference;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameModeSwitcherScreen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.text.Text;
import win.transgirls.streamproof.Streamproof;

import static com.sun.jna.platform.win32.Tlhelp32.*;

import win.transgirls.streamproof.imgui.ImGuiImplementation;
import win.transgirls.streamproof.types.Kernel32;
import win.transgirls.streamproof.types.ResultToast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static win.transgirls.streamproof.Streamproof.LOGGER;
import static win.transgirls.streamproof.Streamproof.minhook;

public class ObsWrapper {
    private static final String obsDll = "(?i)^graphics-hook(32|64)\\.dll$";
    private final long pid;

    public volatile boolean running = true;
    public boolean hooked = false;
    private ScheduledExecutorService obsDetector;

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

        int hr = minhook.MH_CreateHook(proc, (hDc) -> {
            Function original = Function.getFunction(Streamproof.wglSwapBuffersObs.getValue(), Function.ALT_CONVENTION);

            if (Streamproof.client.currentScreen == null || Streamproof.client.currentScreen instanceof ChatScreen || Streamproof.client.currentScreen instanceof GameModeSwitcherScreen) {
                try {
                    if (Streamproof.renderWorldSecrets != null) {
                        Streamproof.renderWorldSecrets.run();
                    }
                } catch (Throwable e) {
                    LOGGER.error("Streamproof failed to render world secrets", e);
                }

                try {
                    if (Streamproof.renderGuiSecrets != null) {
                        Streamproof.renderGuiSecrets.run();
                    }
                } catch (Throwable e) {
                    LOGGER.error("Streamproof failed to render gui secrets", e);
                }
            }

            try {
                if (ImGuiImplementation.initialized) {
                    ImGuiImplementation.draw();
                }
            } catch (Throwable e) {
                LOGGER.error("Streamproof failed to render imgui", e);
            }

            return (boolean) original.invoke(Boolean.class, new Object[]{hDc});
        }, Streamproof.wglSwapBuffersObs);

        int hr2 = minhook.MH_EnableHook(proc);

        if (hr == 0 && hr2 == 0) {
            hooked = true;
            Streamproof.client.getSoundManager().play(PositionedSoundInstance.ui(Streamproof.successSound, 1.0f));
            Streamproof.client.getToastManager().add(new ResultToast(Text.literal("OBS was successfully hooked"), true));
            LOGGER.info("opengl32.dll->wglSwapBuffers hooked after OBS ({}, {}, {})",
                    Streamproof.wglSwapBuffersObs.getValue(), hr, hr2);
        } else {
            Streamproof.client.getSoundManager().play(PositionedSoundInstance.ui(Streamproof.failureSound, 1.0f));
            Streamproof.client.getToastManager().add(new ResultToast(Text.literal("OBS failed to hook"), false));
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