package win.transgirls.streamproof.types;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ResultToast implements Toast {
    private static final Identifier SUCCESS_TEXTURE =
            Identifier.fromNamespaceAndPath("streamproof", "toast/success");
    private static final Identifier FAILURE_TEXTURE =
            Identifier.fromNamespaceAndPath("streamproof", "toast/failure");

    private final Component text;
    private final boolean success;
    private long startTime;
    private Visibility wantedVisibility = Visibility.HIDE;

    public ResultToast(Component text, boolean success) {
        this.text = text;
        this.success = success;
    }

    @Override
    public @NotNull Visibility getWantedVisibility() {
        return this.wantedVisibility;
    }

    @Override
    public void update(@NonNull ToastManager manager, long time) {
        if (this.startTime == 0L) {
            this.startTime = time;
        }

        double displayDuration = 5000L * manager.getNotificationDisplayTimeMultiplier();
        long elapsed = time - this.startTime;
        this.wantedVisibility = elapsed < displayDuration ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, @NonNull Font font, long fullyVisibleForMs) {
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                this.success ? SUCCESS_TEXTURE : FAILURE_TEXTURE,
                0, 0,
                this.width(), this.height()
        );

        graphics.text(font, Component.literal(this.success ? "Success" : "Failure").withStyle(Style.EMPTY.withBold(true)), this.success ? 21 : 18, 7, this.success ? 0xFF618E45 : 0xFF8C4444, true);

        List<FormattedCharSequence> lines = font.split(this.text, this.width() - 8);
        for (int i = 0; i < lines.size(); i++) {
            graphics.text(
                    font,
                    lines.get(i),
                    4,
                    20 + i * font.lineHeight,
                    0xffffffff,
                    true
            );
        }
    }

    @Override
    public float yPos(int topIndex) {
        return topIndex * (float) SLOT_HEIGHT;
    }

    @Override
    public int width() {
        return 160;
    }

    @Override
    public int height() {
        return 64;
    }
}
