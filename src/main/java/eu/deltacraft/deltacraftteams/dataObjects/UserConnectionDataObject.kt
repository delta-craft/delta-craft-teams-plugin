package eu.deltacraft.deltacraftteams.dataObjects

import eu.deltacraft.deltacraftteams.interfaces.IDbConnector
import eu.deltacraft.deltacraftteams.types.DB
import eu.deltacraft.deltacraftteams.types.UserConnection
import java.lang.StringBuilder
import java.util.UUID

class UserConnectionDataObject(private val dbConnector: IDbConnector) {

    fun getByName(playerUid: UUID, playerName: String?): List<UserConnection> {
        val sb = StringBuilder(
            """
            SELECT
                id,
                team_id,
                uid,
                name
            FROM user_connections
            WHERE 
                uid = ?
        """
        )

        if (!playerName.isNullOrEmpty()) {
            sb.append(" OR name = ?")
        }

        val db = DB.getConnection(dbConnector) ?: return emptyList()

        val cmd = db.prepareStatement(sb.toString())

        cmd.setString(1, playerUid.toString())

        if (!playerName.isNullOrEmpty()) {
            cmd.setString(2, playerName)
        }

        val r = cmd.executeQuery()

        val res: MutableList<UserConnection> = mutableListOf()

        while (r.next()) {
            val id = r.getInt("id")
            val teamId = r.getInt("team_id")
            val uid = r.getString("uid")
            val username = r.getString("name")

            val uuid = if (uid.isNullOrEmpty()) null else UUID.fromString(uid)

            res.add(UserConnection(id, teamId, uuid, username))
        }

        r.close()
        cmd.close()

        return res
    }

    fun setUid(id: Int, playerUid: UUID): Boolean {
        val sb = StringBuilder(
            """
            UPDATE user_connections
                SET uid = ?
            WHERE id = ?
        """
        )

        val db = DB.getConnection(dbConnector) ?: return false

        val cmd = db.prepareStatement(sb.toString())

        cmd.setString(1, playerUid.toString())
        cmd.setInt(2, id)

        val res = cmd.executeUpdate()

        cmd.close()

        return res == 1
    }
}