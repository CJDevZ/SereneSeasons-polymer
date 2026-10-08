package eu.cj4.sereneseasonspatch.mixin;

import glitchcore.event.client.ItemTooltipEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.init.ModFertility;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getTooltipLines", at = @At(value = "RETURN", ordinal = 1))
    private void addFertilityTooltip(Item.TooltipContext context, @Nullable Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir) {
        ItemTooltipEvent event = new ItemTooltipEvent(player, (ItemStack) (Object) this, cir.getReturnValue());
        ModFertility.setupTooltips(event);
    }
}
