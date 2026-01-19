package win.transgirls.streamproof.mixin.components.base;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import win.transgirls.streamproof.Streamproof;

@Mixin(ChatComponent.class)
public abstract class UnfocusedChatOverlay {
//    @WrapOperation(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;pushMatrix()Lorg/joml/Matrix3x2fStack;"))
//    private Matrix3x2fStack skipPush(Matrix3x2fStack instance, Operation<Matrix3x2fStack> original) {
//        return instance;
//    }
//
//    @WrapOperation(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;popMatrix()Lorg/joml/Matrix3x2fStack;"))
//    private Matrix3x2fStack skipPop(Matrix3x2fStack instance, Operation<Matrix3x2fStack> original) {
//        return instance;
//    }

    @WrapMethod(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V")
    private void wrapRender(GuiGraphics guiGraphics, Font font, int i, int j, int k, boolean bl, boolean bl2, Operation<Void> original) {
        Streamproof.renderQueue.add((data) -> {
            original.call(data.graphics, font, i, j, k, bl, bl2);
        });
    }
}