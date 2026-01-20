package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;

@Mixin(TextFieldWidget.class)
public class FocusedChatEditOverlay {
    @WrapMethod(method = "renderWidget")
    private void wrapRender(DrawContext guiGraphics, int i, int j, float f, Operation<Void> original) {
        Streamproof.renderQueue.add(guiGraphics, (data) -> {
            original.call(data.graphics, i, j, f);
        });
    }
}