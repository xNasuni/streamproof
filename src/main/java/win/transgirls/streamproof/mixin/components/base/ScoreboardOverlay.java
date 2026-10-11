package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(Gui.class)
public class ScoreboardOverlay {
    @WrapMethod(method = "displayScoreboardSidebar")
    private void wrapRenderScoreboard(GuiGraphicsExtractor graphics, Objective objective, Operation<Void> original) {
        StreamproofAPI.begin("SCOREBOARD");
        original.call(graphics, objective);
        StreamproofAPI.end("SCOREBOARD");
    }
}