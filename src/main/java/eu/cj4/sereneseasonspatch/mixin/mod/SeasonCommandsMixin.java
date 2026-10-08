package eu.cj4.sereneseasonspatch.mixin.mod;

import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.command.SeasonCommands;

import java.util.function.BiConsumer;

@Mixin(SeasonCommands.class)
public class SeasonCommandsMixin {
    @Inject(method = "register", at = @At("RETURN"))
    private static void patchRegistries(BiConsumer<ResourceLocation, ArgumentTypeInfo<?, ?>> func, String name, Class<?> clazz, ArgumentTypeInfo<?, ?> typeInfo, CallbackInfoReturnable<ArgumentTypeInfo<?, ?>> cir) {
        RegistrySyncUtils.setServerEntry(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, cir.getReturnValue());
    }
}
