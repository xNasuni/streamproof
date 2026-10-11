package win.transgirls.streamproof.mixin.components.base.world;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.tools.StreamproofImpl;

@Mixin(DrawableGizmoPrimitives.class)
public class DebugHitboxes {
    @WrapMethod(method = "render")
    private void wrapGizmoDraw(PoseStack poseStack, MultiBufferSource bufferSource, net.minecraft.client.renderer.state.level.CameraRenderState camera, Matrix4fc modelViewMatrix, Operation<Void> original) {
        boolean isStreamproof = StreamproofAPI.isReady() && StreamproofAPI.isStreamproof("DEBUG_HITBOXES");

        if (isStreamproof) {
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.forceDraw(bufferSource);
            }
            StreamproofAPI.start(StreamproofAPI.getRenderTarget("DEBUG_HITBOXES"));
        }

        original.call(poseStack, bufferSource, camera, modelViewMatrix);

        if (isStreamproof) {
            if (StreamproofAPI.getImpl() instanceof StreamproofImpl impl) {
                impl.forceDraw(bufferSource);
            }
            StreamproofAPI.stop(StreamproofAPI.getRenderTarget("DEBUG_HITBOXES"));
        }
    }
}