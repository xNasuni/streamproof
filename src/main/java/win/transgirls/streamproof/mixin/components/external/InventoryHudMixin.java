package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dlovin.inventoryhud.gui.InventoryHUDGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import win.transgirls.streamproof.Streamproof;

@Mixin(InventoryHUDGui.class)
public class InventoryHudMixin {
    @WrapMethod(method = "renderArmor")
    private void wrapArmor(@Coerce Object guiGraphics, int width, int height, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(guiGraphics, (data) -> {
            original.call(data.graphics, width, height);
        });
    }

    @WrapMethod(method = "renderPotion")
    private void wrapPotion(@Coerce Object guiGraphics, int width, int height, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(guiGraphics, (data) -> {
            original.call(data.graphics, width, height);
        });
    }

    @WrapMethod(method = "renderInventory")
    private void wrapInventory(@Coerce Object guiGraphics, @Coerce Object deltaTracker, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(guiGraphics, (data) -> {
            original.call(data.graphics, deltaTracker);
        });
    }
}