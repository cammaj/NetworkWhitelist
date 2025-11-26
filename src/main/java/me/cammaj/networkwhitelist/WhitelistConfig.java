package me.cammaj.networkwhitelist;

import net.kyori.adventure.text.Component;

import java.util.*;

public final class WhitelistConfig {
    private boolean enabled;
    private final List<String> kickMessageLines;
    private final Set<String> players;

    public WhitelistConfig(boolean enabled, List<String> kickMessageLines, Set<String> players) {
        this.enabled = enabled;
        this.kickMessageLines = new ArrayList<>(kickMessageLines);
        this.players = players;
    }

    public static WhitelistConfig defaultConfig() {
        List<String> defaultLines = List.of("You are not whitelisted on this network.");
        return new WhitelistConfig(false, defaultLines, Set.of());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public List<String> getKickMessageLines() {
        return Collections.unmodifiableList(kickMessageLines);
    }

    public Component buildKickComponent() {
        return MessageUtil.buildKickMessage(kickMessageLines);
    }
}
