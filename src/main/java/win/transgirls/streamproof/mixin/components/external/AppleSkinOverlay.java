package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

import squeek.appleskin.client.HUDOverlayHandler;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(HUDOverlayHandler.class)
public class AppleSkinOverlay {
    @WrapMethod(method = "onPreRenderFood")
    private void wrapPreRenderFood(DrawContext context, PlayerEntity player, int top, int right, Operation<Void> original) {
        StreamproofAPI.begin("APPLE_SKIN_OVERLAY");
        original.call(context, player, top, right);
        StreamproofAPI.end("APPLE_SKIN_OVERLAY");
    }

    @WrapMethod(method = "onRenderFood")
    private void wrapRenderFood(DrawContext context, PlayerEntity player, int top, int right, Operation<Void> original) {
        StreamproofAPI.begin("APPLE_SKIN_OVERLAY");
        original.call(context, player, top, right);
        StreamproofAPI.end("APPLE_SKIN_OVERLAY");
    }

    @WrapMethod(method = "onRenderHealth")
    private void wrapRenderHealth(DrawContext context, PlayerEntity player, int left, int top, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, Operation<Void> original) {
        StreamproofAPI.begin("APPLE_SKIN_OVERLAY");
        original.call(context, player, left, top, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking);
        StreamproofAPI.end("APPLE_SKIN_OVERLAY");
    }
}