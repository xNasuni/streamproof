package win.transgirls.streamproof.tools;


import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import net.minecraft.client.Minecraft;

import static com.sun.jna.platform.win32.WinNT.*;
import static com.sun.jna.platform.win32.Tlhelp32.*;

public class ObsWindow {
    private static final String obsDll = "graphics-hook64.dll";
    private Minecraft client;
    private long pid;

    public ObsWindow(Minecraft client) {
        this.client = client;
        this.pid = ProcessHandle.current().pid();
        this.isOBSHooked();
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

            if (name.equalsIgnoreCase(obsDll)) {
                Kernel32.INSTANCE.CloseHandle(snapshot);
                return true;
            }
        } while (Kernel32.INSTANCE.Module32NextW(snapshot, module));

        Kernel32.INSTANCE.CloseHandle(snapshot);
        return false;
    }
}