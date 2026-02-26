package win.transgirls.streamproof.tools;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import win.transgirls.streamproof.api.types.ComponentCategory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static win.transgirls.streamproof.Streamproof.LOGGER;

public class StreamproofSettings {
    public final Map<String, StreamproofComponent> loaded;
    private final Gson gson;
    private final Path settingsFile;
    private final AtomicBoolean running;

    private WatchService watcher;
    private Thread watchThread;

    public StreamproofSettings() {
        this.loaded = new LinkedHashMap<>();
        this.gson = new Gson();
        Path settingsDirectory = FabricLoader.getInstance().getConfigDir().resolve("streamproof");
        this.settingsFile = settingsDirectory.resolve("streamproof.json");
        this.running = new AtomicBoolean(true);

        try {
            Files.createDirectories(settingsDirectory);
        } catch (IOException e) {
            LOGGER.error("Streamproof failed to create settings directory", e);
        }

        try {
            this.verify();

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

        for (String id : loaded.keySet()) {
            if (elem.has(id) && elem.get(id).isJsonPrimitive() && elem.getAsJsonPrimitive(id).isBoolean()) {
                boolean newIsStreamproof = elem.getAsJsonPrimitive(id).getAsBoolean();
                StreamproofComponent component = loaded.get(id);

                component.isStreamproof = newIsStreamproof;
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

    private void ensureLoaded(String id) {
        if (!loaded.containsKey(id)) {
            throw new RuntimeException("Unknown or unloaded streamproof id: " + id);
        }
    }

    private void write(String id, boolean value) throws IOException {
        JsonObject elem = this.verify();
        elem.addProperty(id, value);
        Files.write(this.settingsFile, this.gson.toJson(elem).getBytes());
    }

    public void load(String id, String label, boolean defaultStreamproof, ComponentCategory category) throws IOException {
        JsonObject elem = this.verify();
        boolean isStreamproof = defaultStreamproof;

        if (elem.has(id) && elem.get(id).isJsonPrimitive() && elem.getAsJsonPrimitive(id).isBoolean()) {
            isStreamproof = elem.getAsJsonPrimitive(id).getAsBoolean();
        } else {
            this.write(id, defaultStreamproof);
        }

        this.loaded.put(id, new StreamproofComponent(id, label, isStreamproof, category));
    }

    public void set(String id, boolean streamproof) throws IOException {
        this.getComponent(id).isStreamproof = streamproof;
        this.write(id, streamproof);
    }

    public boolean isStreamproof(String id) {
        return this.getComponent(id).isStreamproof;
    }

    public StreamproofComponent getComponent(String id) {
        this.ensureLoaded(id);
        return this.loaded.get(id);
    }

    public Collection<StreamproofComponent> getComponents() {
        return this.loaded.values();
    }

    public List<ComponentCategory> getCategories() {
        return Arrays.stream(ComponentCategory.values()).filter((category -> !category.equals(ComponentCategory.Hidden))).toList();
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