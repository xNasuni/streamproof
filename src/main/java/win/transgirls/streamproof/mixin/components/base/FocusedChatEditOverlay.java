package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;

@Mixin(EditBox.class)
public class FocusedChatEditOverlay {
    @WrapMethod(method = "renderWidget")
    private void wrapRender(GuiGraphics guiGraphics, int i, int j, float f, Operation<Void> original) {
        Streamproof.renderQueue.add((data) -> {
            original.call(data.graphics, i, j, f);
        });
    }
}