package win.transgirls.streamproof.tools;

import java.util.List;

public enum ComponentKind {
    F3_OVERLAY("F3", ComponentCategory.GUI, true, List.of("net.minecraft.client.gui.hud.DebugHud", "net.minecraft.class_340")),
    DEBUG_HITBOXES("Hitboxes", ComponentCategory.WORLD, true, List.of("net.minecraft.client.render.gizmo.GizmoDrawerImpl", "net.minecraft.class_12160")),
    CHAT_MESSAGES_OVERLAY("Chat Messages", ComponentCategory.GUI, true, List.of("net.minecraft.client.gui.hud.ChatHud", "net.minecraft.class_338")),
    CHAT_INPUT_OVERLAY("Chat Input", ComponentCategory.GUI, true, List.of("net.minecraft.client.gui.widget.TextFieldWidget", "net.minecraft.class_342")),
    INVENTORY_HUD_ARMOR("Inventory Hud Armor", ComponentCategory.GUI, true, List.of("dlovin.inventoryhud.gui.InventoryHUDGui")),
    INVENTORY_HUD_POTION("Inventory Hud Potion", ComponentCategory.GUI, true, List.of("dlovin.inventoryhud.gui.InventoryHUDGui")),
    INVENTORY_HUD_INVENTORY("Inventory Hud Inventory", ComponentCategory.GUI, true, List.of("dlovin.inventoryhud.gui.InventoryHUDGui")),
    XAEROS_MINIMAP_MINIMAP("Xaero's Minimap Map", ComponentCategory.GUI, true, List.of("xaero.hud.minimap.module.MinimapRenderer")),
    XAEROS_MINIMAP_WAYPOINTS("Xaero's Minimap Waypoints", ComponentCategory.GUI, true, List.of("xaero.common.events.ClientEvents")),
    STREAMPROOF_IMGUI_WINDOW("ImGui Window", ComponentCategory.HIDDEN, true, List.of(""));

    public final String label;
    public final ComponentCategory category;
    public final boolean defaultStreamproof;
    public final boolean isInstalled;

    ComponentKind(String label, ComponentCategory category, boolean defaultEnabled) {
        this.label = label;
        this.category = category;
        this.defaultStreamproof = defaultEnabled;
        this.isInstalled = true;
    }

    ComponentKind(String label, ComponentCategory category, boolean defaultEnabled, List<String> requiredClasses) {
        this.label = label;
        this.category = category;
        this.defaultStreamproof = defaultEnabled;
        this.isInstalled = computeIsInstalled(requiredClasses);
    }

    private boolean computeIsInstalled(List<String> requiredClasses) {
        if (requiredClasses == null || requiredClasses.isEmpty()) return true;

        for (String className : requiredClasses) {
            try {
                Class.forName(className, false, ComponentKind.class.getClassLoader());
                return true;
            } catch (ClassNotFoundException ignored) {
            }
        }

        return false;
    }
}