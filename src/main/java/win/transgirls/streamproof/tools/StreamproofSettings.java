package win.transgirls.streamproof.tools;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static win.transgirls.streamproof.Streamproof.LOGGER;

public class StreamproofSettings {
    private final HashMap<ComponentKind, Boolean> loaded;
    private final Gson gson;
    private final Path settingsDirectory;
    private final Path settingsFile;
    private final AtomicBoolean running;

    private WatchService watcher;
    private Thread watchThread;

    public StreamproofSettings() {
        this.loaded = new HashMap<>();
        this.gson = new Gson();
        this.settingsDirectory = FabricLoader.getInstance().getConfigDir().resolve("streamproof");
        this.settingsFile = settingsDirectory.resolve("streamproof.json");
        this.running = new AtomicBoolean(true);

        try {
            Files.createDirectories(this.settingsDirectory);
        } catch (IOException e) {
            LOGGER.error("Streamproof failed to create settings directory", e);
        }

        try {
            this.verify();

            for (ComponentKind kind : this.getKinds()) {
                this.load(kind);
            }

            this.watcher = FileSystems.getDefault().newWatchService();
            settingsDirectory.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            this.watchThread = new Thread(this::watch, "Streamproof Settings Watcher");
            this.watchThread.setDaemon(true);
            this.watchThread.start();
        } catch (IOException e) {
            LOGGER.error("Streamproof failed to verify settings, load kinds, or setup watcher", e);
        }
    }

    private void watch() {
        while (running.get()) {
            WatchKey key;
            try {
                key = this.watcher.take();
            } catch (InterruptedException e) {
                break;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();

                if (filename.toString().equals("streamproof.json")) {
                    try {
                        reload();
                    } catch (IOException e) {
                        LOGGER.error("Streamproof failed to reload settings from file", e);
                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    private void reload() throws IOException {
        JsonObject elem = this.verify();

        for (ComponentKind kind : loaded.keySet()) {
            if (elem.has(kind.name()) && elem.get(kind.name()).isJsonPrimitive() && elem.getAsJsonPrimitive(kind.name()).isBoolean()) {
                boolean newValue = elem.getAsJsonPrimitive(kind.name()).getAsBoolean();
                if (loaded.get(kind) != newValue) {
                    loaded.put(kind, newValue);
                }
            }
        }
    }

    private JsonObject verify() throws IOException {
        try {
            if (Files.exists(this.settingsFile)) {
                String contents = Files.readString(this.settingsFile);
                if (contents.isBlank()) {
                    throw new RuntimeException("Blank settings file");
                }

                JsonElement obj = JsonParser.parseString(contents);
                if (!obj.isJsonObject()) {
                    LOGGER.warn("Streamproof failed to read settings file, may be corrupt. Deleting.");
                    Files.delete(this.settingsFile);
                }

                return obj.getAsJsonObject();
            }

            throw new RuntimeException("Nonexistent settings file");
        } catch (Throwable e) {
            JsonObject blank = new JsonObject();
            Files.write(this.settingsFile, this.gson.toJson(blank).getBytes());

            return blank;
        }
    }

    private void write(ComponentKind kind, boolean value) throws IOException {
        JsonObject elem = this.verify();
        elem.addProperty(kind.name(), value);
        Files.write(this.settingsFile, this.gson.toJson(elem).getBytes());
        this.loaded.put(kind, value);
    }

    private void load(ComponentKind kind) throws IOException {
        JsonObject elem = this.verify();
        if (elem.has(kind.name()) && elem.get(kind.name()).isJsonPrimitive() && elem.getAsJsonPrimitive(kind.name()).isBoolean()) {
            this.loaded.put(kind, elem.getAsJsonPrimitive(kind.name()).getAsBoolean());
        } else {
            write(kind, kind.defaultStreamproof);
        }
    }

    public void setStreamproof(ComponentKind kind, boolean streamproof) throws IOException {
        this.write(kind, streamproof);
    }

    public boolean isStreamproof(ComponentKind kind) {
        return this.loaded.getOrDefault(kind, kind.defaultStreamproof);
    }

    public List<ComponentKind> getKinds() {
        return Arrays.stream(ComponentKind.values()).filter(kind -> !kind.category.equals(ComponentCategory.HIDDEN)).toList();
    }

    public List<ComponentCategory> getCategories() {
        return Arrays.stream(ComponentCategory.values()).filter((category -> !category.equals(ComponentCategory.HIDDEN))).toList();
    }

    public ComponentCategory getCategoryForKind(ComponentKind kind) {
        if (!this.loaded.containsKey(kind) || !kind.isInstalled) {
            return ComponentCategory.NOT_FOUND;
        }

        return kind.category;
    }

    public void stop() {
        running.set(false);
        watchThread.interrupt();
        try {
            watcher.close();
        } catch (IOException e) {
            LOGGER.error("Streamproof failed to close settings file watcher", e);
        }
    }
}