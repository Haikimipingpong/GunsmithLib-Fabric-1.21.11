package mod.chloeprime.gunsmithlib.common.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Optional;

public abstract class SimpleCodecResourceReloadListener<T> extends SimplePreparableReloadListener<Map<Identifier, T>> {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final Codec<Map<Identifier, JsonElement>> RAW_JSON_CODEC = Codec.unboundedMap(
            Identifier.CODEC,
            ExtraCodecs.JSON
    );

    private final Codec<T> codec;
    private final Delegate delegate;
    private final Logger logger;
    protected volatile Map<Identifier, JsonElement> raw;

    public SimpleCodecResourceReloadListener(Codec<T> codec, Gson pGson, String directory) {
        this.codec = codec;
        this.delegate = new Delegate(pGson, directory);
        this.logger = LoggerFactory.getLogger("%s: %s".formatted(getClass().getSimpleName(), directory));
    }

    @Override
    @ParametersAreNonnullByDefault
    protected @Nonnull Map<Identifier, T> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        var jsons = this.raw = delegate.prepare(resourceManager, profiler);
        return decodeFromJson(jsons);
    }

    protected Map<Identifier, T> decodeFromJson(Map<Identifier, JsonElement> rawData) {
        var builder = ImmutableMap.<Identifier, T>builder();
        rawData.forEach((id, json) -> {
            var decoded = codec.decode(JsonOps.INSTANCE, json);
            if (decoded.error().isPresent()) {
                logger.error("failed to parse resource {}: {}", id, decoded.error().get().message());
                return;
            }
            if (decoded.result().isPresent()) {
                builder.put(id, decoded.result().get().getFirst());
            }
        });
        return builder.buildKeepingLast();
    }

    protected static Optional<CompoundTag> encodeJsonToNBT(Map<Identifier, JsonElement> jsons) {
        var encoded = RAW_JSON_CODEC.encodeStart(NbtOps.INSTANCE, jsons);
        if (encoded.error().isPresent()) {
            GunsmithLib.LOGGER.error("Failed to encode gun variant registry: {}", encoded.error().get().message());
            return Optional.empty();
        }
        if (encoded.result().isPresent()) {
            Tag tag = encoded.result().get();
            if (tag instanceof CompoundTag compound) {
                return Optional.of(compound);
            } else {
                GunsmithLib.LOGGER.error("Not a compound tag, this should not happen, actual tag type: {}", tag.getClass().getSimpleName());
                return Optional.empty();
            }
        }
        throw new IllegalStateException();
    }

    protected static Optional<Map<Identifier, JsonElement>> decodeJsonFromNBT(CompoundTag nbt) {
        var json = RAW_JSON_CODEC.decode(NbtOps.INSTANCE, nbt);
        if (json.error().isPresent()) {
            GunsmithLib.LOGGER.error("Failed to decodeFromJson gun variant registry: {}", json.error().get().message());
            return Optional.empty();
        }
        if (json.result().isPresent()) {
            return json.result().map(Pair::getFirst);
        }
        throw new IllegalStateException();
    }

    /**
     * Users should implement this :)
     */
    @Override
    @ParametersAreNonnullByDefault
    protected abstract void apply(Map<Identifier, T> data, ResourceManager resourceManager, ProfilerFiller profiler);

    private static class Delegate extends net.minecraft.server.packs.resources.SimplePreparableReloadListener<java.util.Map<Identifier, com.google.gson.JsonElement>> {
        private final String directory;

        public Delegate(Gson gson, String directory) {
            this.directory = directory;
        }

        @Override
        protected java.util.Map<Identifier, com.google.gson.JsonElement> prepare(ResourceManager rm, ProfilerFiller profiler) {
            // PORT: 1.21.11 的 SimpleJsonResourceReloadListener 改成 Codec 驱动，这里手动扫描目录读原始 JSON。
            var result = new java.util.HashMap<Identifier, com.google.gson.JsonElement>();
            var converter = new net.minecraft.resources.FileToIdConverter(directory, ".json");
            converter.listMatchingResources(rm).forEach((id, resource) -> {
                try (var reader = resource.openAsReader()) {
                    result.put(converter.fileToId(id), com.google.gson.JsonParser.parseReader(reader));
                } catch (Exception ex) {
                    GunsmithLib.LOGGER.error("Failed to read {}", id, ex);
                }
            });
            return result;
        }

        @Override
        protected void apply(java.util.Map<Identifier, com.google.gson.JsonElement> data, ResourceManager rm, ProfilerFiller profiler) {
            throw new AbstractMethodError();
        }
    }
}
