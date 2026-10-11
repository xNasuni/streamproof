package win.transgirls.streamproof.input;

import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;

public class MinecraftKeybind {
    private final KeyMapping binding;
    private final Consumer<KeyAction> listener;

    public MinecraftKeybind(KeyMapping binding, Consumer<KeyAction> listener) {
        this.binding = binding;
        this.listener = listener;
    }

    public boolean is(int code) {
        return this.binding.key.getValue() == code;
    }

    public void press() {
        this.listener.accept(KeyAction.PRESSING);
    }

    public void release() {
        this.listener.accept(KeyAction.RELEASING);
    }
}