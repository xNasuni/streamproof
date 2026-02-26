package win.transgirls.streamproof.mixin.impl;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.StreamproofAPI;

@Mixin(GuiRenderState.Layer.class)
public class RenderLayerMixin {
    @WrapMethod(method = "addItem")
    private void wrapAddItem(ItemGuiElementRenderState state, Operation<Void> original) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderStates.add(state);
        }

        original.call(state);
    }

    @WrapMethod(method = "addText")
    private void wrapAddText(TextGuiElementRenderState state, Operation<Void> original) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderStates.add(state);
        }

        original.call(state);
    }

    @WrapMethod(method = "addSpecialElement")
    private void wrapAddSpecialElement(SpecialGuiElementRenderState state, Operation<Void> original) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderStates.add(state);
        }

        original.call(state);
    }

    @WrapMethod(method = "addSimpleElement")
    private void wrapAddSimpleElement(SimpleGuiElementRenderState state, Operation<Void> original) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderStates.add(state);
        }

        original.call(state);
    }

    @WrapMethod(method = "addPreparedText")
    private void wrapAddPreparedText(SimpleGuiElementRenderState state, Operation<Void> original) {
        if (StreamproofAPI.isWriting()) {
            Streamproof.renderStates.add(state);
        }

        original.call(state);
    }
}