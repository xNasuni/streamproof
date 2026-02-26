package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(ChatScreen.class)
public class ChatInputOverlay {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    private void headRender(DrawContext instance, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
        StreamproofAPI.begin("CHAT_INPUT_OVERLAY");
        original.call(instance, x1, y1, x2, y2, color);
        StreamproofAPI.end("CHAT_INPUT_OVERLAY");
        StreamproofAPI.begin("CHAT_MESSAGES_OVERLAY");
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;IIIZZ)V"))
    private void wrapRender(ChatHud instance, DrawContext context, TextRenderer textRenderer, int currentTick, int mouseX, int mouseY, boolean interactable, boolean bl, Operation<Void> original) {
        original.call(instance, context, textRenderer, currentTick, mouseX, mouseY, interactable, bl);
        StreamproofAPI.end("CHAT_MESSAGES_OVERLAY");
        StreamproofAPI.begin("CHAT_INPUT_OVERLAY");
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void returnRender(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        StreamproofAPI.end("CHAT_INPUT_OVERLAY");
    }
}