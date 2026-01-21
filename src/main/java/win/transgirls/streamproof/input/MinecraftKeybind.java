package win.transgirls.streamproof.input;

import net.minecraft.client.option.KeyBinding;

import java.util.function.Consumer;

public class MinecraftKeybind {
    private final KeyBinding binding;
    private final Consumer<KeyAction> listener;

    public MinecraftKeybind(KeyBinding binding, Consumer<KeyAction> listener) {
        this.binding = binding;
        this.listener = listener;
    }

    public boolean is(int code) {
        return this.binding.boundKey.getCode() == code;
    }

    public void press() {
        this.listener.accept(KeyAction.PRESSING);
    }

    public void release() {
        this.listener.accept(KeyAction.RELEASING);
    }
}