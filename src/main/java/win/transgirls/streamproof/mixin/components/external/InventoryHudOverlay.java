package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dlovin.inventoryhud.gui.InventoryHUDGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.tools.ComponentKind;

@Mixin(InventoryHUDGui.class)
public class InventoryHudOverlay {
    @WrapMethod(method = "renderArmor")
    private void wrapArmor(@Coerce Object graphics, int width, int height, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(Streamproof.settings.isStreamproof(ComponentKind.INVENTORY_HUD_ARMOR), graphics, (data) -> {
            original.call(data.graphics, width, height);
        });
    }

    @WrapMethod(method = "renderPotion")
    private void wrapPotion(@Coerce Object graphics, int width, int height, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(Streamproof.settings.isStreamproof(ComponentKind.INVENTORY_HUD_POTION), graphics, (data) -> {
            original.call(data.graphics, width, height);
        });
    }

    @WrapMethod(method = "renderInventory")
    private void wrapInventory(@Coerce Object graphics, @Coerce Object deltaTracker, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(Streamproof.settings.isStreamproof(ComponentKind.INVENTORY_HUD_INVENTORY), graphics, (data) -> {
            original.call(data.graphics, deltaTracker);
        });
    }
}