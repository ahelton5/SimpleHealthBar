# SimpleHealthBar

A Paper plugin that shows live health bars above players and mobs. No hologram plugin, no scoreboard hacks — it's all done with packets, so the real entities on the server are never touched.

## Why packets?

The obvious way to do this is `setCustomName()` on the entity itself, but that has a nasty side effect: naming a mob makes it persistent, so it stops despawning naturally. Instead, SimpleHealthBar spawns a fake, client-side-only `TextDisplay` for each viewer and mounts it as a passenger on the real entity. The client handles following the entity on its own (walking, jumping, falling — no extra packets needed), and since nothing about the real entity ever changes, despawn behavior, persistence, everything stays exactly as vanilla intended.

## Features

- Players always show their health to nearby players
- Mobs show their health when damaged recently, or when you're looking directly at them within reach
- Distance-based visibility, configurable
- Several bar styles: row of hearts (scaled to max health or fixed at 10), short text, percentage, or a color-shifting single heart
- Per-player toggle if someone doesn't want to see bars at all
- Config-driven — no rebuilding to tweak distances, timers, or styles

## Building

Requires JDK 25 (this targets a recent Minecraft version that needs it). Everything else — PacketEvents, EntityLib — gets bundled into the final jar via Shadow, so you don't need to install anything separately on your server.

```
./gradlew build
```

Output jar lands in `build/libs/`. Drop it in `plugins/` like any other plugin.

## Config

First run generates `plugins/SimpleHealthBar/config.yml`. Worth knowing:

- `show-distance` — how far away bars are visible (this is a box check, not a true sphere, so diagonal distances can be a bit generous)
- `damage-visible-seconds` — how long a mob's bar sticks around after taking damage
- `bar-style` — one of `HEARTS_SCALED`, `HEARTS_FIXED_10`, `SHORT`, `PERCENTAGE`, `GRADIENT_SHORT`
- `enable-player-bars` / `enable-mob-bars` — toggle each independently
- `excluded-entity-types` — entity types that never get a bar (armor stands are excluded by default, since they technically count as living entities and would otherwise pick one up)

## Commands

```
/SimpleHealthBar self      spawn a bar above your own head, for testing
/SimpleHealthBar toggle    turn all bars on/off for yourself
/SimpleHealthBar killall   wipe every active bar (op only)
/SimpleHealthBar debug     dump what the server thinks you're currently tracking
```

## Known limitations

- Distance checks are box-based, not spherical — mentioned above, low priority to fix
- No true half-heart rendering in the heart styles, since it's one glyph doing the work of what vanilla splits into full/half/empty icon states
- A sneaking player's vanilla nametag disappears client-side but the bar's offset doesn't account for it, so there's a small visual gap while sneaking
- Mob bars don't get cleaned up on cross-dimension mob transfers the same way player world-changes are handled — the periodic refresh catches it within half a second regardless, so it's mostly invisible in practice

## Why EntityLib on top of PacketEvents

Raw entity metadata packets are just indexed byte arrays under the hood, and those indices shift between Minecraft versions. EntityLib wraps that in typed classes (`TextDisplayMeta` and friends) so bumping Minecraft versions doesn't mean re-deriving magic numbers by hand.
