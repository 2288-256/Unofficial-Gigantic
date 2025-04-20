package click.seichi.gigantic.message

import click.seichi.gigantic.config.Config
import click.seichi.gigantic.extension.warning
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object DiscordWebhookNotifier {
    private val webhookUrl: String? = Config.WEBHOOK_DISCORD_LEVEL_NOTIFICATION_URL

    fun sendLevelNotification(name: String, unlockLevel: Int) {
        if (webhookUrl == null) {
            warning("webhookUrlが存在しなかったため送信できませんでした。")
            return
        }
        val message = "<@&1322598582444101662>\\r【速報】${name}さんがLv${unlockLevel}を達成しました。\\rおめでとうございます！"
        val payload = "{\"content\": \"$message\"}"

        try {
            val url = URL(webhookUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("User-Agent", "SeichiHaruUnofficialPlugin (Ubuntu 20.04.6 LTS; Kotlin/1.5)")

            connection.outputStream.use { os ->
                val input = payload.toByteArray(StandardCharsets.UTF_8)
                os.write(input, 0, input.size)
            }

            connection.inputStream.use { it.readBytes() }
        } catch (e: Exception) {
            warning("Level達成通知が送信できませんでした。: ${e.message}")
        }
    }

    fun sendAllPartnerNotification(name: String) {
        if (webhookUrl == null) {
            warning("webhookUrlが存在しなかったため送信できませんでした。")
            return
        }
        val message = "<@&1346093737272410205>\\r【速報】${name}さんが全ての意思との関係を神友にしました。\\rおめでとうございます！"
        val payload = "{\"content\": \"$message\"}"

        try {
            val url = URL(webhookUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("User-Agent", "SeichiHaruUnofficialPlugin (Ubuntu 20.04.6 LTS; Kotlin/1.5)")

            connection.outputStream.use { os ->
                val input = payload.toByteArray(StandardCharsets.UTF_8)
                os.write(input, 0, input.size)
            }

            connection.inputStream.use { it.readBytes() }
        } catch (e: Exception) {
            warning("AllPartner通知が送信できませんでした。: ${e.message}")
        }
    }
}