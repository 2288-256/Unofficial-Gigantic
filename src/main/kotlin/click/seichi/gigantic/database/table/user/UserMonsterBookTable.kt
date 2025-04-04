package click.seichi.gigantic.database.table.user

import org.jetbrains.exposed.dao.IntIdTable

/**
 * @author 2288-256
 */
object UserMonsterBookTable : IntIdTable("users_monster_books") {

    val userId = reference("unique_id", UserTable).primaryKey()

    val monsterId = integer("monster_id").primaryKey()

    val encounterCount = long("encounter_count").default(0L)

    val defeatCount = long("defeat_count").default(0L)

    val firstEncounterDate = datetime("first_encounter_date")

    val isAffinity = bool("is_eligible").default(false)
}