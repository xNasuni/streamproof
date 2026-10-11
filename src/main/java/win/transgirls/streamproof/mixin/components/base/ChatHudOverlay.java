package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(ChatComponent.class)
public class ChatHudOverlay {
    @WrapMethod(method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V")
    private void wrapRender(ChatComponent.ChatGraphicsAccess graphics, int screenHeight, int ticks, ChatComponent.DisplayMode displayMode, Operation<Void> original) {
        StreamproofAPI.begin("CHAT_MESSAGES_OVERLAY");
        original.call(graphics, screenHeight, ticks, displayMode);
        StreamproofAPI.end("CHAT_MESSAGES_OVERLAY");
    }
}