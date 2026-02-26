package win.transgirls.streamproof.mixin.components.base;


import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(InGameHud.class)
public class ScoreboardOverlay {
    @WrapMethod(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V")
    private void wrapRenderScoreboard(DrawContext context, ScoreboardObjective objective, Operation<Void> original) {
        StreamproofAPI.begin("SCOREBOARD");
        original.call(context, objective);
        StreamproofAPI.end("SCOREBOARD");
    }
}