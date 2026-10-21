package win.transgirls.streamproof.mixin.impl;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.types.RenderTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mixin(FeatureRenderDispatcher.PhaseSubmitGrouper.class)
public abstract class PhaseSubmitGrouperMixin {
    @Shadow
    private FeatureRenderDispatcher.PreparedGroup<?> lastGroup;

    @Shadow
    @Final
    private List<SubmitNode> allSubmits;

    @Unique
    private RenderTarget streamproof$lastTarget = null;

    @Unique
    private RenderTarget streamproof$pendingTarget = null;

    @WrapMethod(method = "acceptFeatureGroup")
    private <S extends SubmitNode> void splitFeatureGroup(FeatureRendererType<S> featureType, Collection<S> submits, boolean strictlyOrdered, Operation<Void> original) {
        boolean anyTagged = false;
        for (S submit : submits) {
            if (Streamproof.renderCommands.containsKey(submit)) {
                anyTagged = true;
                break;
            }
        }

        if (!anyTagged) {
            original.call(featureType, submits, strictlyOrdered);
            return;
        }

        if (strictlyOrdered) {
            List<S> run = new ArrayList<>();
            RenderTarget runTarget = null;
            for (S submit : submits) {
                RenderTarget target = Streamproof.renderCommands.get(submit);
                if (!run.isEmpty() && target != runTarget) {
                    original.call(featureType, run, true);
                    run = new ArrayList<>();
                }
                runTarget = target;
                run.add(submit);
            }
            if (!run.isEmpty()) {
                original.call(featureType, run, true);
            }
            return;
        }

        Map<RenderTarget, List<S>> byTarget = new LinkedHashMap<>();
        for (S submit : submits) {
            byTarget.computeIfAbsent(Streamproof.renderCommands.get(submit), k -> new ArrayList<>()).add(submit);
        }
        for (List<S> chunk : byTarget.values()) {
            original.call(featureType, chunk, false);
        }
    }

    @Inject(method = "addOrExtendGroup", at = @At("HEAD"))
    private void breakGroupOnTargetChange(FeatureRendererType<?> featureType, boolean strictlyOrdered, int fromInclusive, int toInclusive, CallbackInfo ci) {
        this.streamproof$pendingTarget = Streamproof.renderCommands.get(this.allSubmits.get(fromInclusive));
        if (this.lastGroup != null && this.streamproof$pendingTarget != this.streamproof$lastTarget) {
            this.lastGroup = null;
        }
    }

    @Inject(method = "addOrExtendGroup", at = @At("TAIL"))
    private void rememberGroupTarget(FeatureRendererType<?> featureType, boolean strictlyOrdered, int fromInclusive, int toInclusive, CallbackInfo ci) {
        this.streamproof$lastTarget = this.streamproof$pendingTarget;
    }
}