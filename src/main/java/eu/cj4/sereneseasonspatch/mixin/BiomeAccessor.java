package eu.cj4.sereneseasonspatch.mixin;

import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Biome.class)
public interface BiomeAccessor {
    @Invoker("<init>")
    static Biome createInstance(final Biome.ClimateSettings climateSettings, final EnvironmentAttributeMap attributes, final BiomeSpecialEffects specialEffects, final BiomeGenerationSettings generationSettings) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    Biome.ClimateSettings getClimateSettings();
}
