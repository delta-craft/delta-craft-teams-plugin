package eu.deltacraft.deltacraftteams

import org.jetbrains.exposed.sql.Table
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object NextauthUsers: Table() {
    val id = integer("id")
    val name = varchar("name", length = 255)

    override val primaryKey = PrimaryKey(id);
}

class DbConn(private val plugin: DeltaCraftTeams) {

    private val dbName = ""
    private val uName = ""
    private val pwd = ""
    private val host = ""

    private fun getConn(): Connection? {
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://$host:3306/$dbName",
                uName,
                pwd
            )

        } catch ( ex: SQLException) {

        }
        return null;
    }

    fun getUsers(): String {
        val db = getConn() ?: return "";

        val sql = "SELECT * FROM nextauth_users";

        val stmt = db.prepareStatement(sql)

        val res = stmt.executeQuery()

        if (res.next()) {
            return res.getString("name")
        }

        return ""
    }
}