package com.carrot.carrotshop.command;

import com.carrot.carrotshop.ShopsData;
import com.carrot.carrotshop.util.CommandSignList;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.io.File;

public class ShopCommandSignRemoveExecutor implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        File cmdFile = new File(ShopsData.getCmdsDirs(), args.<String>getOne("command").get() + ".txt");
        cmdFile.delete();
        if (args.hasAny("l")){
            CommandSignList.build(src).sendTo(src);
        }
        return CommandResult.success();
    }

}