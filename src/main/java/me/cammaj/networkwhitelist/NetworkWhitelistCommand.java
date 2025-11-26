package me.cammaj.networkwhitelist;

import com.velocitypowered.api.command.SimpleCommand;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;

public final class NetworkWhitelistCommand implements SimpleCommand {
    private final ConfigManager configManager;
    private final Logger logger;

    public NetworkWhitelistCommand(ConfigManager configManager, Logger logger) {
        this.configManager = configManager;
        this.logger = logger;
    }

    @Override
    public void execute(Invocation invocation) {
        WhitelistConfig config = configManager.getConfig();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            invocation.source().sendMessage(Component.text("Whitelist is " + (config.isEnabled() ? "enabled" : "disabled")
                    + " | Players: " + String.join(", ", config.getPlayers())));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(invocation);
            case "on" -> handleToggle(invocation, true);
            case "off" -> handleToggle(invocation, false);
            case "add" -> handleAdd(invocation, args);
            case "remove" -> handleRemove(invocation, args);
            default -> sendUsage(invocation);
        }
    }

    private void handleReload(Invocation invocation) {
        configManager.loadConfig();
        invocation.source().sendMessage(Component.text("Reloaded whitelist configuration."));
        logger.info("Whitelist configuration reloaded by " + invocation.source());
    }

    private void handleToggle(Invocation invocation, boolean enabled) {
        WhitelistConfig config = configManager.getConfig();
        config.setEnabled(enabled);
        configManager.saveConfig();
        invocation.source().sendMessage(Component.text("Whitelist " + (enabled ? "enabled." : "disabled.")));
    }

    private void handleAdd(Invocation invocation, String[] args) {
        if (args.length < 2) {
            sendUsage(invocation);
            return;
        }

        WhitelistConfig config = configManager.getConfig();
        if (config.addPlayer(args[1])) {
            configManager.saveConfig();
            invocation.source().sendMessage(Component.text("Added " + args[1] + " to the whitelist."));
        } else {
            invocation.source().sendMessage(Component.text(args[1] + " is already whitelisted."));
        }
    }

    private void handleRemove(Invocation invocation, String[] args) {
        if (args.length < 2) {
            sendUsage(invocation);
            return;
        }

        WhitelistConfig config = configManager.getConfig();
        if (config.removePlayer(args[1])) {
            configManager.saveConfig();
            invocation.source().sendMessage(Component.text("Removed " + args[1] + " from the whitelist."));
        } else {
            invocation.source().sendMessage(Component.text(args[1] + " is not on the whitelist."));
        }
    }

    private void sendUsage(Invocation invocation) {
        invocation.source().sendMessage(Component.text("Usage: /nwhitelist add/remove <nick> | /nwhitelist reload | /nwhitelist on/off"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("networkwhitelist.admin");
    }

    @Override
    public java.util.List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 0) {
            return Arrays.asList("add", "remove", "reload", "on", "off");
        }

        if (args.length == 1) {
            return Arrays.asList("add", "remove", "reload", "on", "off");
        }

        return Collections.emptyList();
    }
}
