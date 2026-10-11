package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(PlayerTabOverlay.class)
public class PlayerListOverlay {
    @WrapMethod(method = "extractRenderState")
    private void wrapRenderPlayerList(GuiGraphicsExtractor graphics, int screenWidth, Scoreboard scoreboard, Objective displayObjective, Operation<Void> original) {
        StreamproofAPI.begin("PLAYERLIST");
        original.call(graphics, screenWidth, scoreboard, displayObjective);
        StreamproofAPI.end("PLAYERLIST");
    }
}