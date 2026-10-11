package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SubtitleOverlay;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(SubtitleOverlay.class)
public class SubtitlesOverlay {
    @WrapMethod(method = "extractRenderState")
    private void wrapRenderSubtitles(GuiGraphicsExtractor graphics, Operation<Void> original) {
        StreamproofAPI.begin("SUBTITLES");
        original.call(graphics);
        StreamproofAPI.end("SUBTITLES");
    }
}