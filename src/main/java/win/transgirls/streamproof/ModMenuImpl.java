package win.transgirls.streamproof;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import win.transgirls.streamproof.visuals.Interface;

import java.util.function.Consumer;

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
            super(Text.of("Streamproof"));

            this.parent = parent;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            super.render(context, mouseX, mouseY, deltaTicks);

            if (!Interface.visible) {
                this.close();
            }
        }

        @Override
        public void close() {
            Streamproof.client.setScreen(this.parent);
        }
    }
}