package mod.chloeprime.gunsmithlib.fcompat;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DeferredHolder<R, T extends R> implements Supplier<T>, Holder<T> {
    private final Identifier id;
    private final Supplier<? extends T> factory;
    private volatile T value;
    private volatile Holder<T> holder;

    public DeferredHolder(Identifier id, Supplier<? extends T> factory) {
        this.id = id;
        this.factory = factory;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public T get() {
        return value();
    }

    @Override
    public T value() {
        var v = value;
        if (v == null) {
            synchronized (this) {
                if (value == null) value = factory.get();
                v = value;
            }
        }
        return v;
    }

    @SuppressWarnings("unchecked")
    private Holder<T> holder() {
        var h = holder;
        if (h == null) {
            synchronized (this) {
                if (holder == null) {
                    // Registry lookups are valid once the value has been registered.
                    holder = (Holder<T>) net.minecraft.core.registries.BuiltInRegistries.REGISTRY
                            .stream()
                            .filter(reg -> reg.containsKey(id))
                            .findFirst()
                            .map(reg -> reg.get(id).orElse(null))
                            .orElse(null);
                }
                h = holder;
            }
        }
        return h;
    }

    @SuppressWarnings("unchecked")
    void bind(R value) {
        this.value = (T) value;
    }

    @Override
    public boolean isBound() {
        return value != null;
    }

    public boolean isPresent() {
        return isBound();
    }

    @Override
    public boolean is(Identifier id) {
        return id.equals(this.id);
    }

    @Override
    public boolean is(ResourceKey<T> key) {
        return key.identifier().equals(id);
    }

    @Override
    public boolean is(Predicate<ResourceKey<T>> predicate) {
        return false;
    }

    @Override
    public boolean is(TagKey<T> tag) {
        return false;
    }

    @Override
    public boolean is(Holder<T> other) {
        return other.value() == value();
    }

    @Override
    public Stream<TagKey<T>> tags() {
        return Stream.empty();
    }

    @Override
    public Either<ResourceKey<T>, T> unwrap() {
        return Either.right(value());
    }

    @Override
    public Optional<ResourceKey<T>> unwrapKey() {
        return Optional.empty();
    }

    @Override
    public Holder.Kind kind() {
        return Holder.Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> owner) {
        return true;
    }

    @Override
    public String toString() {
        return "DeferredHolder[" + id + "]";
    }
}
