package win.transgirls.streamproof.tools;

import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.function.Consumer;

public class RenderQueue {
    ArrayList<Consumer<GuiGraphics>> renderQueue = new ArrayList<>();

    public int size() {
        return renderQueue.size();
    }

    public void add(Consumer<GuiGraphics> runnable) {
        renderQueue.add(runnable);
    }

    public void release(GuiGraphics graphics) {
        renderQueue.forEach((runnable) -> {
            runnable.accept(graphics);
        });
        renderQueue.clear();
    }
}