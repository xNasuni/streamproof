package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;
import xaero.hud.minimap.element.render.world.MinimapElementWorldRendererHandler;

@Mixin(MinimapElementWorldRendererHandler.class)
public class XaerosWaypointOverlay {
    @WrapMethod(method = "render")
    private void wrapWaypointRender(Vec3d renderPos, float partialTicks, Framebuffer framebuffer, double backgroundCoordinateScale, RegistryKey<World> mapDimension, Operation<Void> original) {
        StreamproofAPI.beginImmediate("XAEROS_MINIMAP_WAYPOINTS");
        original.call(renderPos, partialTicks, framebuffer, backgroundCoordinateScale, mapDimension);
        StreamproofAPI.endImmediate("XAEROS_MINIMAP_WAYPOINTS");
    }
}