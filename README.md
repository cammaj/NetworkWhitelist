# NetworkWhitelist

Efficient and lightweight whitelist plugin for Velocity networks.

-------------------------

## Features
- Global whitelist enforcement during `LoginEvent` with a customizable kick message.
- Simple command suite for managing the whitelist without restarting the proxy.
- Configuration stored in `config.yml` with automatic creation of defaults.
- Ability to add/remove players from the whitelist dynamically without any need for restarts and list reloads.
- Multiline kick messages supporting minecraft color & style codes and hex colors (&#XXXXXX) .

## Commands
- `/nwhitelist add <nick>` – add a player to the whitelist.
- `/nwhitelist remove <nick>` – remove a player from the whitelist.
- `/nwhitelist reload` – reload the configuration from disk.
- `/nwhitelist on|off` – enable or disable whitelist enforcement.

All commands require the `networkwhitelist.admin` permission and have the alias `/nwl`.

-------------------------

          Originally for KingdomCraft.pl | Licensed for public on Apache 2.0 | 2025 © by Cammaj 