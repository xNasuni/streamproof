package win.transgirls.streamproof.types;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface MinHook extends StdCallLibrary {
    int MH_Initialize();

    int MH_CreateHook(Pointer method, wglSwapBuffers hook, PointerByReference origMethod);

    int MH_EnableHook(Pointer method);

    public static interface wglSwapBuffers extends StdCallCallback {
        boolean callback(Pointer hDc);
    }
}