package win.transgirls.streamproof.visuals.panels;

import imgui.ImGuiIO;
import win.transgirls.streamproof.visuals.Color;
import win.transgirls.streamproof.visuals.Style;
import win.transgirls.streamproof.visuals.candy.EffectOrder;

public class Backdrop extends PanelInterface {
    @Override
    public PanelType getType() {
        return PanelType.Menu;
    }

    @Override
    public void setup() {
//        this.effects.add(new GlowEffect());

        super.setup();
        this.show();
    }

    @Override
    public void render(ImGuiIO io) {
        this.renderEffects(EffectOrder.BeforeOverlay);

        Style.backdropShader.bind();
        Style.backdropShader.set("uColor", new Color(0, 0, 0, 155));
        Style.backdropShader.draw();

        this.renderEffects(EffectOrder.BeforeGui);
        this.renderEffects(EffectOrder.InGui);
        this.renderEffects(EffectOrder.AfterGui);
    }
}