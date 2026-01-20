package win.transgirls.streamproof.visuals.panels;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.type.ImBoolean;
import win.transgirls.streamproof.visuals.candy.EffectInterface;
import win.transgirls.streamproof.visuals.candy.EffectOrder;

import java.util.ArrayList;
import java.util.List;

public class PanelInterface {
    protected static List<PanelInterface> panels = new ArrayList<>();

    protected ImBoolean visible = new ImBoolean(false);
    protected List<EffectInterface> effects = new ArrayList<>();

    protected void renderEffects(EffectOrder order) {
        for (EffectInterface effect : this.effects) {
            if (effect.getOrder() == order) {
                effect.render(ImGui.getIO());
            }
        }
    }

    protected <T> T getEffect(Class<T> clazz) {
        for (EffectInterface effect : this.effects) {
            if (effect.getClass().equals(clazz)) {
                return (T) effect;
            }
        }
        return null;
    }

    protected <T> T getPanel(Class<T> clazz) {
        for (PanelInterface otherPanel : PanelInterface.panels) {
            if (otherPanel.getClass().equals(clazz)) {
                return (T) otherPanel;
            }
        }
        return null;
    }

    public PanelType getType() {
        return PanelType.Unknown;
    }

    public boolean visible() {
        return this.visible.get();
    }

    public ImBoolean visibility() {
        return this.visible;
    }

    public void hide() {
        this.visible.set(false);
    }

    public void show() {
        this.visible.set(true);
    }

    public void setup() {
        PanelInterface.panels.add(this);
        for (EffectInterface effect : this.effects) {
            effect.setup();
        }
    }

    public void render(ImGuiIO io) {
    }
}