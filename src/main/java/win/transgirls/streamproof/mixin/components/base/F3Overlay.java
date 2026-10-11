package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(DebugScreenOverlay.class)
public class F3Overlay {
    @WrapMethod(method = "extractRenderState")
    private void wrapRender(GuiGraphicsExtractor graphics, Operation<Void> original) {
        StreamproofAPI.begin("F3_OVERLAY");
        original.call(graphics);
        StreamproofAPI.end("F3_OVERLAY");
    }
}