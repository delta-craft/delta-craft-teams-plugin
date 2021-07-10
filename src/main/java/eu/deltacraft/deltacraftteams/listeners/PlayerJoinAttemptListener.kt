package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
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

enum class ValidateError constructor(val value: String) {
    ArgumentsError("arguments_error"),
    MethodNotValid("method_not_valid"),
    MissingConsent("missing_consent"),
    MissingName ("missing_name"),
    UuidNotValid("uuid_not_valid"),
    NotRegistered("not_registered"),
    Unauthorized("unauthorized"),
    Unknown("unknown");

    companion object {
        fun from(findValue: String): ValidateError = ValidateError.values().first { it.value == findValue }
    }
}

class PlayerJoinAttemptListener(private val plugin: DeltaCraftTeams) : Listener {

    @EventHandler
    fun onPlayerAttemptJoin(playerJoinEvent:AsyncPlayerPreLoginEvent) {
        val arguments = HashMap<String, String>()
        arguments["name"] = playerJoinEvent.name
        arguments["uuid"] = playerJoinEvent.uniqueId.toString()
        val sj = StringJoiner("&")
        for ((key, value) in arguments)
            sj.add(URLEncoder.encode(key, "UTF-8") + "="
                    + URLEncoder.encode(value, "UTF-8"))
        val out = sj.toString()

        val url = URL("https://portal.deltacraft.eu/api/validate?"+out);
        val con = url.openConnection();
        val http = con as HttpURLConnection
        http.requestMethod = "GET"
        http.doOutput = true

        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        http.setRequestProperty("Authorization", "so@P8&Q67h@oENEaxryb&nhBb!47HrR?7J&FYDnm")
        http.connect()

        var br: BufferedReader? = null
        if (100 <= http.responseCode && http.responseCode <= 399) {
            br = BufferedReader(InputStreamReader(http.inputStream))
        } else {
            br = BufferedReader(InputStreamReader(http.errorStream))
        }

        val responseBody = br.lines().collect(Collectors.joining())
        val json = JSONObject(responseBody);
        if(!json.getBoolean("success")) {
            val errorsString = (json.getJSONArray("errors")).map { x -> x.toString() }

            val errors = errorsString.map { x -> ValidateError.from(x) }
            //val errors = emptyList<ValidateError>().toMutableList()
            /*for (e in errorsString) {
                if(ValidateError.values().any { it.name == e }) {
                    errors.add(ValidateError.values().first { it.name == e })
                }
            }*/

            for (e in errors) {
                when(e) {
                    ValidateError.NotRegistered -> {
                        plugin.logger.info("Player: ${playerJoinEvent.name} tried to join, but he does not accepted the rules.")
                        playerJoinEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text(""))
                        return
                    }
                }
            }
        }
    }
}