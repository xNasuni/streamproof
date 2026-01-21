package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.tools.ComponentKind;

@Mixin(ChatHud.class)
public class ChatMessagesOverlay {
    @WrapMethod(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;IIIZZ)V")
    private void wrapRender(DrawContext graphics, TextRenderer font, int i, int j, int k, boolean bl, boolean bl2, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(Streamproof.settings.isStreamproof(ComponentKind.CHAT_MESSAGES_OVERLAY), graphics, (data) -> {
            original.call(data.graphics, font, i, j, k, bl, bl2);
        });
    }
}