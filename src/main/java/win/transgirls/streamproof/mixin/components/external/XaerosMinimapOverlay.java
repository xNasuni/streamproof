package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.tools.ComponentKind;
import xaero.hud.minimap.module.MinimapRenderer;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.render.module.ModuleRenderContext;

@Mixin(MinimapRenderer.class)
public class XaerosMinimapOverlay {
    @WrapMethod(method = "render(Lxaero/hud/minimap/module/MinimapSession;Lxaero/hud/render/module/ModuleRenderContext;Lnet/minecraft/class_332;F)V")
    private void wrapRender(MinimapSession session, ModuleRenderContext c, @Coerce Object graphics, float partialTicks, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(Streamproof.settings.isStreamproof(ComponentKind.XAEROS_MINIMAP_MINIMAP), graphics, (data) -> {
            original.call(session, c, data.graphics, partialTicks);
        });
    }
}