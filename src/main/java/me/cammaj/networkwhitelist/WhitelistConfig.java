package me.cammaj.networkwhitelist;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class WhitelistConfig {
    private boolean enabled;
    private String kickMessage;
    private final Set<String> players;

    public WhitelistConfig(boolean enabled, String kickMessage, Set<String> players) {
        this.enabled = enabled;
        this.kickMessage = kickMessage;
        this.players = new LinkedHashSet<>();
        for (String player : players) {
            this.players.add(normalize(player));
        }
    }

    public static WhitelistConfig defaultConfig() {
        Set<String> defaults = new LinkedHashSet<>();
        defaults.add("ExamplePlayer");
        return new WhitelistConfig(true, "You are not whitelisted on this network.", defaults);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }

    public Set<String> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public boolean addPlayer(String name) {
        return players.add(normalize(name));
    }

    public boolean removePlayer(String name) {
        return players.remove(normalize(name));
    }

    public boolean isWhitelisted(String name) {
        return players.contains(normalize(name));
    }

    public Set<String> getRawPlayers() {
        return players;
    }

    private static String normalize(String input) {
        return input.toLowerCase(Locale.ROOT);
    }
}
