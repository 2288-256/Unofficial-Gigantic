package click.seichi.gigantic.ranking

import click.seichi.gigantic.database.table.ranking.RankingScoreTable
import org.jetbrains.exposed.sql.selectAll
import java.util.*

/**
 * @author tar0ss
 */
class Ranking(val score: Score) {

    var rankMap = mapOf<Int, UUID>()
        private set

    private var uuidMap = mapOf<UUID, Int>()

    private var valueMap = mapOf<UUID, Long>()

    fun contains(uniqueId: UUID) = uuidMap.contains(uniqueId)

    fun getValue(uniqueId: UUID) = valueMap.getValue(uniqueId)

    fun getRank(uniqueId: UUID) = uuidMap.getValue(uniqueId)

    fun findUUID(rank: Int) = rankMap[rank]

    // transaction内で実行すること
    fun update() {
        val _rankMap = mutableMapOf<Int, UUID>()
        val _uuidMap = mutableMapOf<UUID, Int>()
        val _valueMap = mutableMapOf<UUID, Long>()
        RankingScoreTable.selectAll()
                .notForUpdate()
                .orderBy(score.column to score.sortOrder)
                .limit(10000)
                .forEachIndexed { rank, row ->
                    val uniqueId = row[RankingScoreTable.id].value
                    val value = row[score.column]
                    _rankMap[rank] = uniqueId
                    _uuidMap[uniqueId] = rank
                    _valueMap[uniqueId] = value
                }
        rankMap = _rankMap
        uuidMap = _uuidMap
        valueMap = _valueMap
    }

}