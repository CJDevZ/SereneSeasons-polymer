package eu.cj4.sereneseasonspatch.impl.util;

import eu.cj4.sereneseasonspatch.mixin.BiomeAccessor;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.api.season.Season;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;
import sereneseasons.util.SeasonColorUtil;

public class SeasonBiomeUtil {
    public static final ScopedValue<Boolean> IS_WORLD_LOAD = ScopedValue.newInstance();

    public static Biome createSeasonBiome(Holder<Biome> holder, ISeasonColorProvider colorProvider) {
        Biome biome = holder.value();
        var originalEffects = biome.getSpecialEffects();
        var originalClimate = ((BiomeAccessor)(Object) biome).getClimateSettings();

        var climateSettings = new Biome.ClimateSettings(
                originalClimate.hasPrecipitation(),
                colorProvider instanceof Season.SubSeason subSeason && subSeason.getSeason() == Season.WINTER ? 0.0f : originalClimate.temperature(),
                originalClimate.temperatureModifier(),
                originalClimate.downfall()
        );
        var specialEffects = new BiomeSpecialEffects(
                originalEffects.waterColor(),
                originalEffects.grassColorOverride().map(color -> applySeasonalFoliageColouring(colorProvider, holder, color)),
                originalEffects.dryFoliageColorOverride(),
                originalEffects.grassColorOverride().map(color -> applySeasonalGrassColouring(colorProvider, holder, color)),
                originalEffects.grassColorModifier()
        );
        return BiomeAccessor.createInstance(
                climateSettings,
                biome.getAttributes(),
                specialEffects,
                BiomeGenerationSettings.EMPTY
        );
    }

    public static int applySeasonalGrassColouring(ISeasonColorProvider colorProvider, Holder<Biome> biome, int originalColour) {
        if (biome.is(ModTags.Biomes.BLACKLISTED_BIOMES)) {
            return originalColour;
        }
        int overlay = colorProvider.getGrassOverlay();
        float saturationMultiplier = colorProvider.getGrassSaturationMultiplier();
        if (!ModConfig.seasons.changeGrassColor) {
            overlay = Season.SubSeason.MID_SUMMER.getGrassOverlay();
            saturationMultiplier = Season.SubSeason.MID_SUMMER.getGrassSaturationMultiplier();
        }

        int newColour = SeasonColorUtil.overlayBlend(originalColour, overlay);
        if (biome.is(ModTags.Biomes.LESSER_COLOR_CHANGE_BIOMES)) {
            newColour = SeasonColorUtil.mixColours(newColour, originalColour, 0.75F);
        }

        return SeasonColorUtil.saturateColour(newColour, saturationMultiplier);
    }

    public static int applySeasonalFoliageColouring(ISeasonColorProvider colorProvider, Holder<Biome> biome, int originalColour) {
        if (biome.is(ModTags.Biomes.BLACKLISTED_BIOMES)) {
            return originalColour;
        }
        int overlay = colorProvider.getFoliageOverlay();
        float saturationMultiplier = colorProvider.getFoliageSaturationMultiplier();
        if (!ModConfig.seasons.changeFoliageColor) {
            overlay = Season.SubSeason.MID_SUMMER.getFoliageOverlay();
            saturationMultiplier = Season.SubSeason.MID_SUMMER.getFoliageSaturationMultiplier();
        }

        int newColour = SeasonColorUtil.overlayBlend(originalColour, overlay);
        if (biome.is(ModTags.Biomes.LESSER_COLOR_CHANGE_BIOMES)) {
            newColour = SeasonColorUtil.mixColours(newColour, originalColour, 0.75F);
        }

        return SeasonColorUtil.saturateColour(newColour, saturationMultiplier);
    }
}
