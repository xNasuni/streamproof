package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import fi.dy.masa.litematica.render.OverlayRenderer;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(OverlayRenderer.class)
public class LitematicaOutlineOverlay {
    @WrapMethod(method = "renderBoxes")
    private void wrapRenderBoxes(Matrix4f posMatrix, Matrix4f projMatrix, Profiler profiler, Operation<Void> original) {
        StreamproofAPI.beginImmediate("LITEMATICA_WORLD_OVERLAY");
        original.call(posMatrix, projMatrix, profiler);
        StreamproofAPI.endImmediate("LITEMATICA_WORLD_OVERLAY");
    }

    @WrapMethod(method = "renderSchematicVerifierMismatches")
    private void wrapRenderSchematicVerifierMismatches(Matrix4f posMatrix, Matrix4f projMatrix, Profiler profiler, Operation<Void> original) {
        StreamproofAPI.beginImmediate("LITEMATICA_WORLD_OVERLAY");
        original.call(posMatrix, projMatrix, profiler);
        StreamproofAPI.endImmediate("LITEMATICA_WORLD_OVERLAY");
    }

    @WrapMethod(method = "renderSchematicRebuildTargetingOverlay")
    private void wrapRenderSchematicRebuildTargetingOverlay(Matrix4f posMatrix, Matrix4f projMatrix, Profiler profiler, Operation<Void> original) {
        StreamproofAPI.beginImmediate("LITEMATICA_WORLD_OVERLAY");
        original.call(posMatrix, projMatrix, profiler);
        StreamproofAPI.endImmediate("LITEMATICA_WORLD_OVERLAY");
    }
}
