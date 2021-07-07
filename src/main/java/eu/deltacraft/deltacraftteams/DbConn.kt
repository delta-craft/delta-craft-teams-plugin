package eu.deltacraft.deltacraftteams

import eu.deltacraft.deltacraftteams.interfaces.IDbConnector
import eu.deltacraft.deltacraftteams.types.DB

class DbConn(private val plugin: DeltaCraftTeams, private val dbConnector: IDbConnector) {

    fun getUsers(): String {
        val db = DB.getConnection(dbConnector) ?: return ""

        val sql = "SELECT * FROM user_connections"

        val stmt = db.prepareStatement(sql)

        val res = stmt.executeQuery()

        if (res.next()) {
            return res.getString("uid")
        }

        return ""
    }
}
