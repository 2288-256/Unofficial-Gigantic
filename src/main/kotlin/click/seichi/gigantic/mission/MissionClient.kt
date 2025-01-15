package click.seichi.gigantic.mission

import click.seichi.gigantic.extension.info
import org.bukkit.entity.Player
import org.joda.time.DateTime

/**
 * @author 2288-256
 */
class MissionClient(
    var missionId: Int,
    var missionType: Int,
    var missionReqSize: Int?,
    var progress: Int,
    var complete: Boolean,
    var date: DateTime
)