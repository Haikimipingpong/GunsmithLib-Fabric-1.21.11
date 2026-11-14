package mod.chloeprime.gunsmithlib.fcompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A very small stand-in for NeoForge's config spec, backed by a JSON file at
 * {@code config/gunsmithlib-common.json}. Only the operations GunsmithLib uses are implemented.
 */
public final class ModConfigSpec implements IConfigSpec {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<String, ConfigValue<?>> values;
    private final Path file;

    private ModConfigSpec(Map<String, ConfigValue<?>> values) {
        this.values = values;
        this.file = FabricLoader.getInstance().getConfigDir().resolve("gunsmithlib-common.json");
    }

    @Override
    public void load() {
        if (!Files.exists(file)) {
            save();
            return;
        }
        try {
            var root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
            values.forEach((key, value) -> {
                if (root.has(key)) {
                    value.read(root.get(key));
                }
            });
        } catch (IOException | RuntimeException e) {
            GunsmithLibLog.warn("Failed to read config {}, using defaults", file, e);
        }
    }

    @Override
    public void save() {
        var root = new JsonObject();
        values.forEach((key, value) -> root.add(key, value.write()));
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(root), StandardCharsets.UTF_8);
        } catch (IOException e) {
            GunsmithLibLog.warn("Failed to write config {}", file, e);
        }
    }

    public static final class Builder {
        private final List<String> comments = new ArrayList<>();
        private final List<String> path = new ArrayList<>();
        private final Map<String, ConfigValue<?>> values = new LinkedHashMap<>();

        public Builder comment(String... comment) {
            comments.addAll(List.of(comment));
            return this;
        }

        public Builder push(String section) {
            path.add(section);
            return this;
        }

        public Builder pop() {
            if (!path.isEmpty()) path.removeLast();
            return this;
        }

        private String key(String name) {
            return path.isEmpty() ? name : String.join(".", path) + "." + name;
        }

        public BooleanValue define(String name, boolean defaultValue) {
            var value = new BooleanValue(defaultValue);
            values.put(key(name), value);
            return value;
        }

        public IntValue defineInRange(String name, int defaultValue, int min, int max) {
            var value = new IntValue(defaultValue, min, max);
            values.put(key(name), value);
            return value;
        }

        public DoubleValue defineInRange(String name, double defaultValue, double min, double max) {
            var value = new DoubleValue(defaultValue, min, max);
            values.put(key(name), value);
            return value;
        }

        public <E extends Enum<E>> EnumValue<E> defineEnum(String name, E defaultValue) {
            var value = new EnumValue<>(defaultValue);
            values.put(key(name), value);
            return value;
        }

        public LongValue defineInRange(String name, long defaultValue, long min, long max) {
            var value = new LongValue(defaultValue, min, max);
            values.put(key(name), value);
            return value;
        }

        public ModConfigSpec build() {
            return new ModConfigSpec(values);
        }
    }

    public abstract static class ConfigValue<T extends Comparable<? super T>> {
        private final T defaultValue;
        private volatile T value;

        protected ConfigValue(T defaultValue) {
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        public T get() {
            return value;
        }

        public T getDefault() {
            return defaultValue;
        }

        public void set(T value) {
            this.value = value;
        }

        abstract com.google.gson.JsonElement write();

        abstract void read(com.google.gson.JsonElement element);
    }

    public static final class EnumValue<E extends Enum<E>> extends ConfigValue<E> {
        EnumValue(E defaultValue) {
            super(defaultValue);
        }

        @Override
        com.google.gson.JsonElement write() {
            return new com.google.gson.JsonPrimitive(get().name());
        }

        @SuppressWarnings("unchecked")
        @Override
        void read(com.google.gson.JsonElement element) {
            try {
                set((E) Enum.valueOf(getDefault().getDeclaringClass(), element.getAsString()));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public static final class BooleanValue extends ConfigValue<Boolean> {
        BooleanValue(boolean defaultValue) {
            super(defaultValue);
        }

        @Override
        com.google.gson.JsonElement write() {
            return new com.google.gson.JsonPrimitive(get());
        }

        @Override
        void read(com.google.gson.JsonElement element) {
            set(element.getAsBoolean());
        }
    }

    public static final class IntValue extends ConfigValue<Integer> {
        private final int min;
        private final int max;

        IntValue(int defaultValue, int min, int max) {
            super(defaultValue);
            this.min = min;
            this.max = max;
        }

        @Override
        com.google.gson.JsonElement write() {
            return new com.google.gson.JsonPrimitive(get());
        }

        @Override
        void read(com.google.gson.JsonElement element) {
            set(Math.min(max, Math.max(min, element.getAsInt())));
        }
    }

    public static final class LongValue extends ConfigValue<Long> {
        private final long min;
        private final long max;

        LongValue(long defaultValue, long min, long max) {
            super(defaultValue);
            this.min = min;
            this.max = max;
        }

        @Override
        com.google.gson.JsonElement write() {
            return new com.google.gson.JsonPrimitive(get());
        }

        @Override
        void read(com.google.gson.JsonElement element) {
            set(Math.min(max, Math.max(min, element.getAsLong())));
        }
    }

    public static final class DoubleValue extends ConfigValue<Double> {
        private final double min;
        private final double max;

        DoubleValue(double defaultValue, double min, double max) {
            super(defaultValue);
            this.min = min;
            this.max = max;
        }

        @Override
        com.google.gson.JsonElement write() {
            return new com.google.gson.JsonPrimitive(get());
        }

        @Override
        void read(com.google.gson.JsonElement element) {
            set(Math.min(max, Math.max(min, element.getAsDouble())));
        }
    }
}
