package eu.cj4.sereneseasonspatch.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @WrapOperation(method = "method_49420", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundChunksBiomesPacket;forChunks(Ljava/util/List;)Lnet/minecraft/network/protocol/game/ClientboundChunksBiomesPacket;"))
    private static ClientboundChunksBiomesPacket addContext(
            List<LevelChunk> chunks,
            Operation<ClientboundChunksBiomesPacket> original,
            @Local(argsOnly = true, ordinal = 0) ServerPlayer player
    ) {
        return PacketContext.supplyWithContext(player.connection, () -> original.call(chunks));
    }
}
