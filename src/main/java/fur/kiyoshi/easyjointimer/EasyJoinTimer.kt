package fur.kiyoshi.easyjointimer

import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import java.time.Instant
import java.util.*

class EasyJoinTimer : JavaPlugin() {
    val playerTimes = mutableMapOf<UUID, Instant>()

    override fun onEnable() {
        server.pluginManager.registerEvents(PlayerJoinListener(), this)
        server.pluginManager.registerEvents(PlayerQuitListener(), this)
        if (server.pluginManager.isPluginEnabled("PlaceholderAPI")) {
            EasyJoinTimerPlaceholder(this).register()
        }
    }

    inner class PlayerJoinListener : Listener {
        @EventHandler
        fun onPlayerJoin(event: PlayerJoinEvent) {
            val player = event.player
            playerTimes[player.uniqueId] = Instant.now()
        }
    }

    inner class PlayerQuitListener : Listener {
        @EventHandler
        fun onPlayerQuit(event: PlayerQuitEvent) {
            val player = event.player
            val playerTime = playerTimes[player.uniqueId]
            if (playerTime != null) {
                val duration = Duration.between(playerTime, Instant.now())
                val hours = duration.toHours()
                val minutes = duration.toMinutes()
                val seconds = duration.seconds
                val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(player.uniqueId), listOf("%easyjointimer_timer%")).toString().replace("%easyjointimer_timer%", timeString)
                playerTimes.remove(player.uniqueId)
            }
        }
    }
}

class EasyJoinTimerPlaceholder(private val plugin: EasyJoinTimer) : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "easyjointimer"
    }

    override fun getAuthor(): String {
        return plugin.description.authors.joinToString()
    }

    override fun getVersion(): String {
        return plugin.description.version
    }

    override fun onPlaceholderRequest(player: Player, identifier: String): String? {
        val playerTime = plugin.playerTimes[player.uniqueId]
        val duration = Duration.between(playerTime, Instant.now())
        val hours = duration.toHours()
        val minutes = duration.toMinutes()
        val seconds = duration.seconds
        return when (identifier) {
            "timer" -> {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }
            "timer_hours_minutes" -> {
                String.format("%02d:%02d", hours, minutes)
            }
            "timer_formatted" -> {
                String.format("h%02d:m%02d:s%02d", hours, minutes, seconds)
            }
            "timer_formatted_hours_minutes" -> {
                String.format("h%02d:m%02d", hours, minutes)
            }
            else -> null
        }
    }
}