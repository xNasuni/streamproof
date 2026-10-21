package win.transgirls.streamproof.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.tools.StreamproofImpl;

import java.util.function.Consumer;
import net.minecraft.client.Screenshot;

@Mixin(Screenshot.class)
public class ScreenshotMixin {
    @WrapMethod(method = "takeScreenshot(Lcom/mojang/blaze3d/pipeline/RenderTarget;ILjava/util/function/Consumer;)V")
    private static void wrapTakeScreenshot(RenderTarget target, int downscaleFactor, Consumer<NativeImage> callback, Operation<Void> original) {
        if (StreamproofAPI.isStreamproof("STREAMPROOF_SCREENSHOTS")) {
            original.call(target, downscaleFactor, callback);
        } else {
            if (Streamproof._global_impl instanceof StreamproofImpl impl && impl.isReady()) {
                impl.screenshotRunners.add(callback);
            }
        }
    }
}
