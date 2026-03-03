package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.SubtitlesHud;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(SubtitlesHud.class)
public class SubtitlesOverlay {
    @WrapMethod(method = "render")
    private void wrapRenderSubtitles(DrawContext context, Operation<Void> original) {
        StreamproofAPI.begin("SUBTITLES");
        original.call(context);
        StreamproofAPI.end("SUBTITLES");
    }
}