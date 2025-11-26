package me.cammaj.networkwhitelist;

import com.velocitypowered.api.command.SimpleCommand;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
            invocation.source().sendMessage(Component.text(
                    "Whitelist is " + (config.isEnabled() ? "enabled" : "disabled") +
                            " | Players: " + String.join(", ", config.getPlayers())));
            return;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "reload" -> handleReload(invocation, args);
            case "on" -> handleToggle(invocation, true, args);
            case "off" -> handleToggle(invocation, false, args);
            case "add" -> handleAdd(invocation, args);
            case "remove" -> handleRemove(invocation, args);
            default -> sendUsage(invocation);
        }
    }

    private void handleReload(Invocation invocation, String[] args) {
        if (args.length != 1) {
            invocation.source().sendMessage(Component.text("Usage: /nwhitelist reload"));
            return;
        }

        configManager.loadConfig();
        invocation.source().sendMessage(Component.text("Reloaded whitelist configuration."));
//        logger.info("Whitelist configuration reloaded by " + invocation.source());
    }

    private void handleToggle(Invocation invocation, boolean enabled, String[] args) {
        if (args.length != 1) {
            invocation.source().sendMessage(Component.text("Usage: /nwhitelist " + (enabled ? "on" : "off")));
            return;
        }

        WhitelistConfig config = configManager.getConfig();
        if (config.isEnabled() == enabled) {
            invocation.source().sendMessage(Component.text(
                    "Whitelist is already " + (enabled ? "enabled." : "disabled.")));
            return;
        }

        config.setEnabled(enabled);
        configManager.saveConfig();
        invocation.source().sendMessage(Component.text("Whitelist " + (enabled ? "enabled." : "disabled.")));
//        logger.info("Whitelist " + (enabled ? "enabled" : "disabled") + " by " + invocation.source());
    }

    private void handleAdd(Invocation invocation, String[] args) {
        if (args.length != 2) {
            invocation.source().sendMessage(Component.text("Usage: /nwhitelist add <nick>"));
            return;
        }

        String target = args[1];
        WhitelistConfig config = configManager.getConfig();
        if (config.addPlayer(target)) {
            configManager.saveConfig();
            invocation.source().sendMessage(Component.text("Added " + target + " to the whitelist."));
//            logger.info("Added " + target + " to the whitelist by " + invocation.source());
        } else {
            invocation.source().sendMessage(Component.text(target + " is already whitelisted."));
        }
    }

    private void handleRemove(Invocation invocation, String[] args) {
        if (args.length != 2) {
            invocation.source().sendMessage(Component.text("Usage: /nwhitelist remove <nick>"));
            return;
        }

        String target = args[1];
        WhitelistConfig config = configManager.getConfig();
        if (config.removePlayer(target)) {
            configManager.saveConfig();
            invocation.source().sendMessage(Component.text("Removed " + target + " from the whitelist."));
//            logger.info("Removed " + target + " from the whitelist by " + invocation.source());
        } else {
            invocation.source().sendMessage(Component.text(target + " is not on the whitelist."));
        }
    }

    private void sendUsage(Invocation invocation) {
        invocation.source().sendMessage(Component.text(
                "Usage: /nwhitelist add <nick> | /nwhitelist remove <nick> | /nwhitelist reload | /nwhitelist on | /nwhitelist off"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("networkwhitelist.admin");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 0) {
            return Arrays.asList("add", "remove", "reload", "on", "off");
        }

        if (args.length == 1) {
            return Arrays.asList("add", "remove", "reload", "on", "off");
        }

        // Dalsze argumenty (np. nazwa gracza) zostawiamy klientowi / brak podpowiedzi
        return Collections.emptyList();
    }
}
