package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;

@Mixin(ChatInputSuggestor.class)
public class FocusedChatSuggestionOverlay {
    @WrapMethod(method = "render")
    private void wrapRender(DrawContext guiGraphics, int i, int j, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(guiGraphics, (data) -> {
            original.call(data.graphics, i, j);
        });
    }
}