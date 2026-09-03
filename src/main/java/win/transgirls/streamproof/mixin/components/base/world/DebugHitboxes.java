package win.transgirls.streamproof.mixin.components.base.world;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.gizmo.GizmoDrawerImpl;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.tools.StreamproofImpl;

@Mixin(GizmoDrawerImpl.class)
public class DebugHitboxes {
    @WrapMethod(method = "draw")
    private void wrapGizmoDraw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CameraRenderState cameraRenderState, Matrix4f posMatrix, Operation<Void> original) {
        boolean isStreamproof = StreamproofAPI.isReady() && StreamproofAPI.isStreamproof("DEBUG_HITBOXES");

        if (isStreamproof) {
            StreamproofAPI.start(StreamproofAPI.getRenderTarget("DEBUG_HITBOXES"));
        }

        original.call(matrices, vertexConsumers, cameraRenderState, posMatrix);

        if (isStreamproof) {
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.forceDraw(vertexConsumers);
            }
            StreamproofAPI.stop(StreamproofAPI.getRenderTarget("DEBUG_HITBOXES"));
        }
    }
}