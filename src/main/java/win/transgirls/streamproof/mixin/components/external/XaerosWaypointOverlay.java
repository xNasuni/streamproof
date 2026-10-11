package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;
import xaero.hud.minimap.element.render.world.MinimapElementWorldRendererHandler;

@Mixin(MinimapElementWorldRendererHandler.class)
public class XaerosWaypointOverlay {
    @WrapMethod(method = "render")
    private void wrapWaypointRender(Vec3 renderPos, float partialTicks, RenderTarget framebuffer, double backgroundCoordinateScale, ResourceKey<Level> mapDimension, Operation<Void> original) {
        StreamproofAPI.beginImmediate("XAEROS_MINIMAP_WAYPOINTS");
        original.call(renderPos, partialTicks, framebuffer, backgroundCoordinateScale, mapDimension);
        StreamproofAPI.endImmediate("XAEROS_MINIMAP_WAYPOINTS");
    }
}