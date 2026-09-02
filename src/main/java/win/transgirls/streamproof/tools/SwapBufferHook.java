package win.transgirls.streamproof.tools;

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.text.Text;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.types.Kernel32;
import win.transgirls.streamproof.types.MinHook;
import win.transgirls.streamproof.types.ResultToast;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static win.transgirls.streamproof.Streamproof.mc;
import static win.transgirls.streamproof.Streamproof.LOGGER;
import static win.transgirls.streamproof.Streamproof.minhook;

public class SwapBufferHook {
    private static final int signatureLength = 16;

    public volatile boolean running = true;
    public boolean hooked = false;

    private Pointer target;
    private final AtomicInteger hookCount = new AtomicInteger();
    private byte[] signature;
    private ScheduledExecutorService guardian;
    @SuppressWarnings("FieldCanBeLocal")
    private final MinHook.GlSwapBuffers hookCallback;

    public SwapBufferHook() {
        this.hookCallback = (hDc) -> {
            Function original = Function.getFunction(Streamproof.wglSwapBuffersObs.getValue(), Function.ALT_CONVENTION);
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.composite();
            }

            return (boolean) original.invoke(Boolean.class, new Object[]{hDc});
        };

        guardian = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Streamproof Hook Guardian");
            t.setDaemon(true);
            return t;
        });

        resolveTarget();
    }

    private void resolveTarget() {
        if (!running) return;

        Pointer module = Kernel32.INSTANCE.GetModuleHandleA("opengl32.dll");
        Pointer candidate = module == null ? null : Kernel32.INSTANCE.GetProcAddress(module, "wglSwapBuffers");

        if (candidate == null) {
            guardian.schedule(this::resolveTarget, 250, TimeUnit.MILLISECONDS);
            return;
        }

        target = candidate;
        install();
        startGuardian();
    }

    private void install() {
        Streamproof.wglSwapBuffersObs = new PointerByReference();

        int hr = minhook.MH_CreateHook(target, this.hookCallback, Streamproof.wglSwapBuffersObs);
        int hr2 = minhook.MH_EnableHook(target);

        boolean success = hr == 0 && hr2 == 0;
        hooked = success;

        if (success) {
            signature = target.getByteArray(0, signatureLength);
            LOGGER.info("opengl32.dll->wglSwapBuffers hooked ({}, {}, {})", Streamproof.wglSwapBuffersObs.getValue(), hr, hr2);
        } else {
            LOGGER.warn("opengl32.dll->wglSwapBuffers hook failed while attempting to create or enable! Create={}, Enable={}", hr, hr2);
        }

        announce(success, hookCount.incrementAndGet());
    }

    private void announce(boolean success, int count) {
        String message = success
                ? count == 1 ? "Streamproof hooked wglSwapBuffers" : "Streamproof reclaimed wglSwapBuffers (x" + count + ")"
                : "Streamproof failed to hook wglSwapBuffers";

        mc.execute(() -> {
            mc.getSoundManager().play(PositionedSoundInstance.ui(success ? Streamproof.successSound : Streamproof.failureSound, 1.0f));
            mc.getToastManager().add(new ResultToast(Text.literal(message), success));
        });
    }

    private void startGuardian() {
        guardian.scheduleWithFixedDelay(() -> {
            if (!running) {
                guardian.shutdown();
                return;
            }

            if (hooked && !Arrays.equals(target.getByteArray(0, signatureLength), signature)) {
                LOGGER.info("wglSwapBuffers hook was overwritten by another process, reclaiming...");
                reclaim();
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    private void reclaim() {
        minhook.MH_DisableHook(target);
        minhook.MH_RemoveHook(target);
        install();
    }

    public void stop() {
        running = false;

        if (hooked) {
            minhook.MH_DisableHook(target);
            int hr = minhook.MH_RemoveHook(target);
            if (hr == 0) {
                hooked = false;
                LOGGER.info("opengl32.dll->wglSwapBuffers unhooked ({})", hr);
            } else {
                LOGGER.warn("opengl32.dll->wglSwapBuffers unhook failed while attempting to remove! Remove={}", hr);
            }
        }

        if (guardian != null) {
            guardian.shutdown();
        }
    }
}
