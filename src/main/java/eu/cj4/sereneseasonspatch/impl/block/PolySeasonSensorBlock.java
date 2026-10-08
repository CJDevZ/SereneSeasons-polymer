package eu.cj4.sereneseasonspatch.impl.block;

import eu.cj4.sereneseasonspatch.poly.ExtraBlockModelTypes;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;
import sereneseasons.api.season.Season;
import sereneseasons.block.SeasonSensorBlock;

import java.util.Locale;

public record PolySeasonSensorBlock() implements PolymerTexturedBlock {
    @Unique
    private static final BlockState[] SEASON_STATES;

    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext context) {
        return SEASON_STATES[state.getValue(SeasonSensorBlock.SEASON)];
    }

    static {
        Identifier sensorModel = Identifier.parse("sereneseasons:block/season_sensor");
        SEASON_STATES = Util.makeEnumMap(Season.class, season -> {
            Identifier model = season == Season.SPRING ? sensorModel : sensorModel.withSuffix("_" + season.name().toLowerCase(Locale.ROOT));
            BlockState state = PolymerBlockResourceUtils.requestBlock(ExtraBlockModelTypes.SERENE_SEASONS_POLYMER_PATCH_DAYLIGHT_DETECTOR, PolymerBlockModel.of(model));
            return state == null ? PolymerBlockResourceUtils.requestEmpty(ExtraBlockModelTypes.SERENE_SEASONS_POLYMER_PATCH_DAYLIGHT_DETECTOR) : state;
        }).values().toArray(new BlockState[0]);
    }
}
