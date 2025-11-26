# NetworkWhitelist

Efficient and lightweight whitelist plugin for Velocity networks.

## Features
- Global whitelist enforcement during `LoginEvent` with a customizable kick message.
- Simple command suite for managing the whitelist without restarting the proxy.
- Configuration stored in `config.yml` with automatic creation of defaults.

## Commands
- `/nwhitelist add <nick>` – add a player to the whitelist.
- `/nwhitelist remove <nick>` – remove a player from the whitelist.
- `/nwhitelist reload` – reload the configuration from disk.
- `/nwhitelist on|off` – enable or disable whitelist enforcement.

All commands require the `networkwhitelist.admin` permission and have the alias `/nwl`.

## Building
This project now uses Maven with Java 21. To build a shaded plugin jar, run:

```bash
mvn clean package
```

The output jar will be placed in `target/networkwhitelist-1.0.0.jar`.
