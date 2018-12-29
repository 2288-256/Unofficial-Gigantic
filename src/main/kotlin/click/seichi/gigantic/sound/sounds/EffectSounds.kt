package click.seichi.gigantic.sound.sounds

import click.seichi.gigantic.sound.DetailedSound
import org.bukkit.Sound
import org.bukkit.SoundCategory

/**
 * @author tar0ss
 */
object EffectSounds {

    val EXPLOSION = DetailedSound(
            Sound.ENTITY_GENERIC_EXPLODE,
            SoundCategory.BLOCKS,
            0.1F,
            1.0F
    )

    val BLIZZARD = DetailedSound(
            Sound.BLOCK_GLASS_BREAK,
            SoundCategory.BLOCKS,
            0.1F,
            1.0F
    )

    val MAGIC = DetailedSound(
            Sound.ENTITY_CHICKEN_EGG,
            SoundCategory.BLOCKS,
            0.4F,
            1.5F
    )

    val FLAME = DetailedSound(
            Sound.ENTITY_BLAZE_SHOOT,
            SoundCategory.BLOCKS,
            0.4F,
            0.5F
    )

    val WITCH_SCENT = DetailedSound(
            Sound.ITEM_CHORUS_FRUIT_TELEPORT,
            SoundCategory.BLOCKS,
            0.4F,
            1.5F
    )

    val EXPEL = DetailedSound(
            Sound.ENTITY_VEX_AMBIENT,
            SoundCategory.BLOCKS,
            0.4F,
            1.2F
    )

}