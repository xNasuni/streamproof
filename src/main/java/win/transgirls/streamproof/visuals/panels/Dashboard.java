package win.transgirls.streamproof.visuals.panels;


import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.SharedConstants;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.api.types.ComponentCategory;
import win.transgirls.streamproof.tools.StreamproofComponent;
import win.transgirls.streamproof.visuals.Color;
import win.transgirls.streamproof.visuals.Style;
import win.transgirls.streamproof.visuals.candy.EffectOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static win.transgirls.streamproof.Streamproof.LOGGER;
import static win.transgirls.streamproof.visuals.Color.rgba;

public class Dashboard extends PanelInterface {
    private final List<Tab> tabs = new ArrayList<>();

    public static void graySeperator() {
        ImGui.dummy(0, 1);

        ImVec2 start = ImGui.getCursorScreenPos();
        ImVec2 end = new ImVec2(start.x + ImGui.getContentRegionAvailX(), start.y + 1);

        ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addRectFilled(start.x, start.y, end.x, end.y, rgba(196, 196, 196, 85));

        ImGui.dummy(0, 0);
    }

    @Override
    public PanelType getType() {
        return PanelType.Menu;
    }

    @Override
    public void setup() {
        this.tabs.addAll(List.of(new Tab[]{
                new Tab("Toggles", this::renderToggles, true),
                new Tab("Extra", this::renderExtras, false)
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

        ImGui.dummy(-8f, 0f);
        ImGui.sameLine();
        ImGui.image(Style.iconTexture, 32, 32);

        ImGui.pushFont(Style.getFont(32));
        ImGui.sameLine();

        ImGui.beginGroup();
        ImGui.textColored(Color.PINK.toInt(), "Streamproof");
        ImGui.popFont();
        ImGui.pushFont(Style.getFont(16));
        ImGui.setCursorPosY(ImGui.getCursorPosY() - 4f);
        ImGui.textColored(Color.GRAY.toInt(), String.format("%s <3", SharedConstants.getGameVersion().id()));
        ImGui.popFont();
        ImGui.endGroup();

        ImGui.setCursorPosY(ImGui.getCursorPosY() - 4f);
        graySeperator();

        ImGui.pushFont(Style.getFont(18));

        for (Tab tab : this.tabs) {
            boolean selected = tab.visible();
            ImGui.pushStyleColor(ImGuiCol.Button, rgba(0, 0, 0, 0));
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, rgba(0, 0, 0, 0));
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, rgba(0, 0, 0, 0));
            if (!selected) {
                ImGui.pushStyleColor(ImGuiCol.Text, Color.GRAY.toInt());
            }
            if (ImGui.button(tab.label)) {
                tab.show();
            }
            if (!selected) {
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

    public void renderToggles() {
        for (ComponentCategory category : Streamproof.settings.getCategories()) {
            ImVec2 categorySize = new ImVec2();
            ImGui.calcTextSize(categorySize, category.label);

            float maxWidth = categorySize.x;
            for (StreamproofComponent component : Streamproof.settings.getComponents()) {
                if (component.category.equals(category)) {
                    ImVec2 labelSize = new ImVec2();
                    ImGui.calcTextSize(labelSize, component.label);

                    float textWidth = labelSize.x;
                    maxWidth = Math.max(maxWidth, textWidth);
                }
            }

            float childWidth = maxWidth + 40;
            boolean transparent = category.equals(ComponentCategory.Other);

            if (transparent) {
                ImGui.pushStyleVar(ImGuiStyleVar.Alpha, 0.5f);
            }

            if (ImGui.beginChild(category.label, childWidth, 0)) {
                ImGui.text(category.label);
                ImGui.separator();

                for (StreamproofComponent component : Streamproof.settings.getComponents()) {
                    if (component.category.equals(category)) {
                        if (ImGui.checkbox(component.label, component.isStreamproof)) {
                            try {
                                Streamproof.settings.set(component.id, !component.isStreamproof);
                            } catch (IOException e) {
                                LOGGER.error("Failed to save setting", e);
                            }
                        }
                    }
                }
            }

            ImGui.endChild();

            if (transparent) {
                ImGui.popStyleVar();
            }

            ImGui.sameLine();
        }
    }

    public void renderExtras() {
        StreamproofComponent component = Streamproof.settings.getComponent("STREAMPROOF_IMGUI_WINDOW");

        if (ImGui.checkbox(component.label, component.isStreamproof)) {
            try {
                Streamproof.settings.set(component.id, !component.isStreamproof);
            } catch (IOException e) {
                LOGGER.error("Streamproof failed to save setting", e);
            }
        }
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