package win.transgirls.streamproof.mixin.components.external;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(value = InGameHud.class, priority = 1500)
public abstract class UkusArmorOverlay {
    @TargetHandler(
            mixin = "ru.berdinskiybear.armorhud.mixin.MixinGui",
            name = "drawArmorHud"
    )
    @WrapMethod(method = "@MixinSquared:Handler")
    private void wrapDrawArmorHud(DrawContext graphics, RenderTickCounter tickCounter, Operation<Void> original) {
        StreamproofAPI.begin("UKUS_ARMOR_HUD");
        original.call(graphics, tickCounter);
        StreamproofAPI.end("UKUS_ARMOR_HUD");
    }
}
