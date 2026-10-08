package eu.cj4.sereneseasonspatch.mixin;

import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(BiomeSpecialEffects.class)
public interface BiomeSpecialEffectsAccessor {
    @Invoker("<init>")
    static BiomeSpecialEffects createInstance(
            int fogColor,
            int waterColor,
            int waterFogColor,
            int skyColor,
            Optional<Integer> foliageColorOverride,
            Optional<Integer> dryFoliageColorOverride,
            Optional<Integer> grassColorOverride,
            BiomeSpecialEffects.GrassColorModifier grassColorModifier,
            Optional<AmbientParticleSettings> ambientParticleSettings,
            Optional<Holder<SoundEvent>> ambientLoopSoundEvent,
            Optional<AmbientMoodSettings> ambientMoodSettings,
            Optional<AmbientAdditionsSettings> ambientAdditionsSettings,
            Optional<WeightedList<Music>> backgroundMusicVolume,
            float f
    ) {
        throw new UnsupportedOperationException();
    }
}
