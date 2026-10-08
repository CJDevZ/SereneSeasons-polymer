package eu.cj4.sereneseasonspatch.mixin.mod;

import com.llamalad7.mixinextras.sugar.Local;
import eu.cj4.sereneseasonspatch.impl.SereneSeasonsPolymerPatch;
import eu.cj4.sereneseasonspatch.impl.util.PacketUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sereneseasons.api.season.Season;
import sereneseasons.season.SeasonHandler;
import sereneseasons.season.SeasonTime;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.HashMap;

@Mixin(SeasonHandler.class)
public class SeasonHandlerMixin {
    @Shadow
    @Final
    public static HashMap<ResourceKey<Level>, Integer> prevServerSeasonCycleTicks;

    @Inject(method = "sendSeasonUpdate", at = @At(value = "INVOKE", target = "Ljava/util/HashMap;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"))
    private static void updateBiomes(
            Level level,
            CallbackInfo ci,
            @Local(name = "newTime") SeasonTime newTime
    ) {
        Integer prevSeasonCycleTicks = prevServerSeasonCycleTicks.get(level.dimension());
        ServerLevel serverLevel = (ServerLevel) level;
        Season.SubSeason newSubSeason = newTime.getSubSeason();
        Season.TropicalSeason newTropicalSeason = newTime.getTropicalSeason();

        if (prevSeasonCycleTicks != null) {
            SeasonTime prevTime = new SeasonTime(prevSeasonCycleTicks);
            if (prevTime.getSubSeason().equals(newSubSeason) && prevTime.getTropicalSeason().equals(newTropicalSeason)) {
                return;
            }
        }

        for (ServerPlayer player : serverLevel.players()) {
            PacketContext context = PacketContext.create(player);
            context.setData(PacketUtil.SUB_SEASON_CONTEXT, newSubSeason);
            context.setData(PacketUtil.TROPICAL_SEASON_CONTEXT, newTropicalSeason);
        }
        SereneSeasonsPolymerPatch.onSeasonChange(serverLevel);
    }
}
