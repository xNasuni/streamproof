package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.profiler.TracyProfiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;

import java.util.List;

@Mixin({DummyProfiler.class, ProfilerSystem.class, TracyProfiler.class, Profiler.UnionProfiler.class})
public abstract class UkusArmorOverlay {
    @Inject(method = "push(Ljava/lang/String;)V", at = @At("TAIL"))
    private void tailPush(String location, CallbackInfo ci) {
        Profiler profiler = (Profiler) this;
        List<String> stack = Streamproof.stackFor(profiler);

        stack.add(location);
        if (location.equals("ukus-armor-hud")) {
            StreamproofAPI.begin("UKUS_ARMOR_HUD");
        }
    }

    @Inject(method = "pop()V", at = @At("TAIL"))
    private void wrapPop(CallbackInfo ci) {
        Profiler profiler = (Profiler) this;
        List<String> stack = Streamproof.stackFor(profiler);

        String location = stack.isEmpty() ? null : stack.removeLast();
        if ("ukus-armor-hud".equals(location)) {
            StreamproofAPI.end("UKUS_ARMOR_HUD");
        }
    }
}
