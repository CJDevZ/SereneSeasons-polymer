package eu.cj4.sereneseasonspatch.mixin.mod;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.init.ModBlockEntities;

import java.util.Set;
import java.util.function.BiConsumer;

@Mixin(ModBlockEntities.class)
public class ModBlockEntitiesMixin {
    @Inject(method = "register", at = @At("RETURN"))
    private static <T extends BlockEntity> void registerPolymerBlockEntity(BiConsumer<Identifier, BlockEntityType<?>> func, String name, BlockEntityType.BlockEntitySupplier<T> supplier, Set<Block> blocks, CallbackInfoReturnable<BlockEntityType<?>> cir) {
        PolymerBlockUtils.registerBlockEntity(cir.getReturnValue());
    }
}
