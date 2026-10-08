package eu.cj4.sereneseasonspatch.mixin.mod;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.init.ModBlockEntities;

@Mixin(ModBlockEntities.class)
public class ModBlockEntitiesMixin {
    @Inject(method = "register", at = @At("RETURN"))
    private static void registerPolymerBlockEntity(CallbackInfoReturnable<BlockEntityType<?>> cir) {
        PolymerBlockUtils.registerBlockEntity(cir.getReturnValue());
    }
}
