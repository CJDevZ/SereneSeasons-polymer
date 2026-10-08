package eu.cj4.sereneseasonspatch.api.biome;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;
import sereneseasons.api.season.Season;
import xyz.nucleoid.packettweaker.PacketContext;

public interface PatchedBiome {
    @Nullable Integer sereneSeasons$getReplacement(Holder<Biome> object, PacketContext packetContext);
    void sereneSeasons$clearPatches();
    void sereneSeasons$addSubSeason(Season.SubSeason subSeason, int biomeId);
    void sereneSeasons$addTropicalSeason(Season.TropicalSeason tropicalSeason, int biomeId);

    static PatchedBiome of(Biome biome) {
        return (PatchedBiome)(Object) biome;
    }
}
