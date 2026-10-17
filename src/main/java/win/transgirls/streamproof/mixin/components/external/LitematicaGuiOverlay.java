package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import fi.dy.masa.litematica.event.RenderHandler;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(RenderHandler.class)
public class LitematicaGuiOverlay {
    @WrapMethod(method = "onRenderGameOverlayPostAdvanced")
    private void wrapRenderGameOverlayPost(@Coerce Object ctx, float partialTicks, Profiler profiler, Operation<Void> original) {
        StreamproofAPI.begin("LITEMATICA_GUI_OVERLAY");
        original.call(ctx, partialTicks, profiler);
        StreamproofAPI.end("LITEMATICA_GUI_OVERLAY");
    }
}
