package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CommandSuggestions;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;

@Mixin(CommandSuggestions.class)
public class FocusedChatSuggestionOverlay {
    @WrapMethod(method = "render")
    private void wrapRender(GuiGraphics guiGraphics, int i, int j, Operation<Void> original) {
        Streamproof.renderQueue.add((data) -> {
            original.call(data.graphics, i, j);
        });
    }
}