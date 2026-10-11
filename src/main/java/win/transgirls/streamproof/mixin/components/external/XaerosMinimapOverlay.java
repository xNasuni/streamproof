package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;
import xaero.common.events.ModClientEvents;

@Mixin(ModClientEvents.class)
public class XaerosMinimapOverlay {
    @WrapMethod(method = "handleRenderModOverlay")
    private void wrapRenderMinimap(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
        StreamproofAPI.begin("XAEROS_MINIMAP_MINIMAP");
        original.call(guiGraphics, deltaTracker);
        StreamproofAPI.end("XAEROS_MINIMAP_MINIMAP");
    }
}