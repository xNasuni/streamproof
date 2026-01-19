package win.transgirls.streamproof.types;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {
    static Kernel32 INSTANCE = Native.load("Kernel32", Kernel32.class);

    Pointer GetModuleHandleA(String moduleName);

    Pointer GetProcAddress(Pointer hModule, String procName);

    WinNT.HANDLE CreateToolhelp32Snapshot(WinDef.DWORD dwFlags, WinDef.DWORD th32ProcessID);

    boolean Module32FirstW(WinNT.HANDLE hSnapshot, Tlhelp32.MODULEENTRY32W lpme);

    boolean CloseHandle(WinNT.HANDLE hObject);

    boolean Module32NextW(WinNT.HANDLE hSnapshot, Tlhelp32.MODULEENTRY32W lpme);
}