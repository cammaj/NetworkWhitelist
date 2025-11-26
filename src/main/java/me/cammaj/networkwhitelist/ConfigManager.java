package me.cammaj.networkwhitelist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;

public final class ConfigManager {
    private final Path dataDirectory;
    private final Logger logger;
    private final Path configPath;
    private final Yaml yaml = new Yaml();
    private WhitelistConfig config;

    public ConfigManager(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.configPath = dataDirectory.resolve("config.yml");
    }

    public void loadConfig() {
        try {
            if (Files.notExists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            if (Files.notExists(configPath)) {
                try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                    if (in != null) {
                        Files.copy(in, configPath);
                        logger.info("Created default NetworkWhitelist config.yml in " + configPath.toAbsolutePath());
                    } else {
                        Files.createFile(configPath);
                        logger.warning("Created empty NetworkWhitelist config.yml because default resource was not found.");
                    }
                }
            }

            try (InputStream in = Files.newInputStream(configPath)) {
                Map<String, Object> data = yaml.load(in);
                this.config = parseConfig(data);
            }
        } catch (IOException | RuntimeException e) {
            logger.log(Level.SEVERE, "Unable to load whitelist configuration, using defaults.", e);
            this.config = WhitelistConfig.defaultConfig();
        }

        if (this.config == null) {
            logger.warning("Whitelist configuration was null after loading, falling back to defaults.");
            this.config = WhitelistConfig.defaultConfig();
        }
    }

    public void saveConfig() {
        if (config == null) {
            logger.warning("Attempted to save null whitelist configuration; skipping save.");
            return;
        }

        try {
            if (Files.notExists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
            if (Files.notExists(configPath)) {
                Files.createFile(configPath);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to prepare path for whitelist configuration", e);
            return;
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("enabled", config.isEnabled());
        data.put("kick-message", new ArrayList<>(config.getKickMessageLines()));
        data.put("players", config.getRawPlayers());

        try (OutputStream out = Files.newOutputStream(configPath);
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to save whitelist configuration", e);
        }
    }

    public WhitelistConfig getConfig() {
        if (config == null) {
            logger.warning("Whitelist configuration requested before loading; returning defaults.");
            config = WhitelistConfig.defaultConfig();
        }
        return config;
    }

    private WhitelistConfig parseConfig(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            logger.warning("Whitelist configuration file is empty or null; using defaults.");
            return WhitelistConfig.defaultConfig();
        }

        Object enabledRaw = data.get("enabled");
        boolean enabled;
        if (enabledRaw instanceof Boolean b) {
            enabled = b;
        } else if (enabledRaw instanceof String s) {
            enabled = Boolean.parseBoolean(s);
        } else {
            enabled = false;
        }

        List<String> kickLines = parseKickLines(data.get("kick-message"));

        Set<String> players = new LinkedHashSet<>();
        Object playersRaw = data.get("players");
        if (playersRaw instanceof Iterable<?> iterable) {
            for (Object entry : iterable) {
                if (entry != null) {
                    players.add(entry.toString());
                }
            }
        } else if (playersRaw instanceof String single) {
            players.add(single);
        }

        if (players.isEmpty()) {
            logger.info("Whitelist players list is empty; plugin will start with an empty whitelist.");
        }

        return new WhitelistConfig(enabled, kickLines, players);
    }

    private List<String> parseKickLines(Object value) {
        List<String> lines = new ArrayList<>();
        if (value == null) {
            lines.add("You are not whitelisted on this network.");
            return lines;
        }

        if (value instanceof Iterable<?> iterable) {
            for (Object o : iterable) {
                if (o != null) {
                    String s = o.toString();
                    if (!s.isEmpty()) {
                        lines.add(s);
                    }
                }
            }
        } else {
            String s = value.toString();
            if (!s.isEmpty()) {
                lines.add(s);
            }
        }

        if (lines.isEmpty()) {
            lines.add("You are not whitelisted on this network.");
        }
        return lines;
    }
}