package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dlovin.inventoryhud.gui.InventoryGui;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(InventoryGui.class)
public class InventoryHudOverlay {
    @WrapMethod(method = "renderArmor")
    private void wrapArmor(GuiGraphicsExtractor gui, Operation<Void> original) {
        StreamproofAPI.begin("INVENTORY_HUD_ARMOR");
        original.call(gui);
        StreamproofAPI.end("INVENTORY_HUD_ARMOR");
    }

    @WrapMethod(method = "renderPotions")
    private void wrapPotion(GuiGraphicsExtractor gui, Operation<Void> original) {
        StreamproofAPI.begin("INVENTORY_HUD_POTION");
        original.call(gui);
        StreamproofAPI.end("INVENTORY_HUD_POTION");
    }

    @WrapMethod(method = "renderInventory")
    private void wrapInventory(GuiGraphicsExtractor gui, DeltaTracker deltaTracker, Operation<Void> original) {
        StreamproofAPI.begin("INVENTORY_HUD_INVENTORY");
        original.call(gui, deltaTracker);
        StreamproofAPI.end("INVENTORY_HUD_INVENTORY");
    }
}