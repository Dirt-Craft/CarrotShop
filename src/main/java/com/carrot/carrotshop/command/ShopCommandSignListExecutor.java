package com.carrot.carrotshop.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import com.carrot.carrotshop.util.CommandSignList;

public class ShopCommandSignListExecutor implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        CommandSignList.build(src).sendTo(src);
        return CommandResult.success();
    }

}