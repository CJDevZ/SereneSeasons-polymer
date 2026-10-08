package eu.cj4.sereneseasonspatch.impl.util;

import eu.pb4.polymer.core.mixin.other.AbstractContainerMenuAccessor;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.Season;

import static eu.cj4.sereneseasonspatch.impl.SereneSeasonsPolymerPatch.id;

public final class PacketUtil {
    public static final PacketContext.Key<Boolean> HAS_MOD = PacketContext.key(id("has_mod"));
    public static final PacketContext.Key<ResourceKey<Level>> DIMENSION_CONTEXT = PacketContext.key(id("dimension"));
    public static final PacketContext.Key<Season.SubSeason> SUB_SEASON_CONTEXT = PacketContext.key(id("sub_season"));
    public static final PacketContext.Key<Season.TropicalSeason> TROPICAL_SEASON_CONTEXT = PacketContext.key(id("tropical_season"));

    private PacketUtil() {
    }

    public static void updateItem(ServerPlayer serverPlayer, Item item) {
        AbstractContainerMenu containerMenu = serverPlayer.containerMenu;
        ContainerSynchronizer containerSynchronizer = ((AbstractContainerMenuAccessor) containerMenu).getSynchronizer();
        NonNullList<Slot> slots = containerMenu.slots;
        for (int i = 0, slotsSize = slots.size(); i < slotsSize; i++) {
            Slot slot = slots.get(i);
            ItemStack stack = slot.getItem();
            if (!stack.is(item)) continue;
            containerSynchronizer.sendSlotChange(containerMenu, i, stack);
        }
    }
}
