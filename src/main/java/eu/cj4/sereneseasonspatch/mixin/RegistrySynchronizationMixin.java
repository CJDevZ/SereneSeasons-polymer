package eu.cj4.sereneseasonspatch.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import eu.cj4.sereneseasonspatch.impl.SereneSeasonsPolymerPatch;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

@Mixin(RegistrySynchronization.class)
public class RegistrySynchronizationMixin {
    @ModifyExpressionValue(method = "method_56596", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;listElements()Ljava/util/stream/Stream;"))
    private static Stream<Holder.Reference<Biome>> overlayRegistryData(
            Stream<Holder.Reference<Biome>> original,
            @Local(argsOnly = true, ordinal = 0) Registry<?> registry
    ) {
        return registry.key() == Registries.BIOME && SereneSeasonsPolymerPatch.SEASON_BIOMES != null
                ? Stream.concat(original, SereneSeasonsPolymerPatch.SEASON_BIOMES.listElements()) : original;
    }
}
