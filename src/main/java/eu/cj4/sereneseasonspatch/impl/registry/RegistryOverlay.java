package eu.cj4.sereneseasonspatch.impl.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RegistryOverlay<T> implements HolderOwner<T> {
    private final ResourceKey<? extends Registry<T>> resourceKey;
    private final List<Holder.Reference<T>> elements;
    private int nextId;

    public RegistryOverlay(ResourceKey<? extends Registry<T>> resourceKey) {
        this.resourceKey = resourceKey;
        this.elements = new ArrayList<>();
    }

    public void clear(int idOffset) {
        this.elements.clear();
        this.nextId = idOffset;
    }

    public int register(ResourceLocation key, T value) {
        this.elements.add(new Key(this, ResourceKey.create(this.resourceKey, key), value));
        return nextId++;
    }

    public Stream<Holder.Reference<T>> listElements() {
        return elements.stream();
    }

    protected class Key extends Holder.Reference<T> {
        public Key(HolderOwner<T> owner, @Nullable ResourceKey<T> key, @Nullable T value) {
            super(Type.INTRUSIVE, owner, key, value);
        }
    }
}
