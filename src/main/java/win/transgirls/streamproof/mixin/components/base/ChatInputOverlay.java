package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.tools.ComponentKind;

@Mixin(ChatScreen.class)
public class ChatInputOverlay {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private void wrapSuperRender(ChatScreen screen, DrawContext graphics, int mouseX, int mouseY, float deltaTicks, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(Streamproof.settings.isStreamproof(ComponentKind.CHAT_INPUT_OVERLAY), graphics, (data) -> {
            original.call(screen, data.graphics, mouseX, mouseY, deltaTicks);
        });

    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;render(Lnet/minecraft/client/gui/DrawContext;II)V"))
    private void wrapSuggestionRender(ChatInputSuggestor instance, DrawContext graphics, int mouseX, int mouseY, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(Streamproof.settings.isStreamproof(ComponentKind.CHAT_INPUT_OVERLAY), graphics, (data) -> {
            original.call(instance, data.graphics, mouseX, mouseY);
        });
    }
}