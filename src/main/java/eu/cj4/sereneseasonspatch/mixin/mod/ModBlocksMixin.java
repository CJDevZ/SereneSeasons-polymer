package eu.cj4.sereneseasonspatch.mixin.mod;

import eu.cj4.sereneseasonspatch.impl.block.PolySeasonSensorBlock;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.init.ModBlocks;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Mixin(ModBlocks.class)
public class ModBlocksMixin {
    @Inject(method = "register(Ljava/util/function/BiConsumer;Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", at = @At("RETURN"))
    private static void registerPolymerBlock(BiConsumer<ResourceLocation, Block> func, ResourceKey<Block> key, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties, CallbackInfoReturnable<Block> cir) {
        var name = key.location().getPath();

        if (name.equals("season_sensor")) {
            PolymerBlock.registerOverlay(cir.getReturnValue(), new PolySeasonSensorBlock());
        }
    }
}
