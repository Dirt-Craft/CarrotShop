package com.carrot.carrotshop.api;

import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.flowpowered.math.vector.Vector3i;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public interface ClaimApi {
    boolean isProtected(Player player, BlockSnapshot blockSnapshot);

    class DummyClaimApi implements ClaimApi{
        public boolean isProtected(Player player, BlockSnapshot blockSnapshot){
            return false;
        }

    }

    class FtbClaimApi implements ClaimApi{
        public boolean isProtected(Player spongePlayer, BlockSnapshot blockSnapshot){
            Vector3i position = blockSnapshot.getPosition();
            EntityPlayer player = FMLCommonHandler.instance()
                    .getMinecraftServerInstance()
                    .getPlayerList()
                    .getPlayerByUUID(spongePlayer.getUniqueId());
            final BlockPos pos = new BlockPos(
                    position.getX(),
                    position.getY(),
                    position.getZ()
            );
            final boolean isProtected = ClaimedChunks
                    .blockBlockEditing(player, pos, null);
            if (isProtected) spongePlayer.sendMessage(Text.of("§cYou do not have permission to access this inventory."));
            return isProtected;
        }
    }
}
