package win.transgirls.streamproof.visuals;

import org.joml.Math;

public record Color(int r, int g, int b, int a) {
    public static final Color PINK = new Color(255, 143, 178, 255);
    public static final Color GRAY = new Color(182, 182, 182, 255);

    public static float tick = 0.0f;

    public static int rgba(int r, int g, int b, int a) {
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    public static int rgba(Color color) {
        return rgba(color.r, color.g, color.b, color.a);
    }

    public static int rgba(Color color, int a) {
        return rgba(color.r, color.g, color.b, a);
    }

    public static Color lerp(Color a, Color b, float t) {
        return new Color((int) (a.r + (b.r - a.r) * t), (int) (a.g + (b.g - a.g) * t), (int) (a.b + (b.b - a.b) * t), (int) (a.a + (b.a - a.a) * t));
    }

    public static Color from(int col) {
        int b = col & 0xff;
        int g = (col & 0xff00) >> 8;
        int r = (col & 0xff0000) >> 16;
        int a = (col & 0xff000000) >>> 24;
        return new Color(r, g, b, a);
    }

    public static Color fromHSV(float h, float s, float v) {
        h = h % 360;
        if (h < 0) {
            h += 360;
        }

        float c = v * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = v - c;

        float rPrime = 0;
        float gPrime = 0;
        float bPrime = 0;

        if (h < 60) {
            rPrime = c;
            gPrime = x;
            bPrime = 0;
        } else if (h < 120) {
            rPrime = x;
            gPrime = c;
            bPrime = 0;
        } else if (h < 180) {
            rPrime = 0;
            gPrime = c;
            bPrime = x;
        } else if (h < 240) {
            rPrime = 0;
            gPrime = x;
            bPrime = c;
        } else if (h < 300) {
            rPrime = x;
            gPrime = 0;
            bPrime = c;
        } else {
            rPrime = c;
            gPrime = 0;
            bPrime = x;
        }

        int r = Math.round((rPrime + m) * 255);
        int g = Math.round((gPrime + m) * 255);
        int b = Math.round((bPrime + m) * 255);
        return new Color(r, g, b, 255);
    }

    public static Color rainbow(float brightness, float speed, float offset) {
        if (tick > 1.0f) {
            tick -= 1.0f;
        }

        float hue = ((tick + offset) * 360.0f) % 360.0f;

        return Color.fromHSV(hue, brightness, 1f);
    }

    public static Color rainbow() {
        return Color.rainbow(0.4f, 1f, 0f);
    }

    public Color withAlpha(int a) {
        return new Color(this.r, this.g, this.b, a);
    }

    public int toInt() {
        return rgba(r, g, b, a);
    }
}