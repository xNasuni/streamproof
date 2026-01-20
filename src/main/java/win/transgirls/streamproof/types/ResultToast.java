package win.transgirls.streamproof.types;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ResultToast implements Toast {
    private static final Identifier SUCCESS_TEXTURE =
            Identifier.of("streamproof", "toast/success");
    private static final Identifier FAILURE_TEXTURE =
            Identifier.of("streamproof", "toast/failure");

    private final Text text;
    private final boolean success;
    private long startTime;
    private Visibility wantedVisibility = Visibility.HIDE;

    public ResultToast(Text text, boolean success) {
        this.text = text;
        this.success = success;
    }

    @Override
    public @NotNull Visibility getVisibility() {
        return this.wantedVisibility;
    }

    @Override
    public void update(ToastManager manager, long time) {
        if (this.startTime == 0L) {
            this.startTime = time;
        }

        double displayDuration = 5000L * manager.getNotificationDisplayTimeMultiplier();
        long elapsed = time - this.startTime;
        this.wantedVisibility = elapsed < displayDuration ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public void draw(DrawContext gfx, TextRenderer font, long time) {
        gfx.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                this.success ? SUCCESS_TEXTURE : FAILURE_TEXTURE,
                0, 0,
                this.getWidth(), this.getHeight()
        );

        gfx.drawText(font, Text.literal(this.success ? "Success" : "Failure").fillStyle(Style.EMPTY.withBold(true)), this.success ? 21 : 18, 7, this.success ? 0xFF618E45 : 0xFF8C4444, true);

        int textWidth = font.getWidth(this.text);
        gfx.drawText(
                font,
                this.text,
                4,
                20,
                0xffffffff,
                true
        );
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public int getHeight() {
        return 64;
    }
}
