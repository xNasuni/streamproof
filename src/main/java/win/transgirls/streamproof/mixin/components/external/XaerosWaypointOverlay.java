package win.transgirls.streamproof.mixin.components.external;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.ProjectionType;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.tools.ComponentKind;
import xaero.common.events.ClientEvents;

@Mixin(ClientEvents.class)
public class XaerosWaypointOverlay {
    @WrapMethod(method = "handleRenderGameOverlayEventPre")
    private void wrapEvent(DrawContext graphics, float partialTicks, Operation<Void> original) {
        Streamproof.renderQueue.deferGui(Streamproof.settings.isStreamproof(ComponentKind.XAEROS_MINIMAP_WAYPOINTS), graphics, (data) -> {
            original.call(data.graphics, partialTicks);
        });
    }
}