package win.transgirls.streamproof.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;
import win.transgirls.streamproof.tools.StreamproofImpl;

import java.util.function.Consumer;

@Mixin(ScreenshotRecorder.class)
public class ScreenshotMixin {
    @WrapMethod(method = "takeScreenshot(Lnet/minecraft/client/gl/Framebuffer;ILjava/util/function/Consumer;)V")
    private static void wrapTakeScreenshot(Framebuffer framebuffer, int downscaleFactor, Consumer<NativeImage> callback, Operation<Void> original) {
        if (StreamproofAPI.isStreamproof("STREAMPROOF_SCREENSHOTS")) {
            original.call(framebuffer, downscaleFactor, callback);
        } else {
            if (Streamproof._global_impl instanceof StreamproofImpl impl && impl.isReady()) {
                impl.screenshotRunners.add(callback);
            }
        }
    }
}
