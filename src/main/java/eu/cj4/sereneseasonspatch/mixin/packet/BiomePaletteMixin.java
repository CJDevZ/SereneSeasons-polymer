package eu.cj4.sereneseasonspatch.mixin.packet;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.cj4.sereneseasonspatch.api.biome.PatchedBiome;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.HashMapPalette;
import net.minecraft.world.level.chunk.LinearPalette;
import net.minecraft.world.level.chunk.SingleValuePalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {LinearPalette.class, SingleValuePalette.class, HashMapPalette.class}, priority = 500)
public class BiomePaletteMixin {
    @WrapOperation(method = { "write", "getSerializedSize" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/core/IdMap;getId(Ljava/lang/Object;)I"))
    public <T> int getIdRedirect(IdMap<T> instance, T object, Operation<Integer> original) {
        PacketContext context = PacketContext.get();
        if (context != null && object instanceof Holder<?> holder && holder.value() instanceof Biome biome) {
            Integer biomeId = ((PatchedBiome) (Object) biome)
                    .sereneSeasons$getReplacement((Holder<Biome>) holder, context);
            if (biomeId != null) return biomeId;
        }
        return original.call(instance, object);
    }
}
