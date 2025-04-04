package click.seichi.gigantic.database.dao.user

import click.seichi.gigantic.database.table.user.UserMonsterBookTable
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

/**
 * @author 2288-256
 */
class UserMonsterBook(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, UserMonsterBook>(UserMonsterBookTable)

    var user by User referencedOn UserMonsterBookTable.userId

    var monsterId by UserMonsterBookTable.monsterId

    var encounterCount by UserMonsterBookTable.encounterCount

    var defeatCount by UserMonsterBookTable.defeatCount

    var firstEncounterDate by UserMonsterBookTable.firstEncounterDate

    var isAffinity by UserMonsterBookTable.isAffinity
}