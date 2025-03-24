package click.seichi.gigantic.monster

import org.joda.time.DateTime

/**
 * @author 2288-256
 */
class MonsterBookClient (
    var monsterId: Int,
    var encounterCount: Long,
    var defeatCount: Long,
    var firstEncounterDate: DateTime,
    var isEligible: Boolean
)