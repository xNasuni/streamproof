package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.Streamproof;

@Mixin(ChatHud.class)
public abstract class UnfocusedChatOverlay {
    @WrapMethod(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;IIIZZ)V")
    private void wrapRender(DrawContext guiGraphics, TextRenderer font, int i, int j, int k, boolean bl, boolean bl2, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(guiGraphics, (data) -> {
            original.call(data.graphics, font, i, j, k, bl, bl2);
        });
    }
}