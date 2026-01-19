package win.transgirls.streamproof.types;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface MinHook extends StdCallLibrary {
    int MH_Initialize();

    int MH_CreateHook(Pointer method, GlSwapBuffers hook, PointerByReference origMethod);

    int MH_EnableHook(Pointer method);

    int MH_RemoveHook(Pointer method);

    interface GlSwapBuffers extends StdCallCallback {
        boolean callback(Pointer hDc);
    }
}