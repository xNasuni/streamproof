package win.transgirls.streamproof.mixin.components;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;
import net.minecraft.client.gui.components.DebugScreenOverlay;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class F3Screen {
    @WrapMethod(method = "renderLines")
    private void wrapRender(GuiGraphics guiGraphics, List<String> list, boolean bl, Operation<Void> original) {
        Streamproof.renderQueue.add((graphics) -> {
//            original.call(graphics, list, bl);
            original.call(guiGraphics, list, bl);
        });
    }
}