package com.carrot.carrotshop.shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.carrot.carrotshop.CarrotShop;
import com.carrot.carrotshop.Lang;
import com.carrot.carrotshop.ShopConfig;
import com.carrot.carrotshop.ShopsData;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class DeviceOff extends Shop {
	@Setting
	private Location<World> lever;
	@Setting
	private float price;
	
	static private String type = "DeviceOff";

	public DeviceOff() {
	}

	public DeviceOff(Player player, Location<World> sign) throws ExceptionInInitializerError {
		super(sign);
		if (!player.hasPermission("carrotshop.create.device"))
			throw new ExceptionInInitializerError(Lang.SHOP_PERM.replace("%type%", type));
		Stack<Location<World>> locations = ShopsData.getItemLocations(player);
		if (locations.isEmpty())
			throw new ExceptionInInitializerError(Lang.SHOP_LEVER.replace("%type%", type));
		BlockState targetBlock = locations.peek().getBlock();
		if (!targetBlock.getType().equals(BlockTypes.LEVER))
			throw new ExceptionInInitializerError(Lang.SHOP_LEVER.replace("%type%", type));

		lever = locations.peek();

		float cost = 0;
		if (CarrotShop.getEcoService() != null) {
			price = getPrice(sign);
			if (price < 0)
				throw new ExceptionInInitializerError(Lang.SHOP_PRICE);
			cost = ShopConfig.getNode("cost", type).getFloat(0);
			if (cost > 0) {
				UniqueAccount buyerAccount = CarrotShop.getEcoService().getOrCreateAccount(player.getUniqueId()).get();
				TransactionResult result = buyerAccount.withdraw(getCurrency(), BigDecimal.valueOf(cost), CarrotShop.getCause());
				if (result.getResult() != ResultType.SUCCESS)
					throw new ExceptionInInitializerError(Lang.SHOP_COST.replace("%type%", type).replace("%cost%", formatPrice(BigDecimal.valueOf(cost))));
			}
		}
		setOwner(player);
		ShopsData.clearItemLocations(player);
		if (cost > 0)
			player.sendMessage(Text.of(TextColors.DARK_GREEN, Lang.SHOP_DONE_COST.replace("%type%", type).replace("%cost%", formatPrice(BigDecimal.valueOf(cost)))));
		else
			player.sendMessage(Text.of(TextColors.DARK_GREEN, Lang.SHOP_DONE.replace("%type%", type)));
		done(player);
		info(player);
	}

	@Override
	public List<Location<World>> getLocations() {
		List<Location<World>> locations = super.getLocations();
		locations.add(lever);
		return locations;
	}

	@Override
	public void info(Player player) {
		if (CarrotShop.getEcoService() != null)
			player.sendMessage(Text.of(Lang.SHOP_DEVICEOFF_HELP.replace("%price%", formatPrice(price))));
		else
			player.sendMessage(Text.of(Lang.SHOP_DEVICEOFF_HELP_NOECON));
		update();
	}

	@Override
	public boolean trigger(Player player) {
		String recap = Lang.SHOP_DEVICEOFF_NOECON;
		String orecap = Lang.SHOP_DEVICE_OTHER_NOECON;
		if (CarrotShop.getEcoService() != null) {
			UniqueAccount buyerAccount = CarrotShop.getEcoService().getOrCreateAccount(player.getUniqueId()).get();
			UniqueAccount sellerAccount = CarrotShop.getEcoService().getOrCreateAccount(getOwner()).get();
			float tax = ShopConfig.getNode("taxes", type).getFloat(0);
			TransactionResult accountResult;
			if (tax > 0) {
				accountResult = buyerAccount.withdraw(getCurrency(), BigDecimal.valueOf(price), CarrotShop.getCause());
				if (accountResult.getResult() == ResultType.SUCCESS) {
					accountResult = sellerAccount.deposit(getCurrency(), BigDecimal.valueOf(price - price * tax / 100), CarrotShop.getCause());
				}

			} else {
				accountResult = buyerAccount.transfer(sellerAccount, getCurrency(), BigDecimal.valueOf(price), CarrotShop.getCause());
			}
			if (accountResult.getResult() != ResultType.SUCCESS) {
				player.sendMessage(Text.of(TextColors.DARK_RED, Lang.SHOP_MONEY));
				return false;
			}
			recap = Lang.SHOP_DEVICEOFF.replace("%price%", formatPrice(price));
			if (tax > 0)
				orecap = Lang.SHOP_DEVICE_OTHER_TAX.replace("%tax%", String.valueOf(tax)).replace("%tprice%", formatPrice(price - price * tax / 100));
			else
				orecap = Lang.SHOP_DEVICE_OTHER;
			orecap = orecap.replace("%price%", formatPrice(price));
		}

		lever.offer(Keys.POWERED, false);

		player.sendMessage(Text.of(recap));

		if (!CarrotShop.noSpam(getOwner())) {
			Optional<Player> seller = Sponge.getServer().getPlayer(getOwner());
			if (seller.isPresent())
				seller.get().sendMessage(Text.of(orecap.replace("%player%", player.getName()).replace("%type%", type)));
		}
		return true;
	}

}
