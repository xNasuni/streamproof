package win.transgirls.streamproof.input;


import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;

public class KeyboardMain {
    public static boolean passKeyboardInput = true;
    protected static ArrayList<MinecraftKeybind> listeners = new ArrayList<>();

    public static void keyDown(int key) {
        for (MinecraftKeybind keybind : listeners) {
            if (keybind.is(key)) {
                keybind.press();
            }
        }
    }

    public static void keyUp(int key) {
        for (MinecraftKeybind keybind : listeners) {
            if (keybind.is(key)) {
                keybind.release();
            }
        }
    }

    public static void on(MinecraftKeybind keybind) {
        listeners.add(keybind);
    }
}