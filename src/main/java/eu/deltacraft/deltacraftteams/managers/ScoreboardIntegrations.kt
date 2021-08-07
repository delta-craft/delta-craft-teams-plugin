package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.types.Team
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard

class ScoreboardIntegrations {

    companion object {

        fun registerPlayer(player: Player, team: Team) {
            registerPlayer(player.name, team)
        }

        private fun registerPlayer(playerName: String, team: Team) {
            val manager = Bukkit.getScoreboardManager()
            val board = manager.mainScoreboard

            val playerTeam = board.getEntryTeam(playerName)

            val sTeam = getTeam(board, team)

            if (playerTeam != null && playerTeam.name == sTeam.name) {
                return
            }

            if (sTeam.hasEntry(playerName)) {
                return
            }
            sTeam.addEntry(playerName)
        }

        private fun getTeam(board: Scoreboard, team: Team): org.bukkit.scoreboard.Team {
            return board.getTeam(team.name) ?: createNewTeam(board, team)
        }

        private fun createNewTeam(board: Scoreboard, team: Team): org.bukkit.scoreboard.Team {
            val teamName = team.name

            val sTeam = board.registerNewTeam(teamName)

            val text = Component.empty()
                .append(
                    Component.text(team.name, team.majorTeamEnum.color, TextDecoration.BOLD)
                )

            sTeam.displayName(text)
            sTeam.prefix(text.append(Component.text(" | ")))

            // TODO: Discuss
            // sTeam.setAllowFriendlyFire(false)

            sTeam.setCanSeeFriendlyInvisibles(true)

            return sTeam
        }

    }
}