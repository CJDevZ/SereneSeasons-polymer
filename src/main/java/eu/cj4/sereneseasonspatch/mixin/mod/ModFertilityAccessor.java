package eu.cj4.sereneseasonspatch.mixin.mod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import sereneseasons.init.ModFertility;

import java.util.HashMap;

@Mixin(ModFertility.class)
public interface ModFertilityAccessor {
    @Accessor
    static HashMap<String, Integer> getSeedSeasons() {
        throw new UnsupportedOperationException();
    }
}
