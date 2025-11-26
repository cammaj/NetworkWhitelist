package me.cammaj.networkwhitelist;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.regex.Pattern;

public final class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final Pattern MC_HEX_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");

    private MessageUtil() {
    }

    private static String normalizeLine(String line) {
        if (line == null) {
            return "";
        }

        // § -> &
        String normalized = line.replace('§', '&');

        // minecraftowy hex &#rrggbb -> MiniMessage <#rrggbb>
        normalized = MC_HEX_PATTERN.matcher(normalized)
                .replaceAll("<#$1>");

        // &-kody -> tagi MiniMessage
        return normalized
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&l", "<bold>")
                .replace("&n", "<underlined>")
                .replace("&o", "<italic>")
                .replace("&m", "<strikethrough>")
                .replace("&r", "<reset>");
    }

    public static Component buildKickMessage(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return Component.text("You are not whitelisted on this network.");
        }

        String joined = String.join("\n",
                lines.stream()
                        .map(MessageUtil::normalizeLine)
                        .toList()
        );

        return MINI_MESSAGE.deserialize(joined);
    }
}