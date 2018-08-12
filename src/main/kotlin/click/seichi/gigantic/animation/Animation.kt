package click.seichi.gigantic.animation

import click.seichi.gigantic.Gigantic
import click.seichi.gigantic.schedule.Scheduler
import io.reactivex.Observable
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.concurrent.TimeUnit

/**
 * [Location] に 視覚的なエフェクトを表示する
 *
 * @param ticks 継続時間(ticks)
 * @param rendering tick毎の処理
 *
 * @author tar0ss
 */
class Animation(
        private val ticks: Long,
        private val rendering: (Location, Long) -> Unit
) {

    fun start(location: Location) {
        Observable.interval(50L, TimeUnit.MILLISECONDS)
                .observeOn(Scheduler(Gigantic.PLUGIN, Bukkit.getScheduler()))
                .take(ticks)
                .subscribe {
                    val ticks = it
                    rendering(location, ticks)
                }
    }
}