package eu.deltacraft.deltacraftteams.interfaces

import eu.deltacraft.deltacraftteams.types.ConnectionString

interface IDbConnector {

    fun getConnectionString(): ConnectionString;

}