package eu.cj4.sereneseasonspatch.impl;

import eu.cj4.sereneseasonspatch.api.biome.PatchedBiome;
import eu.cj4.sereneseasonspatch.impl.registry.RegistryOverlay;
import eu.cj4.sereneseasonspatch.impl.util.PacketUtil;
import eu.cj4.sereneseasonspatch.impl.util.SeasonBiomeUtil;
import eu.cj4.sereneseasonspatch.mixin.mod.ModFertilityAccessor;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import sereneseasons.api.SSItems;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.*;

public class SereneSeasonsPolymerPatch implements ModInitializer {
    public static final String MOD_ID = "serene-seasons-polymer-patch";
    public static RegistryOverlay<Biome> SEASON_BIOMES = new RegistryOverlay<>(Registries.BIOME);
    public static final ResourceLocation SYNC_SEASON_CYCLE = ResourceLocation.fromNamespaceAndPath(SereneSeasons.MOD_ID, "sync_season_cycle");

    private static final HashMap<String, Integer> SEED_SEASONS = ModFertilityAccessor.getSeedSeasons();

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets(SereneSeasons.MOD_ID);
        PolymerResourcePackUtils.addModAssets(MOD_ID);

        PolymerItemUtils.ITEM_CHECK.register((instance) ->
                SEED_SEASONS.containsKey(instance.getItemHolder().unwrapKey().orElseThrow().location().toString()));

        ServerPlayerEvents.JOIN.register(player -> {
            PacketContext context = PacketContext.create(player);
            context.setData(PacketUtil.HAS_MOD, ServerPlayNetworking.canSend(player, SYNC_SEASON_CYCLE));
            updateSeasonContext(context, player.level());
        });
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, serverLevel, destination) ->
                updateSeasonContext(PacketContext.create(player), destination));

        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (client || !Boolean.TRUE.equals(SeasonBiomeUtil.IS_WORLD_LOAD.get())) return;
            Registry<Biome> biomeRegistry = registries.lookupOrThrow(Registries.BIOME);
            int offsetId = biomeRegistry.size();
            SEASON_BIOMES.clear(offsetId);
            for (Map.Entry<ResourceKey<Biome>, Biome> entry : biomeRegistry.entrySet()) {
                Holder<Biome> holder = biomeRegistry.wrapAsHolder(entry.getValue());
                if (holder.is(ModTags.Biomes.BLACKLISTED_BIOMES)) continue;
                PatchedBiome patchedBiome = PatchedBiome.of(holder.value());
                patchedBiome.sereneSeasons$clearPatches();
                ResourceLocation biomeId = entry.getKey().location();

                if (holder.is(ModTags.Biomes.TROPICAL_BIOMES)) {
                    for (Season.TropicalSeason tropicalSeason : Season.TropicalSeason.VALUES) {
                        if (tropicalSeason.getGrassOverlay() == 0xFFFFFF
                                && tropicalSeason.getGrassSaturationMultiplier() == -1.0F
                                && tropicalSeason.getFoliageOverlay() == 0xFFFFFF
                                && tropicalSeason.getFoliageSaturationMultiplier() == -1.0F
                        ) continue;
                        ResourceLocation identifier = biomeId.withSuffix("/" + tropicalSeason.name().toLowerCase(Locale.ROOT));
                        patchedBiome.sereneSeasons$addTropicalSeason(
                                tropicalSeason,
                                SEASON_BIOMES.register(identifier, SeasonBiomeUtil.createSeasonBiome(holder, tropicalSeason))
                        );
                    }
                } else {
                    for (Season.SubSeason subSeason : Season.SubSeason.VALUES) {
                        if (subSeason.getGrassOverlay() == 0xFFFFFF
                                && subSeason.getGrassSaturationMultiplier() == -1.0F
                                && subSeason.getFoliageOverlay() == 0xFFFFFF
                                && subSeason.getFoliageSaturationMultiplier() == -1.0F
                        ) continue;
                        ResourceLocation identifier = biomeId.withSuffix("/" + subSeason.getSerializedName());
                        patchedBiome.sereneSeasons$addSubSeason(
                                subSeason,
                                SEASON_BIOMES.register(identifier, SeasonBiomeUtil.createSeasonBiome(holder, subSeason))
                        );
                    }
                }
            }
        });
    }

    public static void updateSeasonContext(@NotNull PacketContext context, @NotNull Level level) {
        ISeasonState seasonState = SeasonHelper.getSeasonState(level);
        context.setData(PacketUtil.DIMENSION_CONTEXT, level.dimension());
        context.setData(PacketUtil.SUB_SEASON_CONTEXT, seasonState.getSubSeason());
        context.setData(PacketUtil.TROPICAL_SEASON_CONTEXT, seasonState.getTropicalSeason());
    }

    public static void onSeasonChange(@NotNull ServerLevel serverLevel) {
        if (!ModConfig.seasons.isDimensionWhitelisted(serverLevel.dimension())) return;
        for (ServerPlayer serverPlayer : serverLevel.players()) {
            // Update Calendars
            PacketUtil.updateItem(serverPlayer, SSItems.CALENDAR);

            // Resend Biomes
            if (ServerPlayNetworking.canSend(serverPlayer, SYNC_SEASON_CYCLE)) continue;
            List<LevelChunk> chunkList = new ArrayList<>();
            serverPlayer.getChunkTrackingView().forEach(chunkPos ->
                    chunkList.add(serverLevel.getChunk(chunkPos.x, chunkPos.z))
            );
            serverPlayer.connection.send(PacketContext.supplyWithContext(serverPlayer.connection, () -> ClientboundChunksBiomesPacket.forChunks(chunkList)));
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
