package win.transgirls.streamproof.mixin.components.base;


import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(PlayerListHud.class)
public class PlayerListOverlay {
    @WrapMethod(method = "render")
    private void wrapRenderPlayerList(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, Operation<Void> original) {
        StreamproofAPI.begin("PLAYERLIST");
        original.call(context, scaledWindowWidth, scoreboard, objective);
        StreamproofAPI.end("PLAYERLIST");
    }
}