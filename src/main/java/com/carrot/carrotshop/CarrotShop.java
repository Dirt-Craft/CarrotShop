package com.carrot.carrotshop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.carrot.carrotshop.api.ClaimApi;
import net.minecraftforge.fml.common.Loader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import com.carrot.carrotshop.command.NoSpamExecutor;
import com.carrot.carrotshop.command.ShopConfigCurrencyExecutor;
import com.carrot.carrotshop.command.ShopConfigExecutor;
import com.carrot.carrotshop.command.ShopConfigReloadExecutor;
import com.carrot.carrotshop.command.ShopImportExecutor;
import com.carrot.carrotshop.command.ShopMainExecutor;
import com.carrot.carrotshop.command.ShopReportExecutor;
import com.carrot.carrotshop.command.ShopServerReportExecutor;
import com.carrot.carrotshop.command.ShopWikiExecutor;
import com.carrot.carrotshop.command.element.CurrencyElement;
import com.carrot.carrotshop.listener.BlockBreakListener;
import com.carrot.carrotshop.listener.PlayerClickListener;
import com.carrot.carrotshop.listener.PlayerConnexionListener;
import com.google.inject.Inject;

@Plugin(id = "carrotshop", name = "CarrotShop", authors={"Carrot"}, url="https://github.com/TheoKah/CarrotShop")
public class CarrotShop {
	private File rootDir;

	private static CarrotShop plugin;

	@Inject
	private Logger logger;

	@Inject
	@ConfigDir(sharedRoot = true)
	private File defaultConfigDir;
	public static ClaimApi claimApi;

	private EconomyService economyService = null;

	private static List<UUID> noSpam = new ArrayList<UUID>();

	@Listener
	public void onInit(GameInitializationEvent event) throws IOException
	{
		plugin = this;

		rootDir = new File(defaultConfigDir, "carrotshop");

		if(Loader.isModLoaded("ftbutilities")){
			try {
				claimApi = Class.forName("com.carrot.carrotshop.api.ClaimApi$FtbClaimApi").asSubclass(ClaimApi.class).newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e){
				e.printStackTrace();
				claimApi = new ClaimApi.DummyClaimApi();
			}
		} else {
			claimApi = new ClaimApi.DummyClaimApi();
		}

		Lang.init(rootDir);
		ShopConfig.init(rootDir);
		ShopsLogs.init(rootDir);
		ShopsData.init(rootDir);
	}

	@Listener
	public void onStart(GameStartedServerEvent event)
	{
		Sponge.getServiceManager()
		.getRegistration(EconomyService.class)
		.ifPresent(prov -> economyService = prov.getProvider());

		ShopConfig.load();
		ShopsData.load();

		CommandSpec shopReport = CommandSpec.builder()
				.description(Text.of(Lang.HELP_DESC_CMD_REPORT))
				.executor(new ShopReportExecutor())
				.arguments(GenericArguments.optional(GenericArguments.user(Text.of("player"))))
				.build();
		
		CommandSpec shopServerReport = CommandSpec.builder()
				.description(Text.of(Lang.HELP_DESC_CMD_SREPORT))
				.executor(new ShopServerReportExecutor())
				.build();
		
		CommandSpec shopSpam = CommandSpec.builder()
				.description(Text.of(Lang.HELP_DESC_CMD_SPAM))
				.executor(new NoSpamExecutor())
				.build();
		
		CommandSpec shopWiki = CommandSpec.builder()
				.description(Text.of(Lang.HELP_DESC_CMD_WIKI))
				.executor(new ShopWikiExecutor())
				.build();
		
		CommandSpec shopConfigCurrency = CommandSpec.builder()
				.description(Text.of(Lang.HELP_DESC_CMD_CONFIG_CURRENCY))
				.permission("carrotshop.config.currency")
				.executor(new ShopConfigCurrencyExecutor())
				.arguments(GenericArguments.optional(new CurrencyElement(Text.of("currency"))))
				.build();
		
		CommandSpec shopConfigReload = CommandSpec.builder()
				.description(Text.of(Lang.HELP_DESC_CMD_CONFIG_RELOAD))
				.permission("carrotshop.config.reload")
				.executor(new ShopConfigReloadExecutor())
				.build();
		
		CommandSpec shopConfig = CommandSpec.builder()
				.description(Text.of(Lang.HELP_DESC_CMD_CONFIG))
				.executor(new ShopConfigExecutor())
				.child(shopConfigCurrency, "currency")
				.child(shopConfigReload, "reload")
				.build();
		
		CommandSpec shopImport = CommandSpec.builder()
				.description(Text.of(Lang.HELP_DESC_CMD_IMPORT))
				.permission("carrotshop.import")
				.executor(new ShopImportExecutor(defaultConfigDir))
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("plugin"))))
				.build();
		
		CommandSpec shopMain = CommandSpec.builder()
				.description(Text.of(Lang.HELP_DESC_CMD_MAIN))
				.executor(new ShopMainExecutor())
				.child(shopWiki, "help", "?", "wiki", "how", "howto", "h")
				.child(shopSpam, "hide", "shopchat", "stop", "off", "nospam", "spam", "toggle", "togglechat", "t")
				.child(shopReport, "report", "shopreport", "r")
				.child(shopServerReport, "serverreport", "server", "servreport", "sr")
				.child(shopConfig, "config")
				.child(shopImport, "import")
				.build();

		Sponge.getCommandManager().register(plugin, shopReport, "shopreport", "carrotshopreport", "cr", "sr", "carrotreport", "creport", "sreport");
		Sponge.getCommandManager().register(plugin, shopServerReport, "serverreport", "carrotshopserverreport", "shopserverreport", "carrotserverreport", "csr", "ssr", "csreport", "ssreport");
		Sponge.getCommandManager().register(plugin, shopSpam, "shophide", "hideshopchat", "carrotshophide", "carrothide", "shide", "chide", "sh", "ch");
		Sponge.getCommandManager().register(plugin, shopWiki, "shophelp", "carrotshopwiki", "shophelp", "carrotshopwiki", "cshophelp", "cshopwiki", "carrothelp", "carrotwiki", "shelp", "swiki");
		Sponge.getCommandManager().register(plugin, shopMain, "shop", "cs", "carrotshop", "s", "c");

		Sponge.getEventManager().registerListeners(this, new PlayerClickListener());
		Sponge.getEventManager().registerListeners(this, new BlockBreakListener());
		Sponge.getEventManager().registerListeners(this, new PlayerConnexionListener());
	}

	@Listener
	public void onStop(GameStoppingServerEvent event) {
		ShopsData.unload();
	}

	public static CarrotShop getInstance()
	{
		return plugin;
	}

	public static Logger getLogger()
	{
		return getInstance().logger;
	}

	public static EconomyService getEcoService()
	{
		return getInstance().economyService;
	}

	public static Cause getCause()
	{
		return Sponge.getCauseStackManager().getCurrentCause();
	}

	public static boolean noSpam(UUID uuid) {
		return noSpam.contains(uuid);
	}

	public static boolean toggleSpam(UUID uuid) {
		if (noSpam.contains(uuid))
			noSpam.remove(uuid);
		else
			noSpam.add(uuid);
		return noSpam.contains(uuid);
	}
}
