package win.transgirls.streamproof.mixin.impl;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(GuiRenderState.Node.class)
public class RenderLayerMixin {
    @WrapMethod(method = "addItem")
    private void wrapAddItem(GuiItemRenderState itemState, Operation<Void> original) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderStates.add(itemState);
        }

        original.call(itemState);
    }

    @WrapMethod(method = "addText")
    private void wrapAddText(GuiTextRenderState textState, Operation<Void> original) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderStates.add(textState);
        }

        original.call(textState);
    }

    @WrapMethod(method = "addPicturesInPictureState")
    private void wrapAddSpecialElement(PictureInPictureRenderState picturesInPictureState, Operation<Void> original) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderStates.add(picturesInPictureState);
        }

        original.call(picturesInPictureState);
    }

    @WrapMethod(method = "addGuiElement")
    private void wrapAddSimpleElement(GuiElementRenderState blitState, Operation<Void> original) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderStates.add(blitState);
        }

        original.call(blitState);
    }

    @WrapMethod(method = "addGlyph")
    private void wrapAddPreparedText(GuiElementRenderState glyphState, Operation<Void> original) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderStates.add(glyphState);
        }

        original.call(glyphState);
    }
}