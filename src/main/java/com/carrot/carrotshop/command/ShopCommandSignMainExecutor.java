package com.carrot.carrotshop.command;

import com.carrot.carrotshop.Lang;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class ShopCommandSignMainExecutor implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Text> contents = new ArrayList<>();

        contents.add(Text.of(TextColors.GOLD, "/cs cmd list", TextColors.GRAY, " - ", TextColors.YELLOW, Lang.HELP_DESC_CMD_COMMANDSIGN_LIST));
        contents.add(Text.of(TextColors.GOLD, "/cs cmd link", TextColors.GRAY, " - ", TextColors.YELLOW, Lang.HELP_DESC_CMD_COMMANDSIGN_LINK));
        contents.add(Text.of(TextColors.GOLD, "/cs cmd set", TextColors.GRAY, " - ", TextColors.YELLOW, Lang.HELP_DESC_CMD_COMMANDSIGN_SET));
        contents.add(Text.of(TextColors.GOLD, "/cs cmd remove", TextColors.GRAY, " - ", TextColors.YELLOW, Lang.HELP_DESC_CMD_COMMANDSIGN_REMOVE));

        PaginationList.builder()
        .title(Text.of(TextColors.GOLD, "{ ", TextColors.YELLOW, Lang.HELP_HEADER_CMD_COMMANDSIGN, TextColors.GOLD, " }"))
        .contents(contents)
        .padding(Text.of("-"))
        .sendTo(src);

        return CommandResult.success();
    }

}