package win.transgirls.streamproof.input;


import java.util.ArrayList;

public class KeyboardMain {
    public static boolean passKeyboardInput = true;
    protected static ArrayList<Keybind> listeners = new ArrayList<>();

    public static void keyDown(int key) {
        for (Keybind listener : listeners) {
            if (listener.is(key)) {
                listener.press();
            }
        }
    }

    public static void keyUp(int key) {
        for (Keybind listener : listeners) {
            if (listener.is(key)) {
                listener.release();
            }
        }
    }

    public static void on(Keybind keybind) {
        listeners.add(keybind);
    }
}