package win.transgirls.streamproof.mixin.impl;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRendererMap;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.api.types.RenderTarget;

import java.util.List;

@Mixin(FeatureRenderDispatcher.PreparedGroup.class)
public abstract class PreparedGroupMixin {
    @Shadow
    @Final
    private int fromInclusive;

    @WrapMethod(method = "execute")
    private void wrapExecute(FeatureFrameContext context, FeatureRendererMap featureRenderers, List<SubmitNode> submits, Operation<Void> original) {
        RenderTarget target = submits.isEmpty() ? null : Streamproof.renderCommands.get(submits.get(this.fromInclusive));

        if (target != null) StreamproofAPI.start(target);
        try {
            original.call(context, featureRenderers, submits);
        } finally {
            if (target != null) StreamproofAPI.stop(target);
        }
    }
}