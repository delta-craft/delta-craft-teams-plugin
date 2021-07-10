package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.utils.enums.ValidateError
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.StringJoiner
import java.util.HashMap
import org.json.JSONObject
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.stream.Collectors

class PlayerJoinAttemptListener(private val plugin: DeltaCraftTeams) : Listener {

    @EventHandler
    fun onPlayerAttemptJoin(playerJoinEvent: AsyncPlayerPreLoginEvent) {
        val arguments = HashMap<String, String>()
        arguments["name"] = playerJoinEvent.name
        arguments["uuid"] = playerJoinEvent.uniqueId.toString()
        val sj = StringJoiner("&")
        for ((key, value) in arguments)
            sj.add(
                URLEncoder.encode(key, "UTF-8") + "="
                        + URLEncoder.encode(value, "UTF-8")
            )
        val out = sj.toString()

        val url = URL("https://portal.deltacraft.eu/api/validate?$out")
        val con = url.openConnection()
        val http = con as HttpURLConnection
        http.requestMethod = "GET"
        http.doOutput = true

        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        http.setRequestProperty("Authorization", "so@P8&Q67h@oENEaxryb&nhBb!47HrR?7J&FYDnm")
        http.connect()

        val br = if (http.responseCode in 100..399) {
            BufferedReader(InputStreamReader(http.inputStream))
        } else {
            BufferedReader(InputStreamReader(http.errorStream))
        }

        val responseBody = br.lines().collect(Collectors.joining())
        val json = JSONObject(responseBody)
        if (!json.getBoolean("success")) {
            val errorsString = (json.getJSONArray("errors")).map { x -> x.toString() }
            val errors = errorsString.map { x -> ValidateError.from(x) }

            var message = "Unkown error"
            for (e in errors) {
                message = when (e) {
                    ValidateError.NotRegistered -> "You have to be registered!"
                    ValidateError.MissingConsent -> "You have to accept our consent!"
                    else -> "Server error"
                }
                break
            }

            plugin.logger.info("Player ${playerJoinEvent.name} tried to join, but error occurred: \"$message\"")
            playerJoinEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text(message))
        }
    }
}