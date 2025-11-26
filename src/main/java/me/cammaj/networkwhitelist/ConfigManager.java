package me.cammaj.networkwhitelist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
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
                    } else {
                        Files.createFile(configPath);
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
    }

    public void saveConfig() {
        if (config == null) {
            return;
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("enabled", config.isEnabled());
        data.put("kick-message", config.getKickMessage());
        data.put("players", config.getRawPlayers());

        try (OutputStream out = Files.newOutputStream(configPath)) {
            yaml.dump(data, new OutputStreamWriter(out, StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to save whitelist configuration", e);
        }
    }

    public WhitelistConfig getConfig() {
        return config;
    }

    private WhitelistConfig parseConfig(Map<String, Object> data) {
        if (data == null) {
            return WhitelistConfig.defaultConfig();
        }

        boolean enabled = Boolean.TRUE.equals(data.get("enabled"));
        String kickMessage = stringOrDefault(data.get("kick-message"), "You are not whitelisted on this network.");
        Set<String> players = new LinkedHashSet<>();

        Object playersRaw = data.get("players");
        if (playersRaw instanceof Iterable<?> iterable) {
            for (Object entry : iterable) {
                if (entry != null) {
                    players.add(entry.toString());
                }
            }
        }

        if (players.isEmpty()) {
            players.add("ExamplePlayer");
        }

        return new WhitelistConfig(enabled, kickMessage, players);
    }

    private String stringOrDefault(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }
}
