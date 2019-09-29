package com.carrot.carrotshop.api;

import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.flowpowered.math.vector.Vector3i;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;

public interface ClaimApi {
    boolean isUnprotectedOrOwned(Player player, BlockSnapshot blockSnapshot);

    class DummyClaimApi implements ClaimApi{
        public boolean isUnprotectedOrOwned(Player player, BlockSnapshot blockSnapshot){
            return true;
        }

    }
    class FtbClaimApi implements ClaimApi{
        public boolean isUnprotectedOrOwned(Player spongePlayer, BlockSnapshot blockSnapshot){
            Vector3i xyz = blockSnapshot.getPosition();
            EntityPlayer player = FMLCommonHandler.instance()
                    .getMinecraftServerInstance()
                    .getPlayerList()
                    .getPlayerByUUID(spongePlayer.getUniqueId());
            return ClaimedChunks.blockBlockEditing(player, new BlockPos(xyz.getX(), xyz.getY(), xyz.getZ()), null);
        }
    }
}
