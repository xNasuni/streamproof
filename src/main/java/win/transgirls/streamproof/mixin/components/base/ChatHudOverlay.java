package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(ChatHud.class)
public class ChatHudOverlay {
    @WrapMethod(method = "render(Lnet/minecraft/client/gui/hud/ChatHud$Backend;IIZ)V")
    private void wrapRender(ChatHud.Backend drawer, int windowHeight, int currentTick, boolean expanded, Operation<Void> original) {
        StreamproofAPI.begin("CHAT_MESSAGES_OVERLAY");
        original.call(drawer, windowHeight, currentTick, expanded);
        StreamproofAPI.end("CHAT_MESSAGES_OVERLAY");
    }
}