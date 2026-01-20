package win.transgirls.streamproof.input;

public class Keybind {
    public static Keybind none = new Keybind(KeyAction.PRESSING, 0, () -> {
    });
    private final KeyAction action;
    private final Runnable listener;
    private int code;

    public Keybind(KeyAction action, int code, Runnable listener) {
        this.action = action;
        this.code = code;
        this.listener = listener;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean is(int code) {
        return this.code == code;
    }

    public void press() {
        if (this.action == KeyAction.PRESSING) {
            this.listener.run();
        }
    }

    public void release() {
        if (this.action == KeyAction.RELEASING) {
            this.listener.run();
        }
    }
}