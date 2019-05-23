package com.carrot.carrotshop.command;

import com.carrot.carrotshop.CarrotShop;
import com.carrot.carrotshop.Lang;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.swing.text.html.Option;
import java.util.Optional;

public class ShopCommandSignLinkExecutor implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> command = args.<String>getOne("name");

        if (src instanceof Player) {
            if (command.isPresent()){
                CarrotShop.setLinkedCommand(((Player) src).getUniqueId(), command.get());
                src.sendMessage(Text.of(TextColors.DARK_GREEN, Lang.COMANDSIGN_LINKED ));
            } else {
                CarrotShop.removeLinkedCommand(((Player) src).getUniqueId());
                src.sendMessage(Text.of(TextColors.DARK_GREEN, Lang.COMANDSIGN_UNLINKED ));
            }
        }

        return CommandResult.success();
    }

}