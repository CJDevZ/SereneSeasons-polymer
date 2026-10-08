package eu.cj4.sereneseasonspatch.mixin;

import eu.cj4.sereneseasonspatch.api.biome.PatchedBiome;
import eu.cj4.sereneseasonspatch.impl.util.PacketUtil;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sereneseasons.api.season.Season;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;

import java.util.Arrays;

@Mixin(Biome.class)
public class BiomeMixin implements PatchedBiome {
    @Unique
    private Integer[] sereneSeasons$seasonMap;
    @Unique
    private Integer[] sereneSeasons$tropicalSeasonMap;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initSeasonMap(CallbackInfo ci) {
        sereneSeasons$seasonMap = new Integer[Season.SubSeason.VALUES.length];
        sereneSeasons$tropicalSeasonMap = new Integer[Season.TropicalSeason.VALUES.length];
    }

    @Override
    public Integer sereneSeasons$getReplacement(Holder<Biome> object, PacketContext packetContext) {
        if (packetContext.orElseThrow(PacketUtil.HAS_MOD)
                || !ModConfig.seasons.isDimensionWhitelisted(packetContext.orElseThrow(PacketUtil.DIMENSION_CONTEXT))) return null;
        return object.is(ModTags.Biomes.TROPICAL_BIOMES)
                ? sereneSeasons$tropicalSeasonMap[packetContext.orElseThrow(PacketUtil.TROPICAL_SEASON_CONTEXT).ordinal()]
                : sereneSeasons$seasonMap[packetContext.orElseThrow(PacketUtil.SUB_SEASON_CONTEXT).ordinal()];
    }

    @Override
    public void sereneSeasons$clearPatches() {
        Arrays.fill(sereneSeasons$seasonMap, null);
        Arrays.fill(sereneSeasons$tropicalSeasonMap, null);
    }

    @Override
    public void sereneSeasons$addSubSeason(Season.SubSeason subSeason, int biomeId) {
        this.sereneSeasons$seasonMap[subSeason.ordinal()] = biomeId;
    }

    @Override
    public void sereneSeasons$addTropicalSeason(Season.TropicalSeason tropicalSeason, int biomeId) {
        this.sereneSeasons$tropicalSeasonMap[tropicalSeason.ordinal()] = biomeId;
    }
}
