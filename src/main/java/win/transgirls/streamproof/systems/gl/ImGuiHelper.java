package win.transgirls.streamproof.systems.gl;


import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import win.transgirls.streamproof.visuals.Color;

import static win.transgirls.streamproof.visuals.Color.rgba;

public class ImGuiHelper {
    public static void addRectFilledGradientHorizontal(ImVec2 start, ImVec2 end, int colorStart, int colorEnd) {
        ImDrawList drawList = ImGui.getWindowDrawList();

        int steps = 50;
        float stepSize = (end.x - start.x) / steps;

        for (int i = 0; i < steps; i++) {
            float t = (float) i / steps;
            int color = Color.lerp(Color.from(colorStart), Color.from(colorEnd), t).toInt();

            float x1 = start.x + i * stepSize;
            float x2 = start.x + (i + 1) * stepSize;

            drawList.addRectFilled(x1, start.y, x2, end.y, color);
        }
    }

    public static void addRectFilledGradientVertical(ImVec2 start, ImVec2 end, int colorStart, int colorEnd) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        int steps = 50;
        float stepSize = (end.y - start.y) / steps;

        for (int i = 0; i < steps; i++) {
            float t = (float) i / steps;
            int color = Color.lerp(Color.from(colorStart), Color.from(colorEnd), t).toInt();

            float y1 = start.y + i * stepSize;
            float y2 = start.y + (i + 1) * stepSize;

            drawList.addRectFilled(start.x, y1, end.x, y2, color);
        }
    }

    public static void graySeperator() {
        ImGui.dummy(0, 1);

        ImVec2 start = ImGui.getCursorScreenPos();
        ImVec2 end = new ImVec2(start.x + ImGui.getContentRegionAvailX(), start.y + 1);

        addRectFilledGradientHorizontal(start, end, rgba(196, 196, 196, 85), rgba(196, 196, 196, 146));

        ImGui.dummy(0, 0);
    }

    public static void rainbowSeperatorVertical(float offset) {
        ImVec2 start = new ImVec2(ImGui.getCursorScreenPos().x, ImGui.getCursorScreenPos().y + 4);

        float separatorWidth = 4f;
        float separatorHeight = ImGui.getContentRegionAvailY();

        ImVec2 end = new ImVec2(start.x + separatorWidth, start.y + separatorHeight);

        addRectFilledGradientVertical(start, end,
                Color.rainbow(0.4f, 1.0f, (offset % 1f)).toInt(),
                Color.rainbow(0.4f, 1.0f, (offset + (1 / 16f)) % 1f).toInt());

        ImGui.dummy(4f, 0f);
    }

    public static void addImageRounded(int imageId, float width, float height, float radius, int tint) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        ImVec2 cursorPos = ImGui.getCursorScreenPos();
        ImGui.dummy(width, height);

        float uMin = 0.0f;
        float vMin = 0.0f;
        float uMax = 1.0f;
        float vMax = 1.0f;

        float xMin = cursorPos.x;
        float yMin = cursorPos.y;
        float xMax = xMin + width;
        float yMax = yMin + height;

        drawList.addImageRounded(imageId, xMin, yMin, xMax, yMax, uMin, vMin, uMax, vMax, tint, radius);
    }

    public static void addImageRounded(int imageId, float width, float height, float radius) {
        addImageRounded(imageId, width, height, radius, rgba(255, 255, 255, 255));
    }
}