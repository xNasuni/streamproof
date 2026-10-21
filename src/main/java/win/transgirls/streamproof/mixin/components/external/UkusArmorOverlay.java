package win.transgirls.streamproof.mixin.components.external;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(value = Hud.class, priority = 1500)
public abstract class UkusArmorOverlay {
    @TargetHandler(
            mixin = "ru.berdinskiybear.armorhud.mixin.MixinHud",
            name = "drawArmorHud"
    )
    @WrapMethod(method = "@MixinSquared:Handler")
    private void wrapDrawArmorHud(GuiGraphicsExtractor graphics, DeltaTracker tickCounter, Operation<Void> original) {
        StreamproofAPI.begin("UKUS_ARMOR_HUD");
        original.call(graphics, tickCounter);
        StreamproofAPI.end("UKUS_ARMOR_HUD");
    }
}
