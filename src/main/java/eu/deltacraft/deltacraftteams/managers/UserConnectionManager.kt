package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.types.UserConnection
import org.bukkit.OfflinePlayer
import java.util.UUID

class UserConnectionManager() {

    fun get(player: OfflinePlayer): UserConnection? {
        return get(player.uniqueId, player.name)
    }

    private fun get(uid: UUID, playerName: String?): UserConnection? {
        // TODO: From API
        return null
        /*  val dataObject = UserConnectionDataObject(dbConnector)
          val connections = dataObject.getByName(uid, playerName)

          if (connections.isEmpty()) {
              return null
          }

          val withUid = connections.firstOrNull { x -> x.uuid == uid }
          if (withUid != null) {
              return withUid
          }

          val withName = connections.firstOrNull { x -> x.name.equals(playerName, true) } ?: return null

          if (dataObject.setUid(withName.id, uid)) {
              withName.uuid = uid
          } else {
              // TODO: Inform about error
          }

          return withName*/

    }

}