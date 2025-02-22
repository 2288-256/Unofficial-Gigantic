package click.seichi.gigantic.mission

import org.joda.time.DateTime

/**
 * @author 2288-256
 */
class MissionClient(
    var missionId: Int,
    var missionType: Int,
    var missionDifficulty: Int,
    var missionReqSize: Int?,
    var missionReqBlock: Int?,
    var progress: Double,
    var complete: Boolean,
    var rewardReceived: Boolean,
    var date: DateTime
)