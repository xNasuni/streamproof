package win.transgirls.streamproof.visuals.panels;


import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.SharedConstants;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.systems.gl.ImGuiHelper;
import win.transgirls.streamproof.visuals.Style;
import win.transgirls.streamproof.visuals.candy.EffectOrder;

import java.util.ArrayList;
import java.util.List;

import static win.transgirls.streamproof.visuals.Color.rgba;

public class Dashboard extends PanelInterface {
    private final List<Tab> tabs = new ArrayList<>();

    @Override
    public PanelType getType() {
        return PanelType.Menu;
    }

    @Override
    public void setup() {
        this.tabs.addAll(List.of(new Tab[]{
                new Tab("Main", () -> {
                    ImGui.text("main tab!!");
                    ImGui.text(String.format("FPS: %.2f", ImGui.getIO().getFramerate()));
                }, true),
                new Tab("Misc", () -> {
                    ImGui.text("second");
                }),
                new Tab("Options", () -> {
                    ImGui.textColored(rgba(182, 182, 182, 255), "Offline");
                })
        }));

        super.setup();
        this.show();
    }

    @Override
    public void render(ImGuiIO io) {
        this.renderEffects(EffectOrder.BeforeOverlay);
        this.renderEffects(EffectOrder.BeforeGui);

        ImGui.setNextWindowPos(io.getDisplaySizeX() / 2.0f, io.getDisplaySizeY() / 2.0f, ImGuiCond.Once);

        ImGui.begin("Streamproof Internal", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoScrollbar);
        this.renderEffects(EffectOrder.InGui);

        ImGuiHelper.addImageRounded(Style.iconTexture, 20, 20, 5);

        ImGui.pushFont(Style.getFont(24));
        ImGui.sameLine();

        ImGui.textColored(rgba(255, 168, 240, 255), "Streamproof");
        ImGui.sameLine();
        ImGui.textColored(rgba(182, 182, 182, 255), String.format("%s <3", SharedConstants.getGameVersion().id()));

        ImGuiHelper.graySeperator();

        ImGui.popFont();
        ImGui.pushFont(Style.getFont(16));

        for (Tab tab : this.tabs) {
            boolean isItPink = tab.visible();
            ImGui.pushStyleColor(ImGuiCol.Button, rgba(0, 0, 0, 0));
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, rgba(0, 0, 0, 0));
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, rgba(0, 0, 0, 0));
            if (isItPink) {
                ImGui.pushStyleColor(ImGuiCol.Text, rgba(255, 168, 240, 255));
            }
            if (ImGui.button(tab.label)) {
                tab.show();
            }
            if (isItPink) {
                ImGui.popStyleColor();
            }
            ImGui.popStyleColor();
            ImGui.popStyleColor();
            ImGui.popStyleColor();
            ImGui.sameLine();
        }

        ImGui.dummy(0, 14);
        for (Tab tab : this.tabs) {
            if (tab.visible()) {
                tab.render();
            }
        }

        ImGui.popFont();
        ImGui.end();

        this.renderEffects(EffectOrder.AfterGui);
    }

    public static class Tab {
        public static final List<Tab> instances = new ArrayList<>();
        public final String label;
        private final Runnable render;
        private boolean visible = false;

        public Tab(String label, Runnable render, boolean showOnCreation) {
            this.label = label;
            this.render = render;

            if (showOnCreation) {
                this.show();
            }

            Tab.instances.add(this);
        }

        public Tab(String label, Runnable render) {
            this(label, render, false);
        }

        public void show() {
            for (Tab tab : Tab.instances) {
                tab.hide();
            }
            this.visible = true;
        }

        public void hide() {
            this.visible = false;
        }

        public boolean visible() {
            return this.visible;
        }

        public void render() {
            this.render.run();
        }
    }
}