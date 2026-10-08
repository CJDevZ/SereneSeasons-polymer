package eu.cj4.sereneseasonspatch.impl;

import eu.cj4.sereneseasonspatch.api.biome.PatchedBiome;
import eu.cj4.sereneseasonspatch.impl.registry.RegistryOverlay;
import eu.cj4.sereneseasonspatch.impl.util.PacketUtil;
import eu.cj4.sereneseasonspatch.impl.util.SeasonBiomeUtil;
import eu.cj4.sereneseasonspatch.mixin.mod.ModFertilityAccessor;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import sereneseasons.api.SSItems;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;

import java.util.*;

public class SereneSeasonsPolymerPatch implements ModInitializer {
    public static final String MOD_ID = "serene-seasons-polymer-patch";
    public static RegistryOverlay<Biome> SEASON_BIOMES = new RegistryOverlay<>(Registries.BIOME);
    public static final Identifier SYNC_SEASON_CYCLE = Identifier.fromNamespaceAndPath(SereneSeasons.MOD_ID, "sync_season_cycle");

    private static final HashMap<String, Integer> SEED_SEASONS = ModFertilityAccessor.getSeedSeasons();

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets(SereneSeasons.MOD_ID);
        PolymerResourcePackUtils.addModAssets(MOD_ID);

        PolymerItemUtils.CONTEXT_ITEM_CHECK.register((instance, _) ->
                SEED_SEASONS.containsKey(instance.typeHolder().unwrapKey().orElseThrow().identifier().toString()));

        ServerPlayerEvents.JOIN.register(player -> {
            PacketContext context = player.getPacketContext();
            context.set(PacketUtil.HAS_MOD, ServerPlayNetworking.canSend(player, SYNC_SEASON_CYCLE));
            updateSeasonContext(context, player.level());
        });
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, _, destination) ->
                updateSeasonContext(player.getPacketContext(), destination));

        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (client || !SeasonBiomeUtil.IS_WORLD_LOAD.orElse(false)) return;
            Registry<Biome> biomeRegistry = registries.lookupOrThrow(Registries.BIOME);
            int offsetId = biomeRegistry.size();
            SEASON_BIOMES.clear(offsetId);
            for (Map.Entry<ResourceKey<Biome>, Biome> entry : biomeRegistry.entrySet()) {
                Holder<Biome> holder = biomeRegistry.wrapAsHolder(entry.getValue());
                if (holder.is(ModTags.Biomes.BLACKLISTED_BIOMES)) continue;
                PatchedBiome patchedBiome = PatchedBiome.of(holder.value());
                patchedBiome.sereneSeasons$clearPatches();
                Identifier biomeId = entry.getKey().identifier();

                if (holder.is(ModTags.Biomes.TROPICAL_BIOMES)) {
                    for (Season.TropicalSeason tropicalSeason : Season.TropicalSeason.VALUES) {
                        Identifier identifier = biomeId.withSuffix("/" + tropicalSeason.name().toLowerCase(Locale.ROOT));
                        patchedBiome.sereneSeasons$addTropicalSeason(
                                tropicalSeason,
                                SEASON_BIOMES.register(identifier, SeasonBiomeUtil.createSeasonBiome(holder, tropicalSeason))
                        );
                    }
                } else {
                    for (Season.SubSeason subSeason : Season.SubSeason.VALUES) {
                        Identifier identifier = biomeId.withSuffix("/" + subSeason.getSerializedName());
                        patchedBiome.sereneSeasons$addSubSeason(
                                subSeason,
                                SEASON_BIOMES.register(identifier, SeasonBiomeUtil.createSeasonBiome(holder, subSeason))
                        );
                    }
                }
            }
        });
    }

    public static void updateSeasonContext(@NonNull PacketContext context, @NonNull Level level) {
        ISeasonState seasonState = SeasonHelper.getSeasonState(level);
        context.set(PacketUtil.DIMENSION_CONTEXT, level.dimension());
        context.set(PacketUtil.SUB_SEASON_CONTEXT, seasonState.getSubSeason());
        context.set(PacketUtil.TROPICAL_SEASON_CONTEXT, seasonState.getTropicalSeason());
    }

    public static void onSeasonChange(@NonNull ServerLevel serverLevel) {
        if (!ModConfig.seasons.isDimensionWhitelisted(serverLevel.dimension())) return;
        for (ServerPlayer serverPlayer : serverLevel.players()) {
            // Update Calendars
            PacketUtil.updateItem(serverPlayer, SSItems.CALENDAR);

            // Resend Biomes
            if (ServerPlayNetworking.canSend(serverPlayer, SYNC_SEASON_CYCLE)) continue;
            List<LevelChunk> chunkList = new ArrayList<>();
            serverPlayer.getChunkTrackingView().forEach(chunkPos ->
                    chunkList.add(serverLevel.getChunk(chunkPos.x(), chunkPos.z()))
            );
            serverPlayer.connection.send(PacketContext.supplyWithContext(serverPlayer, () -> ClientboundChunksBiomesPacket.forChunks(chunkList)));
        }
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
