package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.vertex.PoseStack;
import fi.dy.masa.litematica.render.schematic.OverlayRenderType;
import fi.dy.masa.litematica.render.schematic.WorldRendererSchematic;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import win.transgirls.streamproof.api.StreamproofAPI;

import java.util.function.Consumer;

@Mixin(WorldRendererSchematic.class)
public class LitematicaWorldOverlay {
    @WrapMethod(method = "drawBlockLayerGroup")
    private void wrapDrawBlockLayerGroup(ChunkSectionLayerGroup group, GpuSampler sampler, Operation<Void> original) {
        StreamproofAPI.beginImmediate("LITEMATICA_WORLD_OVERLAY");
        original.call(group, sampler);
        StreamproofAPI.endImmediate("LITEMATICA_WORLD_OVERLAY");
    }

    @WrapMethod(method = "renderBlockOverlay")
    private void wrapRenderBlockOverlay(OverlayRenderType type, Camera camera, float lineWidth, ProfilerFiller profiler, Operation<Void> original) {
        StreamproofAPI.beginImmediate("LITEMATICA_WORLD_OVERLAY");
        original.call(type, camera, lineWidth, profiler);
        StreamproofAPI.endImmediate("LITEMATICA_WORLD_OVERLAY");
    }

    @WrapMethod(method = "renderEntities")
    private void wrapRenderEntities(Camera camera, Frustum frustum, PoseStack matrices, LevelRenderState renderStates, SubmitNodeCollector queue, ProfilerFiller profiler, Operation<Void> original) {
        this.submitIsolated(queue, isolated -> original.call(camera, frustum, matrices, renderStates, isolated, profiler));
    }

    @WrapMethod(method = "renderBlockEntities")
    private void wrapRenderBlockEntities(Camera camera, Frustum frustum, PoseStack matrices, LevelRenderState renderStates, SubmitNodeCollector queue, ProfilerFiller profiler, Operation<Void> original) {
        this.submitIsolated(queue, isolated -> original.call(camera, frustum, matrices, renderStates, isolated, profiler));
    }

    @Unique
    private void submitIsolated(SubmitNodeCollector queue, Consumer<SubmitNodeCollector> submit) {
        StreamproofAPI.begin("LITEMATICA_WORLD_OVERLAY");
        try {
            submit.accept(queue);
        } finally {
            StreamproofAPI.end("LITEMATICA_WORLD_OVERLAY");
        }
    }
}
