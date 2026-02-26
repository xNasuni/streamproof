package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dlovin.inventoryhud.gui.InventoryHUDGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(InventoryHUDGui.class)
public class InventoryHudOverlay {
    @WrapMethod(method = "renderArmor")
    private void wrapArmor(@Coerce Object graphics, int width, int height, Operation<Void> original) {
        StreamproofAPI.begin("INVENTORY_HUD_ARMOR");
        original.call(graphics, width, height);
        StreamproofAPI.end("INVENTORY_HUD_ARMOR");
    }

    @WrapMethod(method = "renderPotion")
    private void wrapPotion(@Coerce Object graphics, int width, int height, Operation<Void> original) {
        StreamproofAPI.begin("INVENTORY_HUD_POTION");
        original.call(graphics, width, height);
        StreamproofAPI.end("INVENTORY_HUD_POTION");
    }

    @WrapMethod(method = "renderInventory")
    private void wrapInventory(@Coerce Object graphics, @Coerce Object deltaTracker, Operation<Void> original) {
        StreamproofAPI.begin("INVENTORY_HUD_INVENTORY");
        original.call(graphics, deltaTracker);
        StreamproofAPI.end("INVENTORY_HUD_INVENTORY");
    }
}