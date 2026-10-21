package win.transgirls.streamproof.mixin.components.base.world;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(DrawableGizmoPrimitives.class)
public class DebugHitboxes {
    @WrapMethod(method = "submit")
    private void wrapGizmoSubmit(SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, boolean onTop, Operation<Void> original) {
        StreamproofAPI.begin("DEBUG_HITBOXES");
        try {
            original.call(submitNodeCollector, cameraRenderState, onTop);
        } finally {
            StreamproofAPI.end("DEBUG_HITBOXES");
        }
    }
}
