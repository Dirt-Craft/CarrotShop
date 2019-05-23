package com.carrot.carrotshop.command;

import com.carrot.carrotshop.CarrotShop;
import com.carrot.carrotshop.Lang;
import com.carrot.carrotshop.ShopsData;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.*;

public class ShopCommandSignSetExecutor implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        File cmdFile = new File(ShopsData.getCmdsDirs(), args.<String>getOne("name").get() + ".txt");
        String command = args.<String>getOne("command").get();
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(cmdFile));
            writer.write(command);
            writer.close();
            src.sendMessage(Text.of(TextColors.DARK_GREEN, "Command set."));
        } catch (FileNotFoundException e) {
            CarrotShop.getLogger().info("Cmd file not found: " + cmdFile.getAbsolutePath());
            if (src instanceof Player) {
                src.sendMessage(Text.of(TextColors.DARK_RED, Lang.SHOP_CMD_ERROR_FILE404));
            }
        } catch (IOException e) {
            CarrotShop.getLogger().info("Error with cmd file: " + cmdFile.getAbsolutePath());
            if (src instanceof Player) {
                src.sendMessage(Text.of(TextColors.DARK_RED, Lang.SHOP_CMD_ERROR));
            }
            e.printStackTrace();
        }
        return CommandResult.success();
    }

}