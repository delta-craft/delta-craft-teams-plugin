package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.StringJoiner
import java.util.HashMap
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil.getOutputStream
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil.getOutputStream
import org.json.JSONObject
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.stream.Collectors











class PlayerJoinAttemptListener(private val plugin: DeltaCraftTeams) : Listener {

    @EventHandler
    fun onPlayerAttemptJoin(playerJoinEvent:AsyncPlayerPreLoginEvent) {
        //URL("https://portal.deltacraft.eu/api/validate?name=Kubas445&uuid=4b715c2f-fd4d-32c9-bcb6-a70dceb93928").readText();

        val arguments = HashMap<String, String>()
        arguments["name"] = playerJoinEvent.name
        arguments["uuid"] = playerJoinEvent.uniqueId.toString()
        val sj = StringJoiner("&")
        for ((key, value) in arguments)
            sj.add(URLEncoder.encode(key, "UTF-8") + "="
                    + URLEncoder.encode(value, "UTF-8"))
        val out = sj.toString()//.getBytes(StandardCharsets.UTF_8)

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
            plugin.logger.info("Player: " + playerJoinEvent.name + " tried to join, but he does not accepted the rules.")
            playerJoinEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,"You have to accept the rules first!")
        }
    }
}