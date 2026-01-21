package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;

import java.util.List;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import win.transgirls.streamproof.tools.ComponentKind;

@Mixin(DebugHud.class)
public class F3Overlay {
    @WrapMethod(method = "drawText")
    private void wrapRender(DrawContext graphics, List<String> list, boolean bl, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(Streamproof.settings.isStreamproof(ComponentKind.F3_OVERLAY), graphics, (data) -> {
            original.call(data.graphics, list, bl);
        });
    }
}