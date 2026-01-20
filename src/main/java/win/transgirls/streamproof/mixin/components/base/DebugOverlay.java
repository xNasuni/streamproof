package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;

import java.util.List;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;

@Mixin(DebugHud.class)
public class DebugOverlay {
    @WrapMethod(method = "drawText")
    private void wrapRender(DrawContext guiGraphics, List<String> list, boolean bl, Operation<Void> original) {
        Streamproof.renderQueue.add(guiGraphics, (data) -> {
            original.call(data.graphics, list, bl);
        });
    }
}