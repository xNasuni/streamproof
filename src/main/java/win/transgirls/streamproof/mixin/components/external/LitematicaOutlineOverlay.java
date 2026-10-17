package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import fi.dy.masa.litematica.render.OverlayRenderer;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(OverlayRenderer.class)
public class LitematicaOutlineOverlay {
    @WrapMethod(method = "renderBoxes")
    private void wrapRenderBoxes(ProfilerFiller profiler, Operation<Void> original) {
        StreamproofAPI.beginImmediate("LITEMATICA_WORLD_OVERLAY");
        original.call(profiler);
        StreamproofAPI.endImmediate("LITEMATICA_WORLD_OVERLAY");
    }

    @WrapMethod(method = "renderSchematicVerifierMismatches")
    private void wrapRenderSchematicVerifierMismatches(ProfilerFiller profiler, Operation<Void> original) {
        StreamproofAPI.beginImmediate("LITEMATICA_WORLD_OVERLAY");
        original.call(profiler);
        StreamproofAPI.endImmediate("LITEMATICA_WORLD_OVERLAY");
    }

    @WrapMethod(method = "renderSchematicRebuildTargetingOverlay")
    private void wrapRenderSchematicRebuildTargetingOverlay(ProfilerFiller profiler, Operation<Void> original) {
        StreamproofAPI.beginImmediate("LITEMATICA_WORLD_OVERLAY");
        original.call(profiler);
        StreamproofAPI.endImmediate("LITEMATICA_WORLD_OVERLAY");
    }
}
