package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import win.transgirls.streamproof.Streamproof;
import xaero.common.XaeroHudModMenu;
import xaero.hud.minimap.module.MinimapRenderer;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.render.module.ModuleRenderContext;

@Mixin(MinimapRenderer.class)
public class XaerosMinimapMixin {
    @WrapMethod(method = "render(Lxaero/hud/minimap/module/MinimapSession;Lxaero/hud/render/module/ModuleRenderContext;Lnet/minecraft/class_332;F)V")
    private void wrapRender(MinimapSession session, ModuleRenderContext c, @Coerce Object guiGraphics, float partialTicks, Operation<Void> original) {
        Streamproof.renderQueue.add(guiGraphics, (data) -> {
            original.call(session, c, data.graphics, partialTicks);
        });
    }
}