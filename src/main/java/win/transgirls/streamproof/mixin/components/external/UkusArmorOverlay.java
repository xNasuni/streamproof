package win.transgirls.streamproof.mixin.components.external;

import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.TracyZoneFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;

import java.util.List;

@Mixin({InactiveProfiler.class, ActiveProfiler.class, TracyZoneFiller.class, ProfilerFiller.CombinedProfileFiller.class})
public abstract class UkusArmorOverlay {
    @Inject(method = "push(Ljava/lang/String;)V", at = @At("TAIL"))
    private void tailPush(String name, CallbackInfo ci) {
        ProfilerFiller profiler = (ProfilerFiller) this;
        List<String> stack = Streamproof.stackFor(profiler);

        stack.add(name);
        if (name.equals("ukus-armor-hud")) {
            StreamproofAPI.begin("UKUS_ARMOR_HUD");
        }
    }

    @Inject(method = "pop()V", at = @At("TAIL"))
    private void wrapPop(CallbackInfo ci) {
        ProfilerFiller profiler = (ProfilerFiller) this;
        List<String> stack = Streamproof.stackFor(profiler);

        String location = stack.isEmpty() ? null : stack.removeLast();
        if ("ukus-armor-hud".equals(location)) {
            StreamproofAPI.end("UKUS_ARMOR_HUD");
        }
    }
}
