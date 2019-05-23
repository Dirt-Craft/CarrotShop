package com.carrot.carrotshop.util;

import com.carrot.carrotshop.Lang;
import com.carrot.carrotshop.ShopsData;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandSignList {
    public static PaginationList build(CommandSource src) {
        File[] Files = ShopsData.getCmdsDirs().listFiles();
        List<Text> commands = new ArrayList<>();
        for (File file : Files) {
            String command = file.getName().substring(0, file.getName().lastIndexOf('.'));
            Text entry = Text.builder(command)
                    .append(
                            Text.builder(" [R]")
                                    .onClick(TextActions.runCommand("/cs cmd remove " + command + " -l"))
                                    .onHover(TextActions.showText(Text.of(Lang.HOVER_ACTION_REMOVE + " " + command)))
                                    .color(TextColors.RED)
                                    .build(),
                            Text.builder(" [L]")
                                    .onClick(TextActions.runCommand("/cs cmd link " + command))
                                    .onHover(TextActions.showText(Text.of(Lang.HOVER_ACTION_LINK + " " + command)))
                                    .color(TextColors.YELLOW)
                                    .build()
                    )
                    .build();
            commands.add(entry);
        }

        PaginationList result = PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "{ ", TextColors.YELLOW, Lang.HELP_HEADER_CMD_COMMANDSIGN_LIST, TextColors.GOLD, " }"))
                .contents(commands)
                .padding(Text.of("-"))
                .build();
        return result;
    }
}
