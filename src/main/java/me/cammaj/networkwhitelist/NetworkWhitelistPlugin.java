package me.cammaj.networkwhitelist;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;

@Plugin(id = "networkwhitelist", name = "NetworkWhitelist", version = "1.0.0", authors = {"cammaj"})
public final class NetworkWhitelistPlugin {
    private final ProxyServer server;
    private final Logger logger;
    private final ConfigManager configManager;

    @Inject
    public NetworkWhitelistPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.configManager = new ConfigManager(dataDirectory, logger);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        configManager.loadConfig();
        registerCommands(server.getCommandManager());
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        WhitelistConfig config = configManager.getConfig();
        if (config == null || !config.isEnabled()) {
            return;
        }

        String username = event.getPlayer().getUsername();
        if (!config.isWhitelisted(username)) {
            event.setResult(LoginEvent.ComponentResult.denied(Component.text(config.getKickMessage())));
        }
    }

    private void registerCommands(CommandManager commandManager) {
        commandManager.register(
                commandManager.metaBuilder("nwhitelist").aliases("nwl").build(),
                new NetworkWhitelistCommand(configManager, logger));
    }
}
