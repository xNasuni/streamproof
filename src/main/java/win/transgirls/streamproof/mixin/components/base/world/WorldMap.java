package win.transgirls.streamproof.mixin.components.base.world;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(MapRenderer.class)
public class WorldMap {
    @WrapMethod(method = "draw")
    private void wrapDrawHeldMap(MapRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, boolean renderDecorations, int light, Operation<Void> original) {
        StreamproofAPI.begin("MAPS");
        original.call(state, matrices, queue, renderDecorations, light);
        StreamproofAPI.end("MAPS");
    }
}