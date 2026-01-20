package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.gizmo.GizmoDrawerImpl;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;

@Mixin(GizmoDrawerImpl.class)
public class DebugHitboxes {
    @WrapMethod(method = "draw")
    private void wrapRender(MatrixStack poseStack, VertexConsumerProvider multiBufferSource, CameraRenderState cameraRenderState, Matrix4f matrix4f, Operation<Void> original) {
//        Streamproof.renderQueue.add((data) -> {
//            original.call(poseStack, data.buffer, cameraRenderState, matrix4f);
//        });
        original.call(poseStack, multiBufferSource, cameraRenderState, matrix4f);
    }
}