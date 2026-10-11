package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(ChatScreen.class)
public class ChatInputOverlay {
    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    private void headRender(GuiGraphicsExtractor instance, int x0, int y0, int x1, int y1, int col, Operation<Void> original) {
        StreamproofAPI.begin("CHAT_INPUT_OVERLAY");
        original.call(instance, x0, y0, x1, y1, col);
        StreamproofAPI.end("CHAT_INPUT_OVERLAY");
        StreamproofAPI.begin("CHAT_MESSAGES_OVERLAY");
    }

    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V"))
    private void wrapRender(ChatComponent instance, GuiGraphicsExtractor graphics, Font font, int ticks, int mouseX, int mouseY, ChatComponent.DisplayMode displayMode, boolean changeCursorOnInsertions, Operation<Void> original) {
        original.call(instance, graphics, font, ticks, mouseX, mouseY, displayMode, changeCursorOnInsertions);
        StreamproofAPI.end("CHAT_MESSAGES_OVERLAY");
        StreamproofAPI.begin("CHAT_INPUT_OVERLAY");
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void returnRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        StreamproofAPI.end("CHAT_INPUT_OVERLAY");
    }
}