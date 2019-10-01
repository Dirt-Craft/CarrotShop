package com.carrot.carrotshop.api;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.flowpowered.math.vector.Vector3i;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

public class FtbUtilitiesIntegration {
    @Listener(beforeModifications = true)
    public void onPlayerLeftClickNormal(InteractBlockEvent.Primary.MainHand event, @First Player player){
        if (!player.hasPermission("carrotshop.ftbutilities.teams.view")) return;
        final Vector3i pos = event.getTargetBlock().getPosition();
        Optional<ItemStack> optItem = player.getItemInHand(HandTypes.MAIN_HAND);
        if (!optItem.isPresent() || optItem.get().getType() != ItemTypes.BLAZE_ROD) return;

        final ClaimedChunks claimedChunks = ClaimedChunks.instance;
        final BlockPos blockPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        final ChunkDimPos chunkDimPos = new ChunkDimPos(blockPos, ((EntityPlayerMP)player).dimension);
        final ForgeTeam team = claimedChunks.getChunkTeam(chunkDimPos);

        if (team == null) return;
        player.sendMessage(Text.of("Team Members:"));
        player.sendMessage(Text.of(team.owner.getName()));
        List<ForgePlayer> members = team.getMembers();
        members.remove(team.owner);
        for (ForgePlayer member : members){
            player.sendMessage(Text.of(member.getName()));
        }
    }
}
