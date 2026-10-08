package eu.cj4.sereneseasonspatch.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.cj4.sereneseasonspatch.impl.util.SeasonBiomeUtil;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
    @WrapOperation(method = "method_42097", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;updateStaticRegistryTags()V"))
    private static void setupFakeBiomes(ReloadableServerResources instance, Operation<Void> original) {
        Boolean oldValue = SeasonBiomeUtil.IS_WORLD_LOAD.get();
        SeasonBiomeUtil.IS_WORLD_LOAD.set(true);
        try {
            original.call(instance);
        } finally {
            SeasonBiomeUtil.IS_WORLD_LOAD.set(oldValue);
        }
    }
}
