package win.transgirls.streamproof;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import win.transgirls.streamproof.visuals.Interface;
import static win.transgirls.streamproof.Streamproof.mc;

public class ModMenuImpl implements ModMenuApi {
    @Override
    public void attachModpackBadges(@NotNull Consumer<String> consumer) {
        consumer.accept("modmenu");
    }

    @Override
    @NotNull
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {
            Interface.visible = true;
            return new ImGuiScreen(screen);
        };
    }

    public static class ImGuiScreen extends Screen {
        private final Screen parent;

        protected ImGuiScreen(Screen parent) {
            super(Component.nullToEmpty("Streamproof"));

            this.parent = parent;
        }

        @Override
        public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            super.extractRenderState(graphics, mouseX, mouseY, a);

            if (!Interface.visible) {
                this.onClose();
            }
        }

        @Override
        public void onClose() {
            mc.gui.setScreen(this.parent);
        }
    }
}