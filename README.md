# Vein Miner

A Fabric mod for Minecraft 26.3 that adds a **Vein Miner** enchantment for pickaxes. Break one ore block and the rest of the connected vein comes with it.

## Features

- Mines every touching block of the same ore, including diagonal connections.
- Works like normal mining: Fortune, Silk Touch, Unbreaking, durability, hunger and XP all apply.
- Only triggers when your pickaxe can actually harvest the ore.
- **Sneak** to mine a single block as normal.
- Capped at 64 extra blocks per vein to avoid lag.
- Stops one use before your pickaxe would break.
- Works with vanilla ores and modded ores tagged `c:ores`.

## Getting the enchantment

Vein Miner (max level I) is a rare enchantment for pickaxes, available from the enchanting table, on enchanted books and through anvils.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.3.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) and put it in your `mods` folder.
3. Download the Vein Miner jar from the [Releases](../../releases) page and put it in your `mods` folder.

The mod is required on the server. In singleplayer it just needs to be in your `mods` folder.

## Building from source

Requires JDK 25.

```
./gradlew build
```

The jar is written to `build/libs/`.

## License

CC0-1.0
