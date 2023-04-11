@file:Suppress("KDocUnresolvedReference", "unused", "SpellCheckingInspection")

package fur.kiyoshi.easyjointimer

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import java.time.Instant
import java.util.*

class EasyJoinTimer : JavaPlugin(), Listener {

    /**
     * @author MyNameIsKiyoshi
     * @see <a href="https://github.com/MyNameIsKiyoshi/EasyJoinTimer"
     * @param playerTimer
     * @param playerId
     * @param joinTime
     * @return true if the player has joined
     * @throws IllegalStateException
     */

    private val playerTimer = HashMap<UUID, Instant>()

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        playerTimer[event.player.uniqueId] = Instant.now()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val playerId = event.player.uniqueId
        val joinTime = playerTimer[playerId]
        if (joinTime != null) {
            val playTime = Duration.between(joinTime, Instant.now())
            val hours = playTime.toHours()
            val minutes = playTime.toMinutes() % 60
            val seconds = playTime.seconds % 60
            val playtimeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            PlaceholderAPI.setPlaceholders(event.player, "%easyjointimer_player_${event.player.name}%:$playtimeString")
            playerTimer.remove(playerId)
        }
    }
}