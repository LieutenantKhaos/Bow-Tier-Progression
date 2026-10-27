# Vanilla Bow Tiers — Fabric, Minecraft 26.2

Version: 0.3.3-rc.4

Adds vanilla-style material tiers for bows and arrows, Sharpshooter I–IV,
and optional built-in Bray/Enderite and Even Better Enchants resource packs.

## Install

Use Java 25, Minecraft 26.2, Fabric Loader 0.19.3 or later, and
Fabric API 0.156.0+26.2. Replace the previous Vanilla Bow Tiers JAR
with vanilla-bow-tiers-0.3.3-rc.4.jar in your mods folder.
Do not leave two versions installed together.

Enderite Mod is optional. Enderite arrow smithing loads only when it is installed.
Enable the desired VBT compatibility packs in Minecraft's resource-pack screen,
above the resource packs they adapt. Those packs require the corresponding
original resource pack/mod. Standalone compatibility ZIPs are not needed.

## Bow stats

| Tier | Durability | Damage multiplier | Velocity multiplier | Draw speed multiplier |
|---|---:|---:|---:|---:|
| Copper | 384 | 1.00 | 1.03 | 1.00 |
| Iron | 500 | 1.10 | 1.05 | 1.05 |
| Gold | 192 | 1.00 | 1.12 | 1.30 |
| Diamond | 1024 | 1.20 | 1.12 | 1.10 |
| Netherite | 1536 | 1.35 | 1.18 | 1.15 |

Arrow damage multipliers: copper 1.05, iron 1.50, gold 1.10, diamond 1.50,
netherite 1.75, Enderite 2.50. Bow and arrow multipliers combine.
These are projectile base-damage multipliers; final damage also depends on
speed, critical hits, enchantments, armor, and other Minecraft mechanics.

Sharpshooter reduces spread by 25% per level and retains 75%, 50%, 25%,
or 10% of normal arrow gravity at levels I–IV respectively.

## Skeletons

Regular Skeletons retain the supplied source's equipment rates: 20% copper,
15% iron, 7% gold, 3% diamond, and 55% vanilla. Wither Skeletons retain
10% gold, 5% diamond, 1% netherite, and otherwise their vanilla equipment.
Tiered bows have the existing separate 2% drop roll.

Ammo is selected once and stored on the held bow, including across reloads:

| Bow | Ammo probabilities |
|---|---|
| Vanilla Skeleton bow | 80% flint, 20% iron |
| Copper | Copper |
| Iron | 40% flint, 60% iron |
| Gold | 20% flint, 40% iron, 40% gold |
| Diamond | 40% iron, 30% gold, 30% diamond |
| Netherite | 35% iron, 30% gold, 25% diamond, 10% netherite |

Enderite ammo is excluded. Other skeleton variants retain their ordinary
behavior unless explicitly equipped with a VBT illusion bow.

## Build

Java 25 and Gradle 9.5.1; Fabric Loom is pinned to 1.17.21.

Windows: `gradlew.bat build`
Other systems: `./gradlew build`

Output: `build/libs/vanilla-bow-tiers-0.3.3-rc.4.jar`.

See CHANGELOG.txt for the fixes and VALIDATION.txt for the checks and limits
of this release. In-game behavior and resource-pack appearance still require
manual playtesting, especially with other mods enabled.