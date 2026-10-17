package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gl.GpuSampler;
import fi.dy.masa.litematica.render.schematic.OverlayRenderType;
import fi.dy.masa.litematica.render.schematic.WorldRendererSchematic;
import net.minecraft.client.render.BlockRenderLayerGroup;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.tools.IsolatedSubmitQueue;

import java.util.function.Consumer;

@Mixin(WorldRendererSchematic.class)
public class LitematicaWorldOverlay {
    @Unique
    private final IsolatedSubmitQueue isolatedQueue = new IsolatedSubmitQueue();

    @WrapMethod(method = "drawBlockLayerGroup")
    private void wrapDrawBlockLayerGroup(BlockRenderLayerGroup group, GpuSampler sampler, Operation<Void> original) {
        StreamproofAPI.beginImmediate("LITEMATICA_WORLD_OVERLAY");
        original.call(group, sampler);
        StreamproofAPI.endImmediate("LITEMATICA_WORLD_OVERLAY");
    }

    @WrapMethod(method = "renderBlockOverlay")
    private void wrapRenderBlockOverlay(OverlayRenderType type, Camera camera, float lineWidth, Profiler profiler, Operation<Void> original) {
        StreamproofAPI.beginImmediate("LITEMATICA_WORLD_OVERLAY");
        original.call(type, camera, lineWidth, profiler);
        StreamproofAPI.endImmediate("LITEMATICA_WORLD_OVERLAY");
    }

    @WrapMethod(method = "renderEntities")
    private void wrapRenderEntities(Camera camera, Frustum frustum, MatrixStack matrices, WorldRenderState renderStates, OrderedRenderCommandQueue queue, Profiler profiler, Operation<Void> original) {
        this.submitIsolated(queue, isolated -> original.call(camera, frustum, matrices, renderStates, isolated, profiler));
    }

    @WrapMethod(method = "renderBlockEntities")
    private void wrapRenderBlockEntities(Camera camera, Frustum frustum, MatrixStack matrices, WorldRenderState renderStates, OrderedRenderCommandQueue queue, Profiler profiler, Operation<Void> original) {
        this.submitIsolated(queue, isolated -> original.call(camera, frustum, matrices, renderStates, isolated, profiler));
    }

    @Unique
    private void submitIsolated(OrderedRenderCommandQueue queue, Consumer<OrderedRenderCommandQueue> submit) {
        if (!StreamproofAPI.isReady() || !StreamproofAPI.isStreamproof("LITEMATICA_WORLD_OVERLAY")) {
            submit.accept(queue);
            return;
        }

        submit.accept(this.isolatedQueue.storage());
        StreamproofAPI.beginImmediate("LITEMATICA_WORLD_OVERLAY");
        this.isolatedQueue.render();
        StreamproofAPI.endImmediate("LITEMATICA_WORLD_OVERLAY");
    }
}
