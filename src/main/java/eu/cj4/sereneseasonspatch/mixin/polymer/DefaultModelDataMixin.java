package eu.cj4.sereneseasonspatch.mixin.polymer;

import eu.cj4.sereneseasonspatch.poly.ExtraBlockModelTypes;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.impl.DefaultModelData;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(DefaultModelData.class)
public class DefaultModelDataMixin {
    @Shadow
    @Final
    public static Map<BlockModelType, List<BlockState>> USABLE_STATES;

    @Shadow
    @Final
    public static Map<BlockState, BlockState> SPECIAL_REMAPS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addExtraModelTypes(CallbackInfo ci) {
        {
            var list = new ReferenceArrayList<BlockState>();
            BlockState defaultState = Blocks.DAYLIGHT_DETECTOR.defaultBlockState();
            BlockState invertedDefaultState = defaultState.setValue(DaylightDetectorBlock.INVERTED, true);
            for (boolean inverted : new boolean[]{ false, true }) {
                for (int i = 1; i <= 15; i++) {
                    var state = (inverted ? invertedDefaultState : defaultState).setValue(DaylightDetectorBlock.POWER, i);
                    list.add(state);
                    if (inverted) SPECIAL_REMAPS.put(state, invertedDefaultState);
                }
            }

            USABLE_STATES.put(ExtraBlockModelTypes.SERENE_SEASONS_POLYMER_PATCH_DAYLIGHT_DETECTOR, list);
        }
    }
}
