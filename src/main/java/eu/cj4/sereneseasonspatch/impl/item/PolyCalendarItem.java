package eu.cj4.sereneseasonspatch.impl.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import glitchcore.event.client.ItemTooltipEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModClient;
import sereneseasons.init.ModConfig;
import sereneseasons.item.CalendarType;
import sereneseasons.season.SeasonTime;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Collections;
import java.util.List;

public record PolyCalendarItem() implements PolymerItem {
    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.TRIAL_KEY;
    }

    @Override
    public void modifyClientTooltip(List<Component> tooltip, ItemStack stack, PacketContext context) {
        ModClient.onItemTooltip(new ItemTooltipEvent(context.getPlayer(), stack, tooltip));
    }

    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context) {
        ServerPlayer serverPlayer = context.getPlayer();
        Level level = serverPlayer.level();

        int seasonCycleTicks = SeasonHelper.getSeasonState(level).getSeasonCycleTicks();
        double progress = (double)seasonCycleTicks / (double) SeasonTime.ZERO.getCycleDuration();

        CalendarType calendarType = ModConfig.seasons.isDimensionWhitelisted(level.dimension())
                ? /*level.getBiome(serverPlayer.blockPosition()).is(ModTags.Biomes.TROPICAL_BIOMES) ? CalendarType.TROPICAL : */CalendarType.STANDARD
                : CalendarType.NONE;

        out.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(
                Collections.singletonList((float) Mth.positiveModulo(progress, 1.0D)),
                Collections.emptyList(),
                Collections.singletonList(calendarType.getSerializedName()),
                Collections.emptyList()
        ));
    }
}
