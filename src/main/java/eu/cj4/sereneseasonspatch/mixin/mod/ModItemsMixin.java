package eu.cj4.sereneseasonspatch.mixin.mod;

import eu.cj4.sereneseasonspatch.impl.item.PolyBaseItem;
import eu.cj4.sereneseasonspatch.impl.item.PolyCalendarItem;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.init.ModItems;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Mixin(ModItems.class)
public class ModItemsMixin {
    @Inject(method = "registerItem(Ljava/util/function/BiConsumer;Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", at = @At("RETURN"))
    private static void registerPolymerItem(BiConsumer<ResourceLocation, Item> func, ResourceKey<Item> key, Function<Item.Properties, Item> factory, Item.Properties properties, CallbackInfoReturnable<Item> cir) {
        var name = key.location().getPath();

        PolymerItem polymerItem;
        if (name.equals("calendar")) {
            polymerItem = new PolyCalendarItem();
        } else {
            polymerItem = new PolyBaseItem(cir.getReturnValue());
        }

        PolymerItem.registerOverlay(cir.getReturnValue(), polymerItem);
    }
}
