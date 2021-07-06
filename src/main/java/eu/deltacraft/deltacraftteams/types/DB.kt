package eu.deltacraft.deltacraftteams.types

import eu.deltacraft.deltacraftteams.interfaces.IDbConnector
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DB {
    companion object {

        @JvmStatic
        fun getConnection(dbConnector: IDbConnector): Connection? {
            return getConnection(dbConnector.getConnectionString())
        }

        @JvmStatic
        fun getConnection(connectionString: ConnectionString): Connection? {
            try {
                return DriverManager.getConnection(
                    connectionString.host,
                    connectionString.login,
                    connectionString.password
                )
            } catch (ex: SQLException) {

            }
            return null
        }
    }
}