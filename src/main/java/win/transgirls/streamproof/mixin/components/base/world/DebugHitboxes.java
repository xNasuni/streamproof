package win.transgirls.streamproof.mixin.components.base.world;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.gizmo.GizmoDrawerImpl;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.tools.ComponentKind;

@Mixin(GizmoDrawerImpl.class)
public class DebugHitboxes {
    @WrapMethod(method = "draw")
    private void wrapGizmoDraw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CameraRenderState cameraRenderState, Matrix4f posMatrix, Operation<Void> original) throws CloneNotSupportedException {
        Streamproof.renderQueue.deferWorld(Streamproof.settings.isStreamproof(ComponentKind.DEBUG_HITBOXES), matrices, vertexConsumers, (data) -> {
            original.call(data.stack, data.buffers, cameraRenderState, posMatrix);
        });
    }
}