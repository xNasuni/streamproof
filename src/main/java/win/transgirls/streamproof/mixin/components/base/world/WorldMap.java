package win.transgirls.streamproof.mixin.components.base.world;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.MapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(MapRenderer.class)
public class WorldMap {
    @WrapMethod(method = "render")
    private void wrapDrawHeldMap(MapRenderState mapRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, boolean showOnlyFrame, int lightCoords, Operation<Void> original) {
        StreamproofAPI.begin("MAPS");
        original.call(mapRenderState, poseStack, submitNodeCollector, showOnlyFrame, lightCoords);
        StreamproofAPI.end("MAPS");
    }
}