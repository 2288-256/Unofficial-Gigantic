package click.seichi.gigantic.relic

import click.seichi.gigantic.cache.key.Keys
import click.seichi.gigantic.extension.*
import click.seichi.gigantic.will.Will
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.block.Block
import org.bukkit.entity.Player

/**
 * @author tar0ss
 */
enum class WillRelic(
        val will: Will,
        val relic: Relic,
        val multiplier: Double,
        val material: Material
) {
    USED_COIN(Will.TERRA, Relic.USED_COIN, 0.6, Material.RED_TULIP) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.GOLD_ORE
        }
    },
    ADVENTURER_SOLE(Will.TERRA, Relic.ADVENTURER_SOLE, 0.3, Material.RED_TULIP) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.GRASS_BLOCK
        }
    },
    SUPER_MUSHROOM(Will.TERRA, Relic.SUPER_MUSHROOM, 0.2, Material.RED_TULIP) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.PLAINS
        }
    },
    RAINBOW_CLAY(Will.TERRA, Relic.RAINBOW_CLAY, 0.4, Material.RED_TULIP) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.CLAY
        }
    },
    CLAY_IMAGE(Will.TERRA, Relic.CLAY_IMAGE, 0.2, Material.RED_TULIP) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.BADLANDS
        }
    },
    MAMMMOTH_RAW_MEET(Will.TERRA, Relic.MAMMMOTH_RAW_MEET, 0.2, Material.RED_TULIP) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SNOWY_TAIGA
        }
    },
    CAT_SAND(Will.TERRA, Relic.CAT_SAND, 0.3, Material.RED_TULIP) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.COARSE_DIRT
        }
    },
    ULURU_SCRAP(Will.TERRA, Relic.ULURU_SCRAP, 0.2, Material.RED_TULIP) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.MODIFIED_BADLANDS_PLATEAU
        }
    },
    SPHINX(Will.TERRA, Relic.SPHINX, 0.2, Material.RED_TULIP) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.DESERT_LAKES
        }
    },
    SHELL(Will.AQUA, Relic.SHELL, 0.2, Material.TUBE_CORAL_FAN) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.BEACH
        }
    },
    SAIL(Will.AQUA, Relic.SAIL, 0.2, Material.TUBE_CORAL_FAN) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.DEEP_LUKEWARM_OCEAN
        }
    },
    DEEP_SEA_FISH_DIODE(Will.AQUA, Relic.DEEP_SEA_FISH_DIODE, 0.2, Material.TUBE_CORAL_FAN) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.DEEP_COLD_OCEAN
        }
    },
    SEICHI_MACKEREL(Will.AQUA, Relic.SEICHI_MACKEREL, 0.2, Material.TUBE_CORAL_FAN) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.OCEAN
        }
    },
    MUSH_FISH(Will.AQUA, Relic.MUSH_FISH, 0.2, Material.TUBE_CORAL_FAN) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.MUSHROOM_FIELDS
        }
    },
    STEERING_WHEEL(Will.AQUA, Relic.STEERING_WHEEL, 0.2, Material.TUBE_CORAL_FAN) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.WARM_OCEAN
        }
    },
    WOOD_SLAB(Will.AQUA, Relic.WOOD_SLAB, 0.2, Material.TUBE_CORAL_FAN) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.RIVER
        }
    },
    BROKEN_WATERMELON(Will.AQUA, Relic.BROKEN_WATERMELON, 0.9, Material.TUBE_CORAL_FAN) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.MELON
        }
    },
    CUTE_WATERING_POT(Will.AQUA, Relic.CUTE_WATERING_POT, 0.2, Material.TUBE_CORAL_FAN) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SUNFLOWER_PLAINS
        }
    },
    BEAST_BONE(Will.IGNIS, Relic.BEAST_BONE, 0.2, Material.POPPY) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SHATTERED_SAVANNA
        }
    },
    THIN_TOOTH(Will.IGNIS, Relic.THIN_TOOTH, 0.2, Material.POPPY) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SHATTERED_SAVANNA_PLATEAU
        }
    },
    BROKEN_IVORY(Will.IGNIS, Relic.BROKEN_IVORY, 0.2, Material.POPPY) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.BADLANDS_PLATEAU
        }
    },
    WILL_O_WISP(Will.IGNIS, Relic.WILL_O_WISP, 0.9, Material.POPPY) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.PUMPKIN
        }
    },
    BIG_FUNG(Will.IGNIS, Relic.BIG_FUNG, 0.2, Material.POPPY) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.DESERT
        }
    },
    CAMP_FIRE_TRACE(Will.IGNIS, Relic.CAMP_FIRE_TRACE, 0.2, Material.POPPY) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.TAIGA_HILLS
        }
    },
    DESERT_CRYSTAL(Will.IGNIS, Relic.DESERT_CRYSTAL, 0.2, Material.POPPY) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.DESERT_HILLS
        }
    },
    CARNIVORE_BONE(Will.IGNIS, Relic.CARNIVORE_BONE, 0.2, Material.POPPY) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SAVANNA
        }
    },
    FRIED_POTATO(Will.IGNIS, Relic.FRIED_POTATO, 0.1, Material.POPPY) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.temperature > 1.5
        }
    },
    BROKEN_BOW(Will.AER, Relic.BROKEN_BOW, 0.1, Material.AZURE_BLUET) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.y in 63..84
        }
    },
    TIME_CAPSEL(Will.AER, Relic.TIME_CAPSEL, 0.2, Material.AZURE_BLUET) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.FLOWER_FOREST
        }
    },
    BROKEN_FORCE_FLAG(Will.AER, Relic.BROKEN_FORCE_FLAG, 0.8, Material.AZURE_BLUET) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.isMossy
        }
    },
    BITTEN_LEATHER_BOOT(Will.AER, Relic.BITTEN_LEATHER_BOOT, 0.2, Material.AZURE_BLUET) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SAVANNA_PLATEAU
        }
    },
    BROKEN_LEAD(Will.AER, Relic.BROKEN_LEAD, 0.2, Material.AZURE_BLUET) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.WOODED_HILLS
        }
    },
    OLD_AXE(Will.AER, Relic.OLD_AXE, 0.2, Material.AZURE_BLUET) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.TAIGA
        }
    },
    VODKA_BOTTLE(Will.AER, Relic.VODKA_BOTTLE, 0.2, Material.AZURE_BLUET) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.COLD_OCEAN
        }
    },
    ACID_GEAR(Will.AER, Relic.ACID_GEAR, 0.2, Material.AZURE_BLUET) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.JUNGLE_HILLS
        }
    },
    SLICED_ROPE(Will.AER, Relic.SLICED_ROPE, 0.2, Material.AZURE_BLUET) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.TAIGA_MOUNTAINS
        }
    },
    WEB_SCRAP(Will.AER, Relic.WEB_SCRAP, 0.2, Material.AZURE_BLUET) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.LUKEWARM_OCEAN
        }
    },
    CACAO_WATERMELON(Will.NATURA, Relic.CACAO_WATERMELON, 0.2, Material.OAK_SAPLING) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.JUNGLE
        }
    },
    SUMMER_DAY(Will.NATURA, Relic.SUMMER_DAY, 0.1, Material.OAK_SAPLING) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.temperature in 1.0..1.5
        }
    },
    BIRCH_MUSHROOM(Will.NATURA, Relic.BIRCH_MUSHROOM, 0.2, Material.OAK_SAPLING) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.BIRCH_FOREST
        }
    },
    EGGPLANT(Will.NATURA, Relic.EGGPLANT, 0.2, Material.OAK_SAPLING) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.WOODED_MOUNTAINS
        }
    },
    WHITE_FLOWER(Will.NATURA, Relic.WHITE_FLOWER, 0.2, Material.OAK_SAPLING) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SNOWY_TUNDRA
        }
    },
    FROSTED_PINECONE(Will.NATURA, Relic.FROSTED_PINECONE, 0.2, Material.OAK_SAPLING) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.GIANT_TREE_TAIGA
        }
    },
    BANANA_SKIN(Will.NATURA, Relic.BANANA_SKIN, 0.2, Material.OAK_SAPLING) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.MODIFIED_JUNGLE
        }
    },
    INSECT_HORN(Will.NATURA, Relic.INSECT_HORN, 0.2, Material.OAK_SAPLING) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.TALL_BIRCH_FOREST
        }
    },
    BROWN_SAP(Will.NATURA, Relic.BROWN_SAP, 0.2, Material.OAK_SAPLING) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.TALL_BIRCH_HILLS
        }
    },
    DOWN_TREE(Will.NATURA, Relic.DOWN_TREE, 0.2, Material.OAK_SAPLING) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SNOWY_TAIGA_MOUNTAINS
        }
    },
    CRYSTAL_OF_SNOW(Will.GLACIES, Relic.CRYSTAL_OF_SNOW, 0.1, Material.BLUE_ORCHID) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.temperature in -0.5..0.15
        }
    },
    FROSTED_FISH(Will.GLACIES, Relic.FROSTED_FISH, 0.2, Material.BLUE_ORCHID) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.FROZEN_RIVER
        }
    },
    NOT_MELTTED_ICE(Will.GLACIES, Relic.NOT_MELTTED_ICE, 0.1, Material.BLUE_ORCHID) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.temperature < -0.5
        }
    },
    FROSTED_CRAFTBOX(Will.GLACIES, Relic.FROSTED_CRAFTBOX, 0.2, Material.BLUE_ORCHID) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SNOWY_BEACH
        }
    },
    SNOW_AMBER(Will.GLACIES, Relic.SNOW_AMBER, 0.2, Material.BLUE_ORCHID) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SNOWY_MOUNTAINS
        }
    },
    ICICLE(Will.GLACIES, Relic.ICICLE, 0.1, Material.BLUE_ORCHID) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome.isSnowy
        }
    },
    FROSTED_WHEEL(Will.GLACIES, Relic.FROSTED_WHEEL, 0.2, Material.BLUE_ORCHID) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.GIANT_TREE_TAIGA_HILLS
        }
    },
    SOFT_RIME(Will.GLACIES, Relic.SOFT_RIME, 0.3, Material.BLUE_ORCHID) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.ICE_SPIKES
        }
    },
    CRAMPONS(Will.GLACIES, Relic.CRAMPONS, 0.2, Material.BLUE_ORCHID) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SNOWY_TAIGA_HILLS
        }
    },
    A_PIECE_OF_CHALK(Will.SOLUM, Relic.A_PIECE_OF_CHALK, 0.1, Material.QUARTZ) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.y in 30..62
        }
    },
    BROKEN_TRAP(Will.SOLUM, Relic.BROKEN_TRAP, 0.9, Material.QUARTZ) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.isInfested
        }
    },
    FLUX_FOSSIL(Will.SOLUM, Relic.FLUX_FOSSIL, 0.2, Material.QUARTZ) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.FROZEN_OCEAN
        }
    },
    FROSTED_ORE(Will.SOLUM, Relic.FROSTED_ORE, 0.2, Material.QUARTZ) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.FROSTED_ICE
        }
    },
    MYCELIUM_PICKAXE(Will.SOLUM, Relic.MYCELIUM_PICKAXE, 0.2, Material.QUARTZ) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.MUSHROOM_FIELD_SHORE
        }
    },
    BEAUTIFUL_ORE(Will.SOLUM, Relic.BEAUTIFUL_ORE, 3.5, Material.QUARTZ) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.EMERALD_ORE
        }
    },
    IRON_ARMOR(Will.SOLUM, Relic.IRON_ARMOR, 0.4, Material.QUARTZ) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.IRON_ORE
        }
    },
    INDIGO(Will.SOLUM, Relic.INDIGO, 0.3, Material.QUARTZ) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.COAL_ORE
        }
    },
    DIAMOND_STONE(Will.SOLUM, Relic.DIAMOND_STONE, 2.4, Material.QUARTZ) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.DIAMOND_ORE
        }
    },
    RUSTED_COMPASS(Will.VENTUS, Relic.RUSTED_COMPASS, 0.1, Material.LILAC) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome.isOcean
        }
    },
    BEAUTIFUL_WING(Will.VENTUS, Relic.BEAUTIFUL_WING, 0.1, Material.LILAC) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.y > 85
        }
    },
    TUMBLEWEED(Will.VENTUS, Relic.TUMBLEWEED, 0.1, Material.LILAC) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome.isDried
        }
    },
    HORN(Will.VENTUS, Relic.HORN, 0.1, Material.LILAC) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome.isWormed
        }
    },
    SYLPH_LEAFE(Will.VENTUS, Relic.SYLPH_LEAFE, 0.2, Material.LILAC) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.FOREST
        }
    },
    TENT_CLOTH(Will.VENTUS, Relic.TENT_CLOTH, 0.2, Material.LILAC) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SNOWY_MOUNTAINS
        }
    },
    PRICKLE(Will.VENTUS, Relic.PRICKLE, 0.2, Material.LILAC) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.MODIFIED_WOODED_BADLANDS_PLATEAU
        }
    },
    WING(Will.VENTUS, Relic.WING, 0.2, Material.LILAC) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.MODIFIED_GRAVELLY_MOUNTAINS
        }
    },
    NIDUS_AVIS(Will.VENTUS, Relic.NIDUS_AVIS, 0.2, Material.LILAC) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.WOODED_BADLANDS_PLATEAU
        }
    },
    ELIXIR(Will.LUX, Relic.ELIXIR, 0.1, Material.DANDELION) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.temperature in 0.15..1.0
        }
    },
    OLD_MESSAGE_BOTTLE(Will.LUX, Relic.OLD_MESSAGE_BOTTLE, 0.2, Material.DANDELION) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.STONE_SHORE
        }
    },
    WHITE_APPLE(Will.LUX, Relic.WHITE_APPLE, 0.2, Material.DANDELION) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.BIRCH_FOREST_HILLS
        }
    },
    BUDDHIST_STATUE(Will.LUX, Relic.BUDDHIST_STATUE, 0.2, Material.DANDELION) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.MOUNTAINS
        }
    },
    TREASURE_CASKET(Will.LUX, Relic.TREASURE_CASKET, 0.2, Material.DANDELION) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.DEEP_OCEAN
        }
    },
    JIZO(Will.LUX, Relic.JIZO, 0.2, Material.DANDELION) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.GRAVELLY_MOUNTAINS
        }
    },
    LIGHTNING_MOSS(Will.LUX, Relic.LIGHTNING_MOSS, 0.2, Material.DANDELION) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.GIANT_SPRUCE_TAIGA_HILLS
        }
    },
    RED_DUST(Will.LUX, Relic.RED_DUST, 1.4, Material.DANDELION) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.REDSTONE_ORE
        }
    },
    BLUE_DUST(Will.LUX, Relic.BLUE_DUST, 1.4, Material.DANDELION) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.LAPIS_ORE
        }
    },
    BEAST_HORN(Will.UMBRA, Relic.BEAST_HORN, 0.1, Material.ALLIUM) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome.isCold
        }
    },
    BOTTLED_LIQUID(Will.UMBRA, Relic.BOTTLED_LIQUID, 0.2, Material.ALLIUM) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SWAMP
        }
    },
    SLIME_LEES(Will.UMBRA, Relic.SLIME_LEES, 0.2, Material.ALLIUM) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.SWAMP_HILLS
        }
    },
    PURPLE_CHEESE(Will.UMBRA, Relic.PURPLE_CHEESE, 0.14, Material.ALLIUM) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.type == Material.MYCELIUM
        }
    },
    SHADE_ARMOR(Will.UMBRA, Relic.SHADE_ARMOR, 0.2, Material.ALLIUM) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.DARK_FOREST_HILLS
        }
    },
    ORICHALCUM(Will.UMBRA, Relic.ORICHALCUM, 0.1, Material.ALLIUM) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.y < 29
        }
    },
    DARK_MATTER(Will.UMBRA, Relic.DARK_MATTER, 0.2, Material.ALLIUM) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.GIANT_SPRUCE_TAIGA
        }
    },
    BLACK_CLOTH(Will.UMBRA, Relic.BLACK_CLOTH, 0.2, Material.ALLIUM) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.DEEP_FROZEN_OCEAN
        }
    },
    BLOODSTAINED_SWORD(Will.UMBRA, Relic.BLOODSTAINED_SWORD, 0.3, Material.ALLIUM) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.ERODED_BADLANDS
        }
    },
    RLYEH_TEXT(Will.UMBRA, Relic.RLYEH_TEXT, 0.2, Material.ALLIUM) {
        override fun isBonusTarget(block: Block): Boolean {
            return block.biome == Biome.DARK_FOREST
        }
    },


    ;

    abstract fun isBonusTarget(block: Block): Boolean

    fun calcMultiplier(player: Player) = player.getOrPut(Keys.RELIC_MAP[relic]!!).times(multiplier)

}